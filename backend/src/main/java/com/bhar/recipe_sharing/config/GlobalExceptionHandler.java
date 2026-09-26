package com.bhar.recipe_sharing.config;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bhar.recipe_sharing.user.exception.EmailAlreadyUsedException;
import com.bhar.recipe_sharing.user.exception.UserNotFoundException;
import com.bhar.recipe_sharing.user.exception.UsernameAlreadyUsedException;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    private ResponseEntity <Map<String, Object>> body(HttpStatus status, String message){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",status.value());
        body.put("message",message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlNotFoud(UserNotFoundException ex){
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
        
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleEmail(EmailAlreadyUsedException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());        // 409
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleUsername(UsernameAlreadyUsedException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());        // 409
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder msg = new StringBuilder("Validation failed: ");
        ex.getFieldErrors().forEach(e ->
            msg.append(e.getField()).append(" ").append(e.getDefaultMessage()).append("; "));
        return body(HttpStatus.BAD_REQUEST, msg.toString());       // 400
    }
}
