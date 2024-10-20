package org.shiftlab.controllers.handler;

import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.exceptions.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValid(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Invalid request");

        Map<String, String> errors = new HashMap<>();
        e.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

        });
        problemDetail.setProperty("errors",errors);

        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(SellerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSellerNotFound(SellerNotFoundException ex) {
        StringBuilder message = new StringBuilder("Seller");

        if(ex.getId() != null) {
            message.append(String.format(" with id '%d'", ex.getId()));
        }
        if(ex.getPeriod() != null) {
            message.append(String.format(" with period '%s'", ex.getPeriod()));
        }
        message.append(" not found");

        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,message.toString());
        problemDetail.setProperty("error", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSellerNotFound(TransactionNotFoundException ex) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,String.format("Transaction with id %d was not found", ex.getId()));
        problemDetail.setProperty("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseStatusException handleResponseStatusException(ResponseStatusException ex) {
        return ex;
    }
}
