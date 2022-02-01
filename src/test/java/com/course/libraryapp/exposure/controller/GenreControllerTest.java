package com.course.libraryapp.exposure.controller;

import com.course.libraryapp.exposure.service.GenreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Test
    void getGenres() throws Exception {
        Mockito.when(genreService.getAllGenres()).thenReturn(Arrays.asList("fantasy", "sci-fi"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/genres")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", contains("fantasy", "sci-fi")));
    }

}
