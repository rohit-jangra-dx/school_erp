package org.example.schoolerp.core;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  public record ApiError(String timeStamp, int status, String error, Map<String, Object> fields) {}
  ;

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
    return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

    Map<String, Object> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

    return ResponseEntity.badRequest()
        .body(
            new ApiError(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
    return errorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex) {

    return ResponseEntity.badRequest()
        .body(
            new ApiError(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Malformed request body",
                null));
  }

  // helpers
  private ResponseEntity<ApiError> errorResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ApiError(Instant.now().toString(), status.value(), message, null));
  }
}
