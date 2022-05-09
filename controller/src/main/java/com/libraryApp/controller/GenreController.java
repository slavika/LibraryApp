package com.libraryApp.controller;

import com.libraryApp.model.GenreEnumRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.libraryApp.service.GenreService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/library")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public ResponseEntity<Object> getGenresFromLibrary() {
        List<String> genres = genreService.getAllGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/possible-genres")
    public ResponseEntity<Object> getAllPossibleGenres() {
        List<String> possibleValues = Arrays.stream(GenreEnumRepresentation.values()).map(GenreEnumRepresentation::getGenreName).collect(Collectors.toList());
        return new ResponseEntity<>(possibleValues, HttpStatus.OK);
    }
}
