package com.course.libraryapp.exposure.controller;

import com.course.libraryapp.exposure.model.ErrorResponse;
import com.course.libraryapp.exposure.model.Book;
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
    public ResponseEntity<Object> addBook(@Valid @RequestBody Book book) {
        try {
            libraryService.checkSignatureAndAddBook(book);
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.BAD_REQUEST, e);
        }
    }

    @PostMapping("/books-by-list")
    public  ResponseEntity<Object> addBooks(@Valid @RequestBody List<Book> books) {
        try {
            libraryService.checkSignaturesAndAddBooks(books);
            return new ResponseEntity<>(books, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/books")
    public List<Book> getBooks() {
        return libraryService.getAllBooks();
    }


    @GetMapping("/books/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable int id) {
        try {
            Book book = libraryService.getBookById(id);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }


    @GetMapping("/books/by-title")
    public ResponseEntity<Object> getBookByTitle(@RequestParam("title") String title) {
        try {
            Book book = libraryService.getBookByTitle(title);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @GetMapping("/books/by-genre")
    public List<Book> getBooksByGenre(@RequestParam("genre") String genre) {
        return libraryService.getBooksByGenre(genre);
    }

    @GetMapping("/books/sorted-by-{param}")
    public List<Book> getBooksSortedBy(@PathVariable String param) {
        return switch (param) {
            case "author" -> libraryService.sortBooksByAuthor();
            case "title" -> libraryService.sortBooksByTitle();
            case "score-ascending" -> libraryService.sortBooksByScoreAscending();
            case "score-descending" -> libraryService.sortBooksByScoreDescending();
            default -> null;
        };
    }

    @GetMapping("/books/most-popular")
    public ResponseEntity<Object> getMostPopularBook() {
        try {
            Book book = libraryService.getMostPopularBook();
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NO_CONTENT, e);
        }
    }

    @GetMapping("/books/sorted-by-score/{genre}")
    public List<Book> getSortedScoreByGenre(@PathVariable String genre) {
        return libraryService.getSortedScoreByGenre(genre);
    }

    @GetMapping("/books/highest-rated")
    public ResponseEntity<Object> getHighestRatedBook() {
        try {
            Book book = libraryService.getHighestRatedBook();
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NO_CONTENT, e);
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable int id, @RequestBody Book newBook) {
        try {
            libraryService.checkIdAndUpdateBook(id, newBook);
            return new ResponseEntity<>(newBook, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @PutMapping("/books/{id}/rate")
    public ResponseEntity<Object> rateBook(@PathVariable int id, @RequestParam("rate") int rate) {
        try {
            Book ratedBook = libraryService.checkIdAndRateABook(id, rate);
            return new ResponseEntity<>(ratedBook, HttpStatus.OK);
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
        return new ResponseEntity<>(new ErrorResponse(httpStatus.value(),
                httpStatus.getReasonPhrase(), e.getMessage(), Arrays.toString(e.getStackTrace())), httpStatus);
    }
}
