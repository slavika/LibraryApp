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
    public ResponseEntity<Object> getBooks() {
        List<Book> allBooks = libraryService.getAllBooks();
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
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
    public ResponseEntity<Object> getBooksByGenre(@RequestParam("genre") String genre) {
        List<Book> booksByGenre = libraryService.getBooksByGenre(genre);
        return new ResponseEntity<>(booksByGenre, HttpStatus.OK);
    }

    @GetMapping("/books/sorted-by-{param}")
    public ResponseEntity<Object> getBooksSortedBy(@PathVariable String param) {
        switch (param) {
            case "author" -> {
                List<Book> booksByAuthor = libraryService.sortBooksByAuthor();
                return new ResponseEntity<>(booksByAuthor, HttpStatus.OK);
            }
            case "title" -> {
                List<Book> booksByTitle = libraryService.sortBooksByTitle();
                return new ResponseEntity<>(booksByTitle, HttpStatus.OK);
            }
            case "score-ascending" -> {
                List<Book> booksByScoreAscending = libraryService.sortBooksByScoreAscending();
                return new ResponseEntity<>(booksByScoreAscending, HttpStatus.OK);
            }
            case "score-descending" -> {
                List<Book> booksByScoreDescending = libraryService.sortBooksByScoreDescending();
                return new ResponseEntity<>(booksByScoreDescending, HttpStatus.OK);
            }
            default -> {
                return null;
            }
        }
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
    public ResponseEntity<Object> getSortedScoreByGenre(@PathVariable String genre) {
        List<Book> booksByScoreByGenre = libraryService.getSortedScoreByGenre(genre);
        return new ResponseEntity<>(booksByScoreByGenre, HttpStatus.OK);
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
