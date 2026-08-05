package org.azzadal.main.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.azzadal.main.handler.AzzadalWebSocketHandler;
import org.azzadal.main.service.SessionService;
import org.azzadal.main.service.TicTacService;
import org.azzadal.main.service.WebSocketSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

@Configuration
public class SocketConfig implements WebFluxConfigurer {

  @Bean
  public HandlerMapping handlerMapping(
    SessionService sessionService,
    WebSocketSessionManager webSocketSessionManager,
    TicTacService ticTacService
  ) {
    System.out.println(ticTacService);
    Map<String, WebSocketHandler> map = new HashMap<>();
    map.put(
      "/tictactoe",
      new AzzadalWebSocketHandler(sessionService, webSocketSessionManager, ticTacService)
    );
    int order = -1;

    return new SimpleUrlHandlerMapping(map, order);
  }

  @Override
  public WebSocketService getWebSocketService() {
    ReactorNettyRequestUpgradeStrategy strategy = new ReactorNettyRequestUpgradeStrategy();
    return new HandshakeWebSocketService(strategy);
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter(
      Objects.requireNonNull(getWebSocketService())
    );
  }
}
