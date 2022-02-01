package com.course.libraryapp.exposure.service;

import com.course.libraryapp.exposure.repository.GenreRepository;
import com.course.libraryapp.persistance.model.GenreEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GenreServiceTest {

    private static final GenreEntity genreFantasyEntity = new GenreEntity("1", "fantasy");
    private static final GenreEntity genreSciFiEntity = new GenreEntity("2", "sci-fi");

    @MockBean
    GenreRepository genreRepository;

    @Autowired
    private GenreService genreService;

    @BeforeEach
    public void setup() {
        genreService = new GenreService(genreRepository);
    }

    @Test
    public void getGenres() {
        when(genreRepository.findAll()).thenReturn(Arrays.asList(genreFantasyEntity, genreSciFiEntity));

        List<String> genres = genreService.getAllGenres();

        assertAll(
                () -> assertTrue(genres.contains("fantasy")),
                () -> assertTrue(genres.contains("sci-fi")),
                () -> assertFalse(genres.contains("unknown genre"))
        );
    }
}
