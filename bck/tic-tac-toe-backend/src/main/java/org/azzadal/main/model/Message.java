package org.azzadal.main.model;

import java.util.UUID;
import java.util.function.Supplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.azzadal.main.enumeration.Value;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Message extends AbstractMessage {

  private MessageData data;

  public Message(String type) {
    super(type, () -> null);
    this.type = type;
  }

  @Data
  public static class MessageData {

    UUID playerId;
    Position position;
    Value value;
  }
}
