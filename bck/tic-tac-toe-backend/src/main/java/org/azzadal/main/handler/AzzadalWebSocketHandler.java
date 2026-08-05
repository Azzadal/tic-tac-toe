package org.azzadal.main.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.azzadal.main.model.AbstractMessage;
import org.azzadal.main.model.Field;
import org.azzadal.main.model.Message;
import org.azzadal.main.model.Session;
import org.azzadal.main.service.SessionService;
import org.azzadal.main.service.TicTacService;
import org.azzadal.main.service.WebSocketSessionManager;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

public class AzzadalWebSocketHandler implements WebSocketHandler {

  private final SessionService sessionService;
  private final WebSocketSessionManager webSocketSessionManager;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ConcurrentHashMap<String, Field> sessions;
  private final AtomicReference<Set<Session>> sessions2 = new AtomicReference<>(
          new HashSet<>()
  );
  private final ConcurrentHashMap<String, Set<UUID>> players = new ConcurrentHashMap<>();
  private final AtomicReference<Set<String>> finalizeGame = new AtomicReference<>(
    new HashSet<>()
  );
  private final TicTacService ticTacService;

  public AzzadalWebSocketHandler(
          SessionService sessionService, WebSocketSessionManager webSocketSessionManager,
          TicTacService ticTacService
  ) {
      this.sessionService = sessionService;
      this.webSocketSessionManager = webSocketSessionManager;
    this.ticTacService = ticTacService;
    this.sessions = sessionService.getActiveSessionInMemory();
  }

  @Override
  public @NotNull Mono<Void> handle(WebSocketSession session) {
    String query = session.getHandshakeInfo().getUri().getQuery();
    String[] params = query.split("&");
    System.out.println("params " + Arrays.toString(params));

    String parsedSid = parseSid(query);
    UUID uuid = parseUuid(query);

    // рабочий идентификатор сессии
    String sid = parsedSid != null ? parsedSid : session.getId();

    Mono<Void> initSession = session.send(
      Mono
        .just(new AbstractMessage("SID", () -> sid))
        .handle(this::serializableMessage)
        .map(session::textMessage)
    );

    System.out.println("sid " + sid + "\n" + "uuid " + uuid);

    Set<UUID> uuids = players.computeIfAbsent(sid, k -> new HashSet<>(2));
    uuids.add(uuid);
    UUID another = this.getAnotherPlayerUuid(sid, uuid);

    Flux<AbstractMessage> individualStream = this.getIndividualStream(uuid);
    Flux<AbstractMessage> anotherPlayerStream = this.getIndividualStream(another);

    // поток для всех игроков в сессии
    Flux<AbstractMessage> broadcastStream = webSocketSessionManager.getStream(
      sid
    );

    Mono<Void> currentField = session.send(
      Mono
        .just(new AbstractMessage("FIELD", () -> sessionService.initSession(sid)))
        .handle(this::serializableMessage)
        .map(session::textMessage)
    );

    Mono<Void> broadcast = session.send(
      broadcastStream
        .<String>handle((message, sink) -> {
          try {
            sink.next(objectMapper.writeValueAsString(message));
          } catch (JsonProcessingException e) {
            sink.error(new RuntimeException(e));
          }
        })
        .map(session::textMessage)
    );

    Mono<Void> individual = session.send(
      individualStream
        .<String>handle((message, sink) -> {
          try {
            sink.next(objectMapper.writeValueAsString(message));
          } catch (JsonProcessingException e) {
            sink.error(new RuntimeException(e));
          }
        })
        .map(session::textMessage)
    );

    Mono<Void> anotherMono = session.send(
      anotherPlayerStream
        .<String>handle((message, sink) -> {
          try {
            sink.next(objectMapper.writeValueAsString(message));
          } catch (JsonProcessingException e) {
            sink.error(new RuntimeException(e));
          }
        })
        .map(session::textMessage)
    );

    Mono<Void> incoming = messageProcessor(session, sid, uuid).then();

    return Mono
      .when(
        initSession,
        currentField,
        incoming,
        broadcast,
        individual,
        anotherMono
      )
      .then(this.onClose(session, uuid));
  }

  private Flux<AbstractMessage> messageProcessor(
    WebSocketSession session,
    String sid,
    UUID activePlayer
  ) {
    return session
      .receive()
      .flatMap(incoming -> {
        Set<String> finalizedSession = finalizeGame.get();
        System.out.println(
                "Завершеннные игры " + finalizedSession + ' ' + sid + ' ' + sid
        );
        if (finalizedSession.contains(sid)) {
          System.out.println("Игра уже завершена " + sid);
          return Mono.error(new ResponseStatusException(HttpStatus.LOCKED, "Игра уже завершена"));
        }
        Publisher<?> mono = Mono.empty();
        UUID anotherPlayerUuid = this.getAnotherPlayerUuid(sid, activePlayer);
        try {
          Message mapped = objectMapper.readValue(
            incoming.getPayloadAsText(),
            Message.class
          );
          System.out.println(mapped);
          String type = mapped.getType();
          if (type.equals("ACTION")) {
            System.out.println("пришел запрос на действие " + anotherPlayerUuid);

            Field field = sessionService.initSession(sid);
            if (anotherPlayerUuid != null) {
              webSocketSessionManager.sendToIndividual(anotherPlayerUuid, new Message("LOCK"));
            }
            mono =
              ticTacService
                .actionProcess(field, mapped, activePlayer)
                .doOnNext(r -> {
                  if (r.getType().equals("WIN")) {
                    finalizeGame.get().add(sid);
                  }
                });
          }
        } catch (JsonProcessingException | InterruptedException e) {
          return Mono.just(new AbstractMessage("ERROR", e::getMessage));
        }
        return Flux.concat(mono);
      })
      .cast(AbstractMessage.class)
      .doOnNext(result -> {
        webSocketSessionManager.sendToAll(sid, result);
      });
  }

  private UUID parseUuid(String query) {
    Matcher matcher = Pattern.compile("uuid=([0-9a-fA-F-]+)").matcher(query);
    return matcher.find() ? UUID.fromString(matcher.group(1)) : null;
  }

  private String parseSid(String query) {
    Matcher matcher = Pattern.compile("sid=([^;]+)").matcher(query);
    return matcher.find() ? matcher.group(1) : null;
  }

  private void serializableMessage(
    AbstractMessage am,
    SynchronousSink<String> sink
  ) {
    try {
      sink.next(objectMapper.writeValueAsString(am));
    } catch (JsonProcessingException e) {
      sink.next("{\"type\":\"ERROR\",\"data\":\"Serialization failed\"}");
    }
  }

  private Mono<Void> onClose(WebSocketSession session, UUID uuid) {
    return session
      .closeStatus()
      .doOnNext(v -> {
        webSocketSessionManager.removeIndividualSink(uuid);
        System.out.printf("disconnect %s...\n", session.getId());
      })
      .then();
  }

  private UUID getAnotherPlayerUuid(String sid, UUID activePlayer) {
    UUID another = null;
    Set<UUID> uuids = players.get(sid);
    for (UUID id : uuids) {
      if (id != null && !id.equals(activePlayer)) {
        another = id;
      }
    }
    return another;
  }

  private Flux<AbstractMessage> getIndividualStream(UUID uuid) {
    return uuid != null
            ? webSocketSessionManager.getIndividualStream(uuid)
            : Flux.empty();
  }
}
