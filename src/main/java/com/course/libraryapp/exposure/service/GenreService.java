package com.course.libraryapp.exposure.service;

import com.course.libraryapp.exposure.repository.GenreRepository;
import com.course.libraryapp.persistance.model.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ApplicationScope
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<String> getAllGenres() {
        List<GenreEntity> genreEntities = genreRepository.findAll();
        List<String> genres = genreEntities.stream()
                .map(GenreEntity::getGenreName).collect(Collectors.toList());
        return genres;
    }
}
