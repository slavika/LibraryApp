package com.course.libraryapp.exposure.model;

import lombok.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class BookRepresentation {
    private static final AtomicInteger count = new AtomicInteger(0);

    private int id;
    @NotBlank(message = "Signature cannot be empty")
    private String signature;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    @NotBlank(message = "Author cannot be empty")
    private String author;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    @NotBlank(message = "Genre cannot be empty")
    private String genre;
    private double score;
    private List<Integer> scoreRegistry;

    // public no-arg constructor required by Spring
    public BookRepresentation(){
        this.id = count.incrementAndGet();
        this.scoreRegistry = new ArrayList<>();
    }

    public BookRepresentation(String signature, String title, String author, String description, String genre) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.signature = signature;
        this.score = 0.0;
        this.scoreRegistry = new ArrayList<>();
    }
}
