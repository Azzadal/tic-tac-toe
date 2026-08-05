package org.azzadal.main.events;

import org.azzadal.main.enumeration.GameStatus;

public record LobbyEvent(
        String gameId,
        String creatorName,
        GameStatus status,
        int playersCount
) {}
