package org.azzadal.main.service;

import org.azzadal.main.enumeration.GameStatus;
import org.azzadal.main.events.LobbyEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LobbyManager {

    private final Map<String, LobbyEvent> activeGames = new ConcurrentHashMap<>();

    private final Sinks.Many<LobbyEvent> lobbySink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<LobbyEvent> createGame(String gameId, String creatorName) {
        return Mono.fromSupplier(() -> {
            LobbyEvent event = new LobbyEvent(gameId, creatorName, GameStatus.CREATED, 1);
            activeGames.put(gameId, event);
            lobbySink.tryEmitNext(event); // оповестить всех подписчиков
            return event;
        });
    }

    public Mono<Void> removeGame(String gameId) {
        return Mono.fromRunnable(() -> {
            LobbyEvent removed = activeGames.remove(gameId);
            if (removed != null) {
                lobbySink.tryEmitNext(new LobbyEvent(gameId, "", GameStatus.FINISHED, 0));
            }
        });
    }

    public Flux<LobbyEvent> getLobbyStream() {
        Flux<LobbyEvent> currentGames = Flux.fromIterable(activeGames.values());
        Flux<LobbyEvent> updates = lobbySink.asFlux();
        return Flux.concat(currentGames, updates);
    }
}
