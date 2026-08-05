package org.azzadal.main.repository;

import org.azzadal.main.model.TicTacCount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TicTacRepository
  extends ReactiveCrudRepository<TicTacCount, Long> {}
