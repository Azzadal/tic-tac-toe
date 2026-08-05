package org.azzadal.main.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.azzadal.main.model.AbstractMessage;
import org.azzadal.main.model.Field;
import org.azzadal.main.model.Message;
import org.azzadal.main.repository.TicTacRepository;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Обработка хода игрока.
 */
@Service
public class TicTacService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TicTacRepository ticTacRepository;

  public TicTacService(TicTacRepository ticTacRepository) {
    this.ticTacRepository = ticTacRepository;
  }

  public Flux<AbstractMessage> actionProcess(
    Field field,
    Message message,
    UUID uuid
  ) throws InterruptedException {
    field.insert(message.getData().getPosition(), message.getData().getValue());
    boolean win = field.isWin(message.getData().getPosition());
    if (win) {
      System.out.println("win");
      return Flux.concat(
        Mono.just(new AbstractMessage("WIN", () -> uuid)),
        Mono.just(new AbstractMessage("FIELD", () -> field))
      );
    } else {
      System.out.println("not win");
      return Flux.just(new AbstractMessage("FIELD", () -> field));
    }
  }
}
