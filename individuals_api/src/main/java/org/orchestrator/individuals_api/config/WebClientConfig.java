package org.orchestrator.individuals_api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${webclient.timeout.connection}")
    private int connectionTimeout;

    @Value("${webclient.timeout.response}")
    private int responseTimeout;

    @Value("${webclient.timeout.read}")
    private int readTimeout;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofMillis(responseTimeout))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                );
    }
}
