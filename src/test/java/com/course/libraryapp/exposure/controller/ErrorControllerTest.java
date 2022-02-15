package com.course.libraryapp.exposure.controller;

import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.exposure.service.LibraryService;
import com.course.libraryapp.exposure.util.JsonUtil;
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

import java.net.BindException;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
class ErrorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @Test
    void should_ThrowValidationError_When_SignatureMissing() throws Exception {
        Mockito.when(libraryService.checkSignatureAndAddBook
                        (new BookRepresentation(1, "","LOTR", "Tolkien", "Desc", "fantasy")))
                .thenThrow(new BindException());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/library/books")
                        .content(JsonUtil.mapToJson(
                                new BookRepresentation(1, "","LOTR", "Tolkien", "Desc", "fantasy")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Signature cannot be empty")))
                .andExpect(jsonPath("$.error", is("Bad Request")));

    }
}