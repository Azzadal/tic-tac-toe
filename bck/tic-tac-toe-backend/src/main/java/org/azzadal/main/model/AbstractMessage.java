package org.azzadal.main.model;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbstractMessage {

  protected String type;
  protected Object data;

  public AbstractMessage(String type, Supplier<Object> supplier) {
    this.type = type;
    this.data = supplier.get();
  }
}
