package com.microservicio.producto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    private int status;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ResponseData<T> success(String message, T data) {
        return ResponseData.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseData<T> created(String message, T data) {
        return ResponseData.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseData<T> error(int status, String message) {
        return ResponseData.<T>builder()
                .status(status)
                .message(message)
                .build();
    }
}
