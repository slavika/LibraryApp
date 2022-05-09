package com.libraryApp.controller;

import com.libraryApp.mapper.BookMapper;
import com.libraryApp.model.BookRepresentation;
import com.libraryApp.model.ErrorResponseRepresentation;
import com.libraryApp.model.GenreEnumRepresentation;
import com.libraryApp.persistence.model.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.libraryApp.service.LibraryService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
            BookEntity mappedBookEntity = mapRepToEntity(bookRepresentation);
            BookEntity bookEntity = libraryService.checkSignatureAndAddBook(mappedBookEntity);
            return new ResponseEntity<>(mapEntityToRep(bookEntity), HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.BAD_REQUEST, e);
        }
    }

    @PostMapping("/books-by-list")
    public ResponseEntity<Object> addBooks(@Valid @RequestBody List<BookRepresentation> bookRepresentations) {
        try {
            List<BookEntity> mappedBookEntity = bookRepresentations.stream().map(this::mapRepToEntity).collect(Collectors.toList());
            libraryService.checkSignaturesAndAddBooks(mappedBookEntity);
            return new ResponseEntity<>(bookRepresentations, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/books")
    public ResponseEntity<Object> getBooks() {
        List<BookEntity> allBookEntities = libraryService.getAllBooks();
        List<BookRepresentation> allBookRepresentations = allBookEntities.stream().map(this::mapEntityToRep).collect(Collectors.toList());
        return new ResponseEntity<>(allBookRepresentations, HttpStatus.OK);
    }


    @GetMapping("/books/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable int id) {
        try {
            BookEntity bookEntity = libraryService.getBookById(id);
            return new ResponseEntity<>(mapEntityToRep(bookEntity), HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }


    @GetMapping("/books/by-title")
    public ResponseEntity<Object> getBookByTitle(@RequestParam("title") String title) {
        try {
            List<BookEntity> bookEntitiesByTitle = libraryService.getBookByTitle(title);
            List<BookRepresentation> bookRepsByTitle = bookEntitiesByTitle.stream().map(this::mapEntityToRep).collect(Collectors.toList());
            return new ResponseEntity<>(bookRepsByTitle, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @GetMapping("/books/by-genre")
    public ResponseEntity<Object> getBooksByGenre(@RequestParam("genre") String genre) {
        try {
            List<BookEntity> booksByGenre = libraryService.getBooksByGenre(GenreEnumRepresentation.of(genre.toLowerCase()).getGenreName());
            List<BookRepresentation> bookRepsByGenre = booksByGenre.stream().map(this::mapEntityToRep).collect(Collectors.toList());
            return new ResponseEntity<>(bookRepsByGenre, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, new NoSuchElementException("Invalid genre name. Provide valid name."));
        }
    }

    @GetMapping("/books/sorted-by-{param}")
    public ResponseEntity<Object> getBooksSortedBy(@PathVariable String param) {
        switch (param) {
            case "author" -> {
                List<BookEntity> booksByAuthor = libraryService.sortBooksByAuthor();
                List<BookRepresentation> bookRepsByAuthor = booksByAuthor.stream().map(this::mapEntityToRep).collect(Collectors.toList());
                return new ResponseEntity<>(bookRepsByAuthor, HttpStatus.OK);
            }
            case "title" -> {
                List<BookEntity> booksByTitle = libraryService.sortBooksByTitle();
                List<BookRepresentation> bookRepsByTitle = booksByTitle.stream().map(this::mapEntityToRep).collect(Collectors.toList());
                return new ResponseEntity<>(bookRepsByTitle, HttpStatus.OK);
            }
            case "score-ascending" -> {
                List<BookEntity> booksByScoreAscending = libraryService.sortBooksByScoreAscending();
                List<BookRepresentation> bookRepsByScoreAscending = booksByScoreAscending.stream().map(this::mapEntityToRep).collect(Collectors.toList());
                return new ResponseEntity<>(bookRepsByScoreAscending, HttpStatus.OK);
            }
            case "score-descending" -> {
                List<BookEntity> booksByScoreDescending = libraryService.sortBooksByScoreDescending();
                List<BookRepresentation> bookRepsByScoreDescending = booksByScoreDescending.stream().map(this::mapEntityToRep).collect(Collectors.toList());
                return new ResponseEntity<>(bookRepsByScoreDescending, HttpStatus.OK);
            }
            default -> {
                return errorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, new Exception("No endpoint found."));
            }
        }
    }

    @GetMapping("/books/most-popular")
    public ResponseEntity<Object> getMostPopularBook() {
        try {
            List<BookEntity> mostPopularBookEntities = libraryService.getMostPopularBook();
            List<BookRepresentation> mostPopularBookReps= mostPopularBookEntities.stream().map(this::mapEntityToRep).collect(Collectors.toList());
            return new ResponseEntity<>(mostPopularBookReps, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.OK, e);
        }
    }

    @GetMapping("/books/sorted-by-score/{genre}")
    public ResponseEntity<Object> getSortedScoreByGenre(@PathVariable String genre) {
        List<BookEntity> bookEntitiesByScoreByGenre = libraryService.getSortedScoreByGenre(genre);
        List<BookRepresentation> bookRepsByScoreByGenre= bookEntitiesByScoreByGenre.stream().map(this::mapEntityToRep).collect(Collectors.toList());
        return new ResponseEntity<>(bookRepsByScoreByGenre, HttpStatus.OK);
    }

    @GetMapping("/books/highest-rated")
    public ResponseEntity<Object> getHighestRatedBook() {
        try {
            List<BookEntity> highestRateBookEntities = libraryService.getHighestRatedBook();
            List<BookRepresentation> highestRateBookReps= highestRateBookEntities.stream().map(this::mapEntityToRep).collect(Collectors.toList());
            return new ResponseEntity<>(highestRateBookReps, HttpStatus.OK);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.OK, e);
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable int id, @RequestBody BookRepresentation newBookRepresentation) {
        try {
            BookEntity newBookEntity = mapRepToEntity(newBookRepresentation);
            libraryService.checkIdAndUpdateBook(id, newBookEntity);
            return new ResponseEntity<>(newBookRepresentation, HttpStatus.CREATED);
        } catch (Exception e) {
            return errorResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @PutMapping("/books/{id}/rate")
    public ResponseEntity<Object> rateBook(@PathVariable int id, @RequestParam("rate") int rate) {
        try {
            BookEntity ratedBookEntity = libraryService.checkIdAndRateABook(id, rate);
            return new ResponseEntity<>(mapEntityToRep(ratedBookEntity), HttpStatus.OK);
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

    private ResponseEntity<Object> errorResponseEntity(HttpStatus httpStatus, Exception e) {
        return new ResponseEntity<>(new ErrorResponseRepresentation(httpStatus.value(),
                httpStatus.getReasonPhrase(), e.getMessage(), Arrays.toString(e.getStackTrace())), httpStatus);
    }

    private BookEntity mapRepToEntity(BookRepresentation bookRepresentation) {
        return BookMapper.INSTANCE.bookRepToEntity(bookRepresentation);
    }

    private BookRepresentation mapEntityToRep(BookEntity bookEntity) {
        return BookMapper.INSTANCE.entityToBookRep(bookEntity);
    }

}
