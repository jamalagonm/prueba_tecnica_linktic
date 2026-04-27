package com.microservicio.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class ApiKeyGatewayFilter implements GlobalFilter, Ordered {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Value("${app.security.api-key}")
    private String apiKey;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestApiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);

        if (apiKey.equals(requestApiKey)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                    "status": 401,
                    "message": "No autorizado. Se requiere un API Key válido en el header X-API-KEY"
                }
                """;

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
