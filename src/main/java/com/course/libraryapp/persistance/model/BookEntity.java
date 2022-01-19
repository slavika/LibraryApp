package com.course.libraryapp.persistance.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Document(collection = "books")
@Setter
@Getter
public class BookEntity {

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

    // public no-arg constructor required
    public BookEntity(){
    }
}

