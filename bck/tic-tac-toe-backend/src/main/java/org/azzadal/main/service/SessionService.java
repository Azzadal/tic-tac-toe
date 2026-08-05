package org.azzadal.main.service;

import lombok.RequiredArgsConstructor;
import org.azzadal.main.model.Field;
import org.azzadal.main.model.Session;
import org.azzadal.main.repository.SessionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final ConcurrentHashMap<String, Field> sessionsInMemory = new ConcurrentHashMap<>();
    private final SessionRepository sessionRepository;

    public Mono<Session> get(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public ConcurrentHashMap<String, Field> getActiveSessionInMemory() {
        return sessionsInMemory;
    }

    public Field initSession(String sid) {
        System.out.println("Init...");
        return this.sessionsInMemory.computeIfAbsent(sid, k -> new Field());
    }

    public Mono<Session> save(Session session) {
        return sessionRepository.save(session);
    }

}
