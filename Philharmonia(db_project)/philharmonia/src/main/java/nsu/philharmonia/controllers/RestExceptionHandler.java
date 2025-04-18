package nsu.philharmonia.controllers;

import nsu.philharmonia.model.exceptions.ErrorResponse;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(400, "Validation error"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(404, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidInputException.class})
    protected ResponseEntity<Object> handleInvalidInput(Exception e) {
        System.out.println("INVALID INPUT: e");
        return new ResponseEntity<>(new ErrorResponse(400, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
