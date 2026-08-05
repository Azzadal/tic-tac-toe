package org.azzadal.main.model;

import org.springframework.data.annotation.Id;

public class TicTacCount {

  @Id
  private Long id;

  private String name;
  private String winner;
}
