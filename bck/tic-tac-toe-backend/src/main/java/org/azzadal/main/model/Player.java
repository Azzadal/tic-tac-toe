package org.azzadal.main.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Player {

  @Id
  private Long id;

  private String uuid;

  private String name;
}
