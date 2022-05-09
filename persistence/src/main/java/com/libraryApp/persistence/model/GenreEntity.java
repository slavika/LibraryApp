package com.libraryApp.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "genres")
@Getter
@AllArgsConstructor
public class GenreEntity {

    @Id
    private String id;
    @NotBlank(message = "Genre name cannot be empty")
    private String genreName;
}
