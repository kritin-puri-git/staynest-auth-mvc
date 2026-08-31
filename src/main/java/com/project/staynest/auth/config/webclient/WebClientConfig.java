package com.project.staynest.auth.config.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("generalWebclient")
    public WebClient webclient(){
        return WebClient.builder().build();
    }
}

//
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.resources.ConnectionProvider;
//
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean("generalWebclient")
//    public WebClient webClient() {
//
//        ConnectionProvider connectionProvider =
//                ConnectionProvider.builder("staynest-load-test")
//
//                        // Maximum simultaneously active connections
//                        .maxConnections(10_000)
//
//                        // Unlimited pending requests waiting for a connection
//                        .pendingAcquireMaxCount(-1)
//
//                        // Wait indefinitely for a free connection
//                        .pendingAcquireTimeout(Duration.ofHours(1))
//
//                        // Close idle connections
//                        .maxIdleTime(Duration.ofMinutes(5))
//
//                        // Maximum lifetime of a connection
//                        .maxLifeTime(Duration.ofMinutes(30))
//
//                        // Background cleanup
//                        .evictInBackground(Duration.ofMinutes(2))
//
//                        .build();
//
//        HttpClient httpClient =
//                HttpClient.create(connectionProvider)
//
//                        // Connection timeout
//                        .option(
//                                ChannelOption.CONNECT_TIMEOUT_MILLIS,
//                                30_000
//                        )
//
//                        // Enable HTTP Keep-Alive
//                        .keepAlive(true)
//
//                        // Compress request/response if supported
//                        .compress(true)
//
//                        // Response timeout
//                        .responseTimeout(Duration.ofMinutes(2))
//
//                        // Read / Write timeout
//                        .doOnConnected(connection ->
//                                connection
//
//                                        .addHandlerLast(
//                                                new ReadTimeoutHandler(
//                                                        2,
//                                                        TimeUnit.MINUTES
//                                                )
//                                        )
//
//                                        .addHandlerLast(
//                                                new WriteTimeoutHandler(
//                                                        2,
//                                                        TimeUnit.MINUTES
//                                                )
//                                        )
//                        );
//
//        return WebClient.builder()
//                .clientConnector(
//                        new ReactorClientHttpConnector(httpClient)
//                )
//                .build();
//    }
//}