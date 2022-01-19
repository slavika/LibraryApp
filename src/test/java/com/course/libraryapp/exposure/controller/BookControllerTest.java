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

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    private static final List<BookRepresentation> library = Arrays.asList(
            new BookRepresentation("F01","LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
            new BookRepresentation("F02" ,"Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
            new BookRepresentation("T01", "State of terror", "Hillary Rodham Clinton", "A series of terrorist attacks throws the global order into disarray", "thriller"),
            new BookRepresentation("SF01","Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
            new BookRepresentation("F03","Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy")
    );

    @Test
    void addBook() throws Exception {
        BookRepresentation bookRepresentationToAdd = library.get(4);
        Mockito.when(libraryService.checkSignatureAndAddBook(bookRepresentationToAdd)).thenReturn(Collections.singletonList(bookRepresentationToAdd));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/library/books")
                        .content(JsonUtil.mapToJson(bookRepresentationToAdd))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Good Omens")));

    }

    @Test
    void addDuplicatedBook() throws Exception {
        BookRepresentation bookRepresentationToAdd = library.get(4);
        Mockito.when(libraryService.checkSignatureAndAddBook(bookRepresentationToAdd)).thenThrow(new Exception());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/library/books")
                        .content(JsonUtil.mapToJson(bookRepresentationToAdd))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addBookByList() throws Exception {
        Mockito.when(libraryService.checkSignaturesAndAddBooks(library)).thenReturn(library);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/library/books-by-list")
                        .content(JsonUtil.mapToJson(library))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void addDuplicatedBooksByList() throws Exception {
        Mockito.when(libraryService.checkSignaturesAndAddBooks(library)).thenThrow(new Exception());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/library/books-by-list")
                        .content(JsonUtil.mapToJson(library))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void getBooks() throws Exception {
        Mockito.when(libraryService.getAllBooks()).thenReturn(library);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[2].title", is("State of terror")));
    }

    @Test
    void getBookById() throws Exception {
        Mockito.when(libraryService.getBookById(1)).thenReturn(library.get(0));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("LOTR")))
                .andExpect(jsonPath("$.author", is("J.R.R.Tolkien")))
                .andExpect(jsonPath("$.description", is("A hobbit on a mission to destroy the ring")));
    }

    @Test
    void getBookByIdNotFound() throws Exception {
        Mockito.when(libraryService.getBookById(1)).thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookByTitle() throws Exception {
        String title = "Diune";
        Mockito.when(libraryService.getBookByTitle(title)).thenReturn(Collections.singletonList(library.get(3)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/by-title")
                        .queryParam("title", title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is(title)))
                .andExpect(jsonPath("$[0].author", is("Frank Herbert")))
                .andExpect(jsonPath("$[0].description", is("Story about a spice")));
    }

    @Test
    void getBookByTitleNotFound() throws Exception {
        String title = "Diune";
        Mockito.when(libraryService.getBookByTitle(title)).thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/by-title")
                        .queryParam("title", title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByGenre() throws Exception {
        String genre = "fantasy";
        List<BookRepresentation> filteredBookRepresentations = library.stream().filter(book -> book.getGenre().equals(genre)).collect(Collectors.toList());
        Mockito.when(libraryService.getBooksByGenre(genre)).thenReturn(filteredBookRepresentations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/by-genre")
                        .queryParam("genre", genre)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$..title", hasItem("LOTR")))
                .andExpect(jsonPath("$..title", hasItem("Good Omens")))
                .andExpect(jsonPath("$..title", hasItem("Stardust")));
    }

    @Test
    void getBooksSortedByAuthor() throws Exception {
        List<BookRepresentation> sortedBookRepresentations = library.stream().sorted(Comparator.comparing(BookRepresentation::getAuthor)).collect(Collectors.toList());
        Mockito.when(libraryService.sortBooksByAuthor()).thenReturn(sortedBookRepresentations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/sorted-by-author")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author", is("Frank Herbert")))
                .andExpect(jsonPath("$[1].author", is("Hillary Rodham Clinton")))
                .andExpect(jsonPath("$[2].author", is("J.R.R.Tolkien")))
                .andExpect(jsonPath("$[3].author", is("Neil Gaiman")))
                .andExpect(jsonPath("$[4].author", is("Terry Pratchett & Neil Gaiman")));
    }

    @Test
    void getBooksSortedByTitle() throws Exception {
        List<BookRepresentation> sortedBookRepresentations = library.stream().sorted(Comparator.comparing(BookRepresentation::getTitle)).collect(Collectors.toList());
        Mockito.when(libraryService.sortBooksByTitle()).thenReturn(sortedBookRepresentations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/sorted-by-title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Diune")))
                .andExpect(jsonPath("$[1].title", is("Good Omens")))
                .andExpect(jsonPath("$[2].title", is("LOTR")))
                .andExpect(jsonPath("$[3].title", is("Stardust")))
                .andExpect(jsonPath("$[4].title", is("State of terror")));
    }

    @Test
    void getBooksSortedByScoreAscending() throws Exception {
        addRatesAndScores();
        List<BookRepresentation> sortedBookRepresentations = library.stream().sorted(Comparator.comparing(BookRepresentation::getScore)).collect(Collectors.toList());
        Mockito.when(libraryService.sortBooksByScoreAscending()).thenReturn(sortedBookRepresentations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/sorted-by-score-ascending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score", is(1.0)))
                .andExpect(jsonPath("$[1].score", is(2.3)))
                .andExpect(jsonPath("$[2].score", is(3.0)))
                .andExpect(jsonPath("$[3].score", is(4.0)))
                .andExpect(jsonPath("$[4].score", is(5.0)));
    }

    @Test
    void getBooksSortedByScoreDescending() throws Exception {
        addRatesAndScores();
        List<BookRepresentation> sortedBookRepresentations = library.stream().sorted(Comparator.comparing(BookRepresentation::getScore).reversed()).collect(Collectors.toList());
        Mockito.when(libraryService.sortBooksByScoreDescending()).thenReturn(sortedBookRepresentations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/sorted-by-score-descending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score", is(5.0)))
                .andExpect(jsonPath("$[1].score", is(4.0)))
                .andExpect(jsonPath("$[2].score", is(3.0)))
                .andExpect(jsonPath("$[3].score", is(2.3)))
                .andExpect(jsonPath("$[4].score", is(1.0)));
    }

    @Test
    void getMostPopularBook() throws Exception {
        addRatesAndScores();
        Mockito.when(libraryService.getMostPopularBook()).thenReturn(Collections.singletonList(library.get(4)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/most-popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Good Omens")));
    }

    @Test
    void getMostPopularBookNotFound() throws Exception {
        addRatesAndScores();
        Mockito.when(libraryService.getMostPopularBook()).thenThrow(new Exception());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/most-popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSortedScoreByGenre() throws Exception {
        addRatesAndScores();
        String genre = "fantasy";
        List<BookRepresentation> sortedBookRepresentations = library.stream().filter(book -> book.getGenre().equals(genre))
                .sorted(Comparator.comparing(BookRepresentation::getScore).reversed()).collect(Collectors.toList());
        Mockito.when(libraryService.getSortedScoreByGenre(genre)).thenReturn(sortedBookRepresentations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/sorted-by-score/" + genre)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("LOTR")))
                .andExpect(jsonPath("$[1].title", is("Good Omens")))
                .andExpect(jsonPath("$[2].title", is("Stardust")));
    }

    @Test
    void getHighestRatedBook() throws Exception {
        addRatesAndScores();
        Mockito.when(libraryService.getHighestRatedBook()).thenReturn(Collections.singletonList(library.get(2)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/highest-rated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("State of terror")))
                .andExpect(jsonPath("$[0].author", is("Hillary Rodham Clinton")));
    }

    @Test
    void getHighestRatedBookNotFound() throws Exception {
        addRatesAndScores();
        Mockito.when(libraryService.getHighestRatedBook()).thenThrow(new Exception());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books/highest-rated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBook() throws Exception {
        BookRepresentation updatedBookRepresentation = new BookRepresentation("F01", "Two Towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");
        Mockito.when(libraryService.checkIdAndUpdateBook(1, updatedBookRepresentation)).thenReturn(updatedBookRepresentation);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/library/books/1")
                        .content(JsonUtil.mapToJson(updatedBookRepresentation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Two Towers")));
    }

    @Test
    void updateNonExistingBook() throws Exception {
        BookRepresentation updatedBookRepresentation = new BookRepresentation("F01", "Two Towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");
        Mockito.when(libraryService.checkIdAndUpdateBook(10, updatedBookRepresentation)).thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/library/books/10")
                        .content(JsonUtil.mapToJson(updatedBookRepresentation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void rateBook() throws Exception {
        BookRepresentation ratedBookRepresentation = new BookRepresentation("F01", "Two Towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");
        ratedBookRepresentation.setScore(2);
        Mockito.when(libraryService.checkIdAndRateABook(1, 2)).thenReturn(ratedBookRepresentation);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/library/books/1/rate?rate=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score", is(2.0)));
    }

    @Test
    void rateNonExistingBook() throws Exception {
        Mockito.when(libraryService.checkIdAndRateABook(1, 2)).thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/library/books/1/rate?rate=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/library/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void removeNonExistingBook() throws Exception {
        Mockito.when(libraryService.checkIdAndRemoveBook(1)).thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/library/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private void addRatesAndScores() {
        library.get(0).setScore(4);
        library.get(0).getScoreRegistry().add(4);
        library.get(1).setScore(1);
        library.get(1).getScoreRegistry().add(1);
        library.get(2).setScore(5);
        library.get(2).getScoreRegistry().add(5);
        library.get(3).setScore(2.3);
        library.get(3).getScoreRegistry().add(4);
        library.get(4).setScore(3);
        library.get(0).getScoreRegistry().add(3);
        library.get(0).getScoreRegistry().add(3);
    }
}