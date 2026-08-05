package org.azzadal.main.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Tic-tac-toe session.
 */
@Getter
@Setter
public class Session {
    @Id
    private Long id;

    private String sessionId;

    private String playerOne;

    private String playerTwo;
}
