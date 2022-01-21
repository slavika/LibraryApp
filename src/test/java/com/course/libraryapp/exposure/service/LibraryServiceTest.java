package com.course.libraryapp.exposure.service;

import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.exposure.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties
class LibraryServiceTest {

    private static final List<BookRepresentation> library = Arrays.asList(
            new BookRepresentation("F01", "LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
            new BookRepresentation("F02", "Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
            new BookRepresentation("T01", "State of terror", "Hillary Rodham Clinton", " A series of terrorist attacks throws the global order into disarray", "thriller/horror"),
            new BookRepresentation("SF01", "Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
            new BookRepresentation("F03", "Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy")
    );

    @Autowired
    BookRepository bookRepository;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    public void setup() {
        bookRepository.deleteAll();
        libraryService = new LibraryService(bookRepository);
        library.forEach(book -> book.setScore(0.0));
        library.forEach(book -> book.getScoreRegistry().clear());
        addMockBooksToLibrary();
    }

    @Test
    public void addBookToLibrary() {
        List<BookRepresentation> bookRepresentation = libraryService.getBookByTitle("LOTR");

        assertAll(
                () -> assertEquals(library.get(0).getTitle(), bookRepresentation.get(0).getTitle()),
                () -> assertEquals(library.get(0).getAuthor(), bookRepresentation.get(0).getAuthor()),
                () -> assertEquals(library.get(0).getDescription(), bookRepresentation.get(0).getDescription())
        );
    }

    @Test
    public void addDuplicateBookToLibrary() {
        Exception exception = assertThrows(Exception.class, () ->
                libraryService.checkSignatureAndAddBook(
                        new BookRepresentation("F01", "LOTR", "J.R.R.Tolkien", "A hobbit", "fantasy")));
        assertEquals("Book with provided signature F01 already in a library.", exception.getMessage());
    }

    @Test
    public void addListOfBooksToLibrary() throws Exception {
        final List<BookRepresentation> listOfBookRepresentations = Arrays.asList(
                new BookRepresentation("F05", "Two Towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
                new BookRepresentation("F06", "American Gods", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"));
        libraryService.checkSignaturesAndAddBooks(listOfBookRepresentations);

        assertAll(
                () -> assertEquals(7, libraryService.getAllBooks().size()),
                () -> assertEquals(listOfBookRepresentations.get(0).getSignature(), libraryService.getBookByTitle("Two Towers").get(0).getSignature()),
                () -> assertEquals(listOfBookRepresentations.get(1).getSignature(), libraryService.getBookByTitle("American Gods").get(0).getSignature())
        );
    }

    @Test
    public void removeBookFromLibrary() {
        libraryService.checkIdAndRemoveBook(1);
        List<BookRepresentation> bookRepresentations = libraryService.getAllBooks();

        assertAll(
                () -> assertEquals(4, bookRepresentations.size()),
                () -> assertTrue(bookRepresentations.stream().noneMatch(book -> book.getTitle().equals("LOTR")))
        );
    }

    @Test
    public void removeNonExistingBookFromLibrary() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                libraryService.checkIdAndRemoveBook(8));
        assertEquals("No requested book with id=8 in a library.", exception.getMessage());
    }

    @Test
    public void updateBook() {
        BookRepresentation newBookRepresentation = new BookRepresentation("F01", "Fellowship of the ring", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");
        BookRepresentation updatedBookRepresentation = libraryService.checkIdAndUpdateBook(1, newBookRepresentation);

        assertEquals("Fellowship of the ring", updatedBookRepresentation.getTitle());
    }

    @Test
    public void updateNonExistingBook() {
        BookRepresentation updatedBookRepresentation = new BookRepresentation("F05", "Two towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                libraryService.checkIdAndUpdateBook(9, updatedBookRepresentation));
        assertEquals("No requested book with id=9 in a library.", exception.getMessage());
    }

    @Test
    public void getBooksByGenre() {
        List<BookRepresentation> fantasyBookRepresentations = libraryService.getBooksByGenre("fantasy");

        assertAll(
                () -> assertEquals(3, fantasyBookRepresentations.size()),
                () -> fantasyBookRepresentations.forEach(book -> assertEquals("fantasy", book.getGenre().getGenreName())),
                () -> assertTrue(fantasyBookRepresentations.contains(library.get(0))),
                () -> assertTrue(fantasyBookRepresentations.contains(library.get(1))),
                () -> assertTrue(fantasyBookRepresentations.contains(library.get(4)))
        );
    }

    @Test
    public void getBooksByGenreNonExisting() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                libraryService.getBooksByGenre("fantasyyy"));
        assertEquals("No genre fantasyyy in a library.", exception.getMessage());
    }

    @Test
    public void getBooksByTitle() {
        List<BookRepresentation> bookRepresentation = libraryService.getBookByTitle("Diune");

        assertAll(
                () -> assertEquals("Diune", bookRepresentation.get(0).getTitle()),
                () -> assertEquals("Frank Herbert", bookRepresentation.get(0).getAuthor()),
                () -> assertEquals("Story about a spice", bookRepresentation.get(0).getDescription())
        );
    }

    @Test
    public void getBooksByTitleNotFound() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                libraryService.getBookByTitle("No title"));
        assertEquals("No requested book with title No title in a library.", exception.getMessage());
    }

    @Test
    public void sortBooksByAuthor() {
        List<BookRepresentation> sortedLibraryBookRepresentations = libraryService.sortBooksByAuthor();
        List<BookRepresentation> sorted = Arrays.asList(
                new BookRepresentation("SF01", "Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
                new BookRepresentation("T01", "State of terror", "Hillary Rodham Clinton", " A series of terrorist attacks throws the global order into disarray", "thriller/horror"),
                new BookRepresentation("F01", "LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
                new BookRepresentation("F02", "Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
                new BookRepresentation("F03", "Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy")
        );

        assertAll(
                () -> assertEquals(sorted.get(0).getAuthor(), sortedLibraryBookRepresentations.get(0).getAuthor()),
                () -> assertEquals(sorted.get(1).getAuthor(), sortedLibraryBookRepresentations.get(1).getAuthor()),
                () -> assertEquals(sorted.get(2).getAuthor(), sortedLibraryBookRepresentations.get(2).getAuthor()),
                () -> assertEquals(sorted.get(3).getAuthor(), sortedLibraryBookRepresentations.get(3).getAuthor()),
                () -> assertEquals(sorted.get(4).getAuthor(), sortedLibraryBookRepresentations.get(4).getAuthor())
        );
    }

    @Test
    public void sortBooksByTitle() {
        List<BookRepresentation> sortedLibraryBookRepresentations = libraryService.sortBooksByTitle();
        List<BookRepresentation> sorted = Arrays.asList(
                new BookRepresentation("SF01", "Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
                new BookRepresentation("F03", "Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy"),
                new BookRepresentation("F01", "LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
                new BookRepresentation("F02", "Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
                new BookRepresentation("T01", "State of terror", "Hillary Rodham Clinton", " A series of terrorist attacks throws the global order into disarray", "thriller/horror")
        );

        assertAll(
                () -> assertEquals(sorted.get(0).getTitle(), sortedLibraryBookRepresentations.get(0).getTitle()),
                () -> assertEquals(sorted.get(1).getTitle(), sortedLibraryBookRepresentations.get(1).getTitle()),
                () -> assertEquals(sorted.get(2).getTitle(), sortedLibraryBookRepresentations.get(2).getTitle()),
                () -> assertEquals(sorted.get(3).getTitle(), sortedLibraryBookRepresentations.get(3).getTitle()),
                () -> assertEquals(sorted.get(4).getTitle(), sortedLibraryBookRepresentations.get(4).getTitle())
        );
    }

    @Test
    public void sortBooksByScoreAscending() {
        rateBooks();
        List<BookRepresentation> sortedLibraryBookRepresentations = libraryService.sortBooksByScoreAscending();

        assertAll(
                () -> assertEquals(1.0, sortedLibraryBookRepresentations.get(0).getScore()),
                () -> assertEquals(2.0, sortedLibraryBookRepresentations.get(1).getScore()),
                () -> assertEquals(3.0, sortedLibraryBookRepresentations.get(2).getScore()),
                () -> assertEquals(4.33, sortedLibraryBookRepresentations.get(3).getScore()),
                () -> assertEquals(5.0, sortedLibraryBookRepresentations.get(4).getScore())
        );
    }

    @Test
    public void sortBooksByScoreDescending() {
        rateBooks();
        List<BookRepresentation> sortedLibraryBookRepresentations = libraryService.sortBooksByScoreDescending();

        assertAll(
                () -> assertEquals(5.0, sortedLibraryBookRepresentations.get(0).getScore()),
                () -> assertEquals(4.33, sortedLibraryBookRepresentations.get(1).getScore()),
                () -> assertEquals(3.0, sortedLibraryBookRepresentations.get(2).getScore()),
                () -> assertEquals(2.0, sortedLibraryBookRepresentations.get(3).getScore()),
                () -> assertEquals(1.0, sortedLibraryBookRepresentations.get(4).getScore())
        );
    }

    @Test
    public void getMostPopularBook() throws Exception {
        rateBooks();
        List<BookRepresentation> mostPopularBookRepresentation = libraryService.getMostPopularBook();

        assertEquals("LOTR", mostPopularBookRepresentation.get(0).getTitle());
        assertEquals("Good Omens", mostPopularBookRepresentation.get(1).getTitle());
    }

    @Test
    public void getMostPopularBookNoVotes() {
        Exception exception = assertThrows(Exception.class, () ->
                libraryService.getMostPopularBook());
        assertEquals("Couldn't get the most popular book. No votes yet.", exception.getMessage());
    }

    @Test
    public void getMostPopularByGenre() {
        rateBooks();
        List<BookRepresentation> sortedLibraryBookRepresentations = libraryService.getSortedScoreByGenre("fantasy");

        assertAll(
                () -> assertEquals(3, sortedLibraryBookRepresentations.size()),
                () -> assertEquals("LOTR", sortedLibraryBookRepresentations.get(0).getTitle()),
                () -> assertEquals(4.33, sortedLibraryBookRepresentations.get(0).getScore()),
                () -> assertEquals("Good Omens", sortedLibraryBookRepresentations.get(1).getTitle()),
                () -> assertEquals(3.0, sortedLibraryBookRepresentations.get(1).getScore()),
                () -> assertEquals("Stardust", sortedLibraryBookRepresentations.get(2).getTitle()),
                () -> assertEquals(1.0, sortedLibraryBookRepresentations.get(2).getScore())
        );
    }

    @Test
    public void getHighestRatedBook() throws Exception {
        rateBooks();
        List<BookRepresentation> highestRatedBookRepresentations = libraryService.getHighestRatedBook();

        assertAll(
                () -> assertEquals(5.0, highestRatedBookRepresentations.get(0).getScore()),
                () -> assertEquals("State of terror", highestRatedBookRepresentations.get(0).getTitle())
        );
    }

    @Test
    public void getHighestRatedBookAllRate0() {
        Exception exception = assertThrows(Exception.class, () ->
                libraryService.getHighestRatedBook());
        assertEquals("Couldn't get the highest rated book. All rate to 0.0", exception.getMessage());
    }

    @Test
    public void rateBook() {
        libraryService.checkIdAndRateABook(5, 4);
        libraryService.checkIdAndRateABook(5, 5);
        libraryService.checkIdAndRateABook(5, 2);
        BookRepresentation bookRepresentation = libraryService.getBookById(5);

        assertAll(
                () -> assertEquals(3, bookRepresentation.getScoreRegistry().size()),
                () -> assertEquals(3.67, bookRepresentation.getScore())
        );
    }

    private void rateBooks() {
        libraryService.checkIdAndRateABook(1, 4);
        libraryService.checkIdAndRateABook(1, 5);
        libraryService.checkIdAndRateABook(1, 4);
        libraryService.checkIdAndRateABook(2, 1);
        libraryService.checkIdAndRateABook(3, 5);
        libraryService.checkIdAndRateABook(4, 2);
        libraryService.checkIdAndRateABook(5, 3);
        libraryService.checkIdAndRateABook(5, 4);
        libraryService.checkIdAndRateABook(5, 2);
    }

    private void addMockBooksToLibrary() {
        library.forEach(book -> {
            try {
                libraryService.checkSignatureAndAddBook(book);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}