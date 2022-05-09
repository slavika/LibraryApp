package com.libraryApp;

import com.libraryApp.persistence.model.GenreEntity;
import com.libraryApp.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.libraryApp.persistence.repository.GenreRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    private static final GenreEntity genreFantasyEntity = new GenreEntity("1", "fantasy");
    private static final GenreEntity genreSciFiEntity = new GenreEntity("2", "sci-fi");

    @Mock
    GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    @BeforeEach
    public void setup() {
        genreService = new GenreService(genreRepository);
    }

    @Test
    public void should_ReturnAllGenres_When_AnyExist() {
        when(genreRepository.findAll()).thenReturn(Arrays.asList(genreFantasyEntity, genreSciFiEntity));

        List<String> genres = genreService.getAllGenres();

        assertAll(
                () -> assertTrue(genres.contains("fantasy")),
                () -> assertTrue(genres.contains("sci-fi")),
                () -> assertFalse(genres.contains("unknown genre"))
        );
    }
}