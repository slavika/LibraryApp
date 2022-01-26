package com.course.libraryapp.exposure.controller;

import com.course.libraryapp.exposure.model.ErrorResponseRepresentation;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestControllerAdvice
class ErrorController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        return this.createResponseEntity(List.of(Objects.requireNonNull(ex.getMessage())), request);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        List<ObjectError> exceptions = ex.getBindingResult().getAllErrors();
        List<String> errorMessages = exceptions.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return this.createResponseEntity(errorMessages, request);
    }

    private ResponseEntity<Object> createResponseEntity(List<String> errorMessages, WebRequest request) {
        ErrorResponseRepresentation errorResponse = ErrorResponseRepresentation.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessages.toString().replace("[", "").replace("]", ""))
                .build();
        return handleExceptionInternal(new Exception(errorResponse.getMessage()), errorResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
