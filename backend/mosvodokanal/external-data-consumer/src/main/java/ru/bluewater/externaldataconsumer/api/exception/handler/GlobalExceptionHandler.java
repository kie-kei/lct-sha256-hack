package ru.bluewater.externaldataconsumer.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bluewater.externaldataconsumer.api.dto.response.ErrorResponse;
import ru.bluewater.externaldataconsumer.api.exception.ITPDataException;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ITPDataException.class)
    public ResponseEntity<ErrorResponse> handleITPDataException(ITPDataException e) {
        log.error("ITP Data processing error", e);

        ErrorResponse response = ErrorResponse.builder()
                .message("ITP Data processing failed: " + e.getMessage())
                .timestamp(new Date())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error", e);

        ErrorResponse response = ErrorResponse.builder()
                .message("Validation failed: " + e.getMessage())
                .timestamp(new Date())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error", e);

        ErrorResponse response = ErrorResponse.builder()
                .message("Unexpected error: " + e.getMessage())
                .timestamp(new Date())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
