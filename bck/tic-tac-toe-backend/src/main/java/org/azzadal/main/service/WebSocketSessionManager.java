package org.azzadal.main.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.azzadal.main.model.AbstractMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class WebSocketSessionManager {

  private final Map<String, Sinks.Many<AbstractMessage>> sinks = new ConcurrentHashMap<>();
  private final Map<UUID, Sinks.Many<AbstractMessage>> individualSinks = new ConcurrentHashMap<>();

  public Flux<AbstractMessage> getStream(String sid) {
    return sinks
      .computeIfAbsent(sid, k -> Sinks.many().replay().latest())
      .asFlux();
  }

  public Flux<AbstractMessage> getIndividualStream(UUID uuid) {
    System.out.println("геттинг индивидуал стрим " + uuid);
    return individualSinks
      .computeIfAbsent(
        uuid,
        k -> Sinks.many().multicast().onBackpressureBuffer()
      )
      .asFlux();
  }

  public void sendToAll(String sid, AbstractMessage message) {
    Sinks.Many<AbstractMessage> sink = sinks.get(sid);
    if (sink != null) {
      System.out.println("sending... " + sid);
      sink.tryEmitNext(message);
    } else {
      System.out.println("No active sink for sid: " + sid);
    }
  }

  public void sendToIndividual(UUID uuid, AbstractMessage message) {
    Sinks.Many<AbstractMessage> sink = individualSinks.get(uuid);
    if (sink != null) {
      System.out.println("sending individual... " + uuid);
      sink.tryEmitNext(message);
    } else {
      System.out.println("No active sink for uuid: " + uuid);
    }
  }

  public void removeIndividualSink(UUID uuid) {
    System.out.println("remove individual sink " + uuid);
    individualSinks.remove(uuid);
  }
}
