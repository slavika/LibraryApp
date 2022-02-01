package com.course.libraryapp.exposure.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Data
public class BookRepresentation {
    private int id;
    @NotBlank(message = "Signature cannot be empty")
    private String signature;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    @NotBlank(message = "Author cannot be empty")
    private String author;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private GenreEnumRepresentation genre;
    private double score;
    private List<Integer> scoreRegistry;

    // public no-arg constructor required by Spring
    public BookRepresentation() {
        this.scoreRegistry = new ArrayList<>();
    }

    public BookRepresentation(int id, String signature, String title, String author, String description, String genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = checkIfGenreIsValid(genre);
        this.signature = signature;
        this.score = 0.0;
        this.scoreRegistry = new ArrayList<>();
    }

    // TODO dodac sprawdzenie czy gatunek istnieje
    private GenreEnumRepresentation checkIfGenreIsValid(String genre) {
        try {
            return GenreEnumRepresentation.of(genre);
        } catch (Exception ex) {
            throw new NoSuchElementException("blad");
        }
    }
}