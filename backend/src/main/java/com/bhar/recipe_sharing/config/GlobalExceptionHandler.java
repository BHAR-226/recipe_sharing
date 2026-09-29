package com.bhar.recipe_sharing.config;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.user.exception.EmailAlreadyUsedException;
import com.bhar.recipe_sharing.user.exception.UsernameAlreadyUsedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleEmail(EmailAlreadyUsedException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleUsername(UsernameAlreadyUsedException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
    String constraint = extractConstraint(ex.getMessage());
    String message = constraint.isEmpty()
        ? "Operation cannot be completed because it violates a database constraint (duplicate value or foreign-key reference)"
        : "Operation cannot be completed: violates database constraint " + constraint + " (duplicate value or foreign-key reference)";
    return body(HttpStatus.CONFLICT, message);
}

private String extractConstraint(String message) {
    if (message == null) return "";
    int start = message.indexOf("constraint [");
    if (start >= 0) {
        int end = message.indexOf(']', start);
        return end > start ? message.substring(start + "constraint [".length(), end) : "";
    }
    int idx = message.indexOf("Constraint:");
    if (idx >= 0) {
        String rest = message.substring(idx + "Constraint:".length()).trim();
        return rest.isBlank() ? "" : rest;
    }
    return "";
}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder msg = new StringBuilder("Validation failed: ");
        ex.getFieldErrors().forEach(e ->
            msg.append(e.getField()).append(" ").append(e.getDefaultMessage()).append("; "));
        return body(HttpStatus.BAD_REQUEST, msg.toString());
    }
}