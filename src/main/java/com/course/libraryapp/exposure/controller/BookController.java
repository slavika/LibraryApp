package com.course.libraryapp.exposure.controller;

import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.exposure.model.ErrorResponseRepresentation;
import com.course.libraryapp.exposure.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequestMapping("/library")
public class BookController {
    private final LibraryService libraryService;

    @Autowired
    public BookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/books")
    public ResponseEntity<Object> addBook(@Valid @RequestBody BookRepresentation bookRepresentation) {
        try {
            libraryService.checkSignatureAndAddBook(bookRepresentation);
            return new ResponseEntity<>(bookRepresentation, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.BAD_REQUEST, e);
        }
    }

    @PostMapping("/books-by-list")
    public  ResponseEntity<Object> addBooks(@Valid @RequestBody List<BookRepresentation> bookRepresentations) {
        try {
            libraryService.checkSignaturesAndAddBooks(bookRepresentations);
            return new ResponseEntity<>(bookRepresentations, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/books")
    public ResponseEntity<Object> getBooks() {
        List<BookRepresentation> allBookRepresentations = libraryService.getAllBooks();
        return new ResponseEntity<>(allBookRepresentations, HttpStatus.OK);
    }


    @GetMapping("/books/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable int id) {
        try {
            BookRepresentation bookRepresentation = libraryService.getBookById(id);
            return new ResponseEntity<>(bookRepresentation, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }


    @GetMapping("/books/by-title")
    public ResponseEntity<Object> getBookByTitle(@RequestParam("title") String title) {
        try {
            List<BookRepresentation> booksByTitle = libraryService.getBookByTitle(title);
            return new ResponseEntity<>(booksByTitle, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @GetMapping("/books/by-genre")
    public ResponseEntity<Object> getBooksByGenre(@RequestParam("genre") String genre) {
        try{
            List<BookRepresentation> booksByGenre = libraryService.getBooksByGenre(genre);
            return new ResponseEntity<>(booksByGenre, HttpStatus.OK);
        } catch (Exception e){
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @GetMapping("/books/sorted-by-{param}")
    public ResponseEntity<Object> getBooksSortedBy(@PathVariable String param) {
        switch (param) {
            case "author" -> {
                List<BookRepresentation> booksByAuthor = libraryService.sortBooksByAuthor();
                return new ResponseEntity<>(booksByAuthor, HttpStatus.OK);
            }
            case "title" -> {
                List<BookRepresentation> booksByTitle = libraryService.sortBooksByTitle();
                return new ResponseEntity<>(booksByTitle, HttpStatus.OK);
            }
            case "score-ascending" -> {
                List<BookRepresentation> booksByScoreAscending = libraryService.sortBooksByScoreAscending();
                return new ResponseEntity<>(booksByScoreAscending, HttpStatus.OK);
            }
            case "score-descending" -> {
                List<BookRepresentation> booksByScoreDescending = libraryService.sortBooksByScoreDescending();
                return new ResponseEntity<>(booksByScoreDescending, HttpStatus.OK);
            }
            default -> {
                return errorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, new Exception("No endpoint found."));
            }
        }
    }

    @GetMapping("/books/most-popular")
    public ResponseEntity<Object> getMostPopularBook() {
        try {
            List<BookRepresentation> mostPopularBookRepresentations = libraryService.getMostPopularBook();
            return new ResponseEntity<>(mostPopularBookRepresentations, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.OK, e);
        }
    }

    @GetMapping("/books/sorted-by-score/{genre}")
    public ResponseEntity<Object> getSortedScoreByGenre(@PathVariable String genre) {
        List<BookRepresentation> booksByScoreByGenre = libraryService.getSortedScoreByGenre(genre);
        return new ResponseEntity<>(booksByScoreByGenre, HttpStatus.OK);
    }

    @GetMapping("/books/highest-rated")
    public ResponseEntity<Object> getHighestRatedBook() {
        try {
            List<BookRepresentation> highestRateBookRepresentations = libraryService.getHighestRatedBook();
            return new ResponseEntity<>(highestRateBookRepresentations, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.OK, e);
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable int id, @RequestBody BookRepresentation newBookRepresentation) {
        try {
            libraryService.checkIdAndUpdateBook(id, newBookRepresentation);
            return new ResponseEntity<>(newBookRepresentation, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @PutMapping("/books/{id}/rate")
    public ResponseEntity<Object> rateBook(@PathVariable int id, @RequestParam("rate") int rate) {
        try {
            BookRepresentation ratedBookRepresentation = libraryService.checkIdAndRateABook(id, rate);
            return new ResponseEntity<>(ratedBookRepresentation, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Object> removeBook(@PathVariable int id) {
        try {
            libraryService.checkIdAndRemoveBook(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    private ResponseEntity<Object> errorResponseEntity(HttpStatus httpStatus, Exception e){
        return new ResponseEntity<>(new ErrorResponseRepresentation(httpStatus.value(),
                httpStatus.getReasonPhrase(), e.getMessage(), Arrays.toString(e.getStackTrace())), httpStatus);
    }
}
