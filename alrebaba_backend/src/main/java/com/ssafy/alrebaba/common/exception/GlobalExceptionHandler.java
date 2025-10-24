package com.ssafy.alrebaba.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 필드 유효성 검사 실패 처리기 (이메일 등)
    private Map<String, String> generateErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return error;
    }

    // 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> handleEnumTypeMismatchException(Exception ex) {
        String message = "올바르지 않은 값입니다.";
        Throwable cause = ex.getCause();
        if (cause instanceof IllegalArgumentException) {
            message = extractEnumValidationMessage(cause.getMessage());
        }
        return new ResponseEntity<>(generateErrorResponse(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(generateErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // 401 UnAuthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(generateErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    // 403 Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(generateErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    // 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(generateErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // 409 Conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(generateErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    private String extractEnumValidationMessage(String exceptionMessage) {
        String message = "올바르지 않은 값입니다.";
        String enumClassName = exceptionMessage.substring(exceptionMessage.indexOf("'") + 1, exceptionMessage.lastIndexOf("'"));
        try {
            Class<?> enumClass = Class.forName(enumClassName);
            if (enumClass.isEnum()) {
                String allowedValues = Stream.of(enumClass.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                message = "올바르지 않은 값입니다. 허용되는 값은 [" + allowedValues + "] 입니다.";
            }
        } catch (ClassNotFoundException ignored) {
        }
        return message;
    }

//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<?> handleException(Exception e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//    }


}

