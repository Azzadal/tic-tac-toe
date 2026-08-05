package org.azzadal.main.repository;

import org.azzadal.main.model.Session;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SessionRepository extends ReactiveCrudRepository<Session, String> {
}
