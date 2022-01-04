package com.course.libraryapp.exposure.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class Book {
    private static final AtomicInteger count = new AtomicInteger(0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ElementCollection
    private List<Integer> scoreRegistry;

    // public no-arg constructor required by JPA
    public Book(){
        this.id = count.incrementAndGet();
        this.scoreRegistry = new ArrayList<>();
    }

    public Book(String signature, String title, String author, String description, String genre) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.signature = signature;
        this.score = 0.0;
        this.scoreRegistry = new ArrayList<>();
    }

    public int getId() {return id;}

    public void setId(int id) { this.id = id;}

    public String getSignature() {return signature;}

    public String getTitle() {return title;}

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<Integer> getScoreRegistry() {
        return scoreRegistry;
    }
}
