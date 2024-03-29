package com.course.libraryapp.exposure.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseRepresentation {

    private int status;
    private String error;
    private String message;
    private String stackTrace;

    public ErrorResponseRepresentation(int status, String error, String message, String stackTrace) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.stackTrace = stackTrace;
    }
}
