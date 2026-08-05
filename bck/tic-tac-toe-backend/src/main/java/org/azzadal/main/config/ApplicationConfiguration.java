package org.azzadal.main.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import reactor.core.publisher.Mono;

@Configuration
public class ApplicationConfiguration { //        extends AbstractR2dbcConfiguration

  @Value("${spring.r2dbc.url}")
  private String url;
  //    @Override
  //    @Bean
  //    public @NotNull ConnectionFactory connectionFactory() {
  //        return ConnectionFactoryBuilder.withUrl(ConnectionFactoryOptions.builder()
  //                .option()).build();
  ////        new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
  ////                .host("localhost")
  ////                .port(5432)  // optional, defaults to 5432
  ////                .username("postgres")
  ////                .password("1234")
  ////                .database("reactor")  // optional
  ////                .build());
  //    }
  //
  //    @Bean
  //    public Mono<PostgresqlConnection> connection() {
  //        System.out.println("Connection...");
  //        return ((PostgresqlConnectionFactory)this.connectionFactory()).create();
  //    }
}
