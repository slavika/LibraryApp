package com.course.libraryapp.exposure.service;

import com.course.libraryapp.exposure.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private static final List<Book> library = Arrays.asList(
            new Book("F01", "LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
            new Book("F02", "Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
            new Book("T01", "State of terror", "Hillary Rodham Clinton", " A series of terrorist attacks throws the global order into disarray", "thriller"),
            new Book("SF01", "Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
            new Book("F03", "Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy")
    );

    @MockBean
    private LibraryService libraryService;

    @BeforeEach
    public void setup() {
        libraryService = new LibraryService();
        library.forEach(book -> book.setScore(0.0));
        library.forEach(book -> book.getScoreRegistry().clear());
        addMockBooksToLibrary();
    }

    @Test
    public void addBookToLibrary() {
        Book book = libraryService.getBookByTitle("LOTR");

        assertAll(
                () -> assertEquals(library.size(), libraryService.getAllBooks().size()),
                () -> assertEquals(library.get(0).getTitle(), book.getTitle()),
                () -> assertEquals(library.get(0).getAuthor(), book.getAuthor()),
                () -> assertEquals(library.get(0).getDescription(), book.getDescription())
        );
    }

    @Test
    public void addDuplicateBookToLibrary() {
        Exception exception = assertThrows(Exception.class, () ->
                libraryService.checkSignatureAndAddBook(
                        new Book("F01", "LOTR", "J.R.R.Tolkien", "A hobbit", "fantasy")));
        assertEquals("Book with provided signature F01 already in a library.", exception.getMessage());
    }

    @Test
    public void addListOfBooksToLibrary() throws Exception {
        final List<Book> listOfBooks = Arrays.asList(
                new Book("F05", "Two Towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
                new Book("F06", "American Gods", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"));
        libraryService.checkSignaturesAndAddBooks(listOfBooks);

        assertAll(
                () -> assertEquals(7, libraryService.getAllBooks().size()),
                () -> assertEquals(listOfBooks.get(0).getSignature(), libraryService.getBookByTitle("Two Towers").getSignature()),
                () -> assertEquals(listOfBooks.get(1).getSignature(), libraryService.getBookByTitle("American Gods").getSignature())
        );
    }

    @Test
    public void removeBookFromLibrary() {
        List<Book> books = libraryService.checkIdAndRemoveBook(1);

        assertAll(
                () -> assertEquals(4, books.size()),
                () -> assertTrue(books.stream().noneMatch(book -> book.getTitle().equals("LOTR")))
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
        Book newBook = new Book("F01", "Fellowship of the ring", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");
        Book updatedBook = libraryService.checkIdAndUpdateBook(1, newBook);

        assertEquals("Fellowship of the ring", updatedBook.getTitle());
    }

    @Test
    public void updateNonExistingBook() {
        Book updatedBook = new Book("F05", "Two towers", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy");

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                libraryService.checkIdAndUpdateBook(9, updatedBook));
        assertEquals("No requested book with id=9 in a library.", exception.getMessage());
    }

    @Test
    public void getBooksByGenre() {
        List<Book> fantasyBooks = libraryService.getBooksByGenre("fantasy");

        assertAll(
                () -> assertEquals(3, fantasyBooks.size()),
                () -> fantasyBooks.forEach(book -> assertEquals("fantasy", book.getGenre())),
                () -> assertTrue(fantasyBooks.contains(library.get(0))),
                () -> assertTrue(fantasyBooks.contains(library.get(1))),
                () -> assertTrue(fantasyBooks.contains(library.get(4)))
        );
    }

    @Test
    public void getBooksByTitle() {
        Book book = libraryService.getBookByTitle("Diune");

        assertAll(
                () -> assertEquals("Diune", book.getTitle()),
                () -> assertEquals("Frank Herbert", book.getAuthor()),
                () -> assertEquals("Story about a spice", book.getDescription())
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
        List<Book> sortedLibraryBooks = libraryService.sortBooksByAuthor();
        List<Book> sorted = Arrays.asList(
                new Book("SF01", "Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
                new Book("T01", "State of terror", "Hillary Rodham Clinton", " A series of terrorist attacks throws the global order into disarray", "thriller"),
                new Book("F01", "LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
                new Book("F02", "Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
                new Book("F03", "Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy")
        );

        assertAll(
                () -> assertEquals(sorted.get(0).getAuthor(), sortedLibraryBooks.get(0).getAuthor()),
                () -> assertEquals(sorted.get(1).getAuthor(), sortedLibraryBooks.get(1).getAuthor()),
                () -> assertEquals(sorted.get(2).getAuthor(), sortedLibraryBooks.get(2).getAuthor()),
                () -> assertEquals(sorted.get(3).getAuthor(), sortedLibraryBooks.get(3).getAuthor()),
                () -> assertEquals(sorted.get(4).getAuthor(), sortedLibraryBooks.get(4).getAuthor())
        );
    }

    @Test
    public void sortBooksByTitle() {
        List<Book> sortedLibraryBooks = libraryService.sortBooksByTitle();
        List<Book> sorted = Arrays.asList(
                new Book("SF01", "Diune", "Frank Herbert", "Story about a spice", "sci-fi"),
                new Book("F03", "Good Omens", "Terry Pratchett & Neil Gaiman", "Extremely silly story of an angel", "fantasy"),
                new Book("F01", "LOTR", "J.R.R.Tolkien", "A hobbit on a mission to destroy the ring", "fantasy"),
                new Book("F02", "Stardust", "Neil Gaiman", "Young man tries to find a star for the woman he loves after they see it fall from the night sky. ", "fantasy"),
                new Book("T01", "State of terror", "Hillary Rodham Clinton", " A series of terrorist attacks throws the global order into disarray", "thriller")
        );

        assertAll(
                () -> assertEquals(sorted.get(0).getTitle(), sortedLibraryBooks.get(0).getTitle()),
                () -> assertEquals(sorted.get(1).getTitle(), sortedLibraryBooks.get(1).getTitle()),
                () -> assertEquals(sorted.get(2).getTitle(), sortedLibraryBooks.get(2).getTitle()),
                () -> assertEquals(sorted.get(3).getTitle(), sortedLibraryBooks.get(3).getTitle()),
                () -> assertEquals(sorted.get(4).getTitle(), sortedLibraryBooks.get(4).getTitle())
        );
    }

    @Test
    public void sortBooksByScoreAscending() {
        rateBooks();
        List<Book> sortedLibraryBooks = libraryService.sortBooksByScoreAscending();

        assertAll(
                () -> assertEquals(1.0, sortedLibraryBooks.get(0).getScore()),
                () -> assertEquals(2.0, sortedLibraryBooks.get(1).getScore()),
                () -> assertEquals(3.0, sortedLibraryBooks.get(2).getScore()),
                () -> assertEquals(4.5, sortedLibraryBooks.get(3).getScore()),
                () -> assertEquals(5.0, sortedLibraryBooks.get(4).getScore())
        );
    }

    @Test
    public void sortBooksByScoreDescending() {
        rateBooks();
        List<Book> sortedLibraryBooks = libraryService.sortBooksByScoreDescending();

        assertAll(
                () -> assertEquals(5.0, sortedLibraryBooks.get(0).getScore()),
                () -> assertEquals(4.5, sortedLibraryBooks.get(1).getScore()),
                () -> assertEquals(3.0, sortedLibraryBooks.get(2).getScore()),
                () -> assertEquals(2.0, sortedLibraryBooks.get(3).getScore()),
                () -> assertEquals(1.0, sortedLibraryBooks.get(4).getScore())
        );
    }

    @Test
    public void getMostPopularBook() throws Exception {
        rateBooks();
        Book mostPopularBook = libraryService.getMostPopularBook();

        assertEquals("Good Omens", mostPopularBook.getTitle());
    }

    @Test
    public void getMostPopularBookNoContent() {
        LibraryService libraryServiceMock = new LibraryService();
        LibraryService spy = Mockito.spy(libraryServiceMock);

        try {
            Mockito.when(spy.getMostPopularBook()).thenThrow(Exception.class);
            spy.getMostPopularBook();
        } catch (Exception e) {
            assertEquals("Couldn't get the most popular book.", e.getMessage());
        }
    }

    @Test
    public void getMostPopularByGenre() {
        rateBooks();
        List<Book> sortedLibraryBooks = libraryService.getSortedScoreByGenre("fantasy");

        assertAll(
                () -> assertEquals(3, sortedLibraryBooks.size()),
                () -> assertEquals("LOTR", sortedLibraryBooks.get(0).getTitle()),
                () -> assertEquals(4.5, sortedLibraryBooks.get(0).getScore()),
                () -> assertEquals("Good Omens", sortedLibraryBooks.get(1).getTitle()),
                () -> assertEquals(3.0, sortedLibraryBooks.get(1).getScore()),
                () -> assertEquals("Stardust", sortedLibraryBooks.get(2).getTitle()),
                () -> assertEquals(1.0, sortedLibraryBooks.get(2).getScore())
        );
    }

    @Test
    public void getHighestRatedBook() throws Exception {
        rateBooks();
        Book highestRatedBook = libraryService.getHighestRatedBook();

        assertAll(
                () -> assertEquals(5.0, highestRatedBook.getScore()),
                () -> assertEquals("State of terror", highestRatedBook.getTitle())
        );
    }

    @Test
    public void getHighestRatedBookNoContent() {
        LibraryService libraryServiceMock = new LibraryService();
        LibraryService spy = Mockito.spy(libraryServiceMock);

        try {
            Mockito.when(spy.getHighestRatedBook()).thenThrow(Exception.class);
            spy.getHighestRatedBook();
        } catch (Exception e) {
            assertEquals("Couldn't get the highest rated book.", e.getMessage());
        }
    }

    @Test
    public void rateBook() {
        libraryService.checkIdAndRateABook(5, 4);
        libraryService.checkIdAndRateABook(5, 5);
        libraryService.checkIdAndRateABook(5, 2);
        Book book = libraryService.getBookById(5);

        assertAll(
                () -> assertEquals(3, book.getScoreRegistry().size()),
                () -> assertEquals(3.67, book.getScore())
        );
    }

    private void rateBooks() {
        libraryService.checkIdAndRateABook(1, 4);
        libraryService.checkIdAndRateABook(1, 5);
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