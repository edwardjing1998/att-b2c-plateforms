package att.b2c.segment.gateway.error;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<GatewayErrorResponse> handleTimeout(TimeoutException ex, ServerWebExchange exchange) {
        return buildResponse(HttpStatus.GATEWAY_TIMEOUT, "Downstream service timeout", ex, exchange);
    }

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<GatewayErrorResponse> handleNotFound(WebClientResponseException.NotFound ex, ServerWebExchange exchange) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex, exchange);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<GatewayErrorResponse> handleWebClient(WebClientResponseException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }
        return buildResponse(status, ex.getStatusText(), ex, exchange);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GatewayErrorResponse> handleResponseStatus(ResponseStatusException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return buildResponse(status, ex.getReason(), ex, exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GatewayErrorResponse> handleGeneric(Exception ex, ServerWebExchange exchange) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex, exchange);
    }

    private ResponseEntity<GatewayErrorResponse> buildResponse(HttpStatus status, String message, Throwable ex,
            ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();

        GatewayErrorResponse body = new GatewayErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message != null ? message : status.getReasonPhrase(),
                path);

        return ResponseEntity.status(status).body(body);
    }
}
