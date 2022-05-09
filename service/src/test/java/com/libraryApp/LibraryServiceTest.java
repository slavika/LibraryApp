package com.libraryApp;

import com.libraryApp.persistence.model.BookEntity;
import com.libraryApp.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.libraryApp.persistence.repository.BookRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
class LibraryServiceTest {

    private static final BookEntity bookEntity = new BookEntity(1, "F01", "LOTR", "J.R.R.Tolkien",
            "A hobbit on a mission to destroy the ring", "sci-fi", 3.5, new ArrayList<>());
    private static final BookEntity bookEntity2 = new BookEntity(2, "F02", "Star Dust", "Gaiman",
            "A hobbit on a mission", "fantasy", 5.0, Arrays.asList(5, 5, 5));
    private static final BookEntity bookEntity3 = new BookEntity(3, "F03", "Witcher", "Neil Gaiman",
            "A hobbit on a mission", "fantasy", 3.0, Arrays.asList(3, 3));
    private static final BookEntity bookEntity5 = new BookEntity(5, "F05", "Two Towers", "Zajdel",
            "A hobbit on a mission to destroy the ring", "thriller/horror", 4.5, Arrays.asList(4, 5));
    private static final BookEntity bookEntity6 = new BookEntity(6, "F06", "American Gods", "Neil Gaiman",
            "Young man tries to find a star for the woman he loves.", "powieść przygodowa", 2.0, List.of(2));
    private static final BookEntity bookEntity7 = new BookEntity(7, "F07", "Witcher", "Adrzej Sapkowski",
            "A hobbit on a mission", "fantasy", 0.0, Arrays.asList(2, 4, 5));


    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    public void setup() {
        libraryService = new LibraryService(bookRepository);
    }

    @Nested
    class CRUDTests{

        @Test
        public void should_AddBookToLibrary_When_BookRepIsOk() throws Exception {
            BookEntity newbookEntity = libraryService.checkSignatureAndAddBook(bookEntity);

            assertAll(
                    () -> assertEquals(newbookEntity.getTitle(), bookEntity.getTitle()),
                    () -> assertEquals(newbookEntity.getAuthor(), bookEntity.getAuthor()),
                    () -> assertEquals(newbookEntity.getDescription(), bookEntity.getDescription())
            );
        }

        @Test
        public void should_AddListOfBooksToLibrary_When_CorrectListOfBooksGiven() throws Exception {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity5, bookEntity6));

            final List<BookEntity> listOfBookRepresentations = Arrays.asList(bookEntity5, bookEntity6);

            List<BookEntity> result = libraryService.checkSignaturesAndAddBooks(listOfBookRepresentations);

            assertAll(
                    () -> assertEquals(2, libraryService.getAllBooks().size()),
                    () -> assertEquals(result.get(0).getSignature(), bookEntity5.getSignature()),
                    () -> assertEquals(result.get(1).getSignature(), bookEntity6.getSignature())
            );
        }

        @Test
        public void should_RemoveBookFromLibrary_When_BookIdExists() {
            when(bookRepository.findById(1)).thenReturn(bookEntity);
            when(bookRepository.findAll()).thenReturn(Collections.singletonList(bookEntity2));

            libraryService.checkIdAndRemoveBook(1);
            List<BookEntity> bookRepresentations = libraryService.getAllBooks();

            assertAll(
                    () -> assertEquals(1, bookRepresentations.size()),
                    () -> assertTrue(bookRepresentations.stream().noneMatch(book -> book.getTitle().equals("LOTR")))
            );
        }

        @Test
        public void should_UpdateBook_When_BookIdExists() {
            when(bookRepository.findById(1)).thenReturn(bookEntity);

            BookEntity updatedBookRepresentation = libraryService.checkIdAndUpdateBook(1, bookEntity2);

            assertEquals("Star Dust", updatedBookRepresentation.getTitle());
        }

        @Test
        public void should_RateABook_When_BookIdExists() {
            when(bookRepository.findById(1)).thenReturn(bookEntity);

            libraryService.checkIdAndRateABook(1, 4);
            libraryService.checkIdAndRateABook(1, 5);

            BookEntity bookRepresentation = libraryService.getBookById(1);

                    assertAll(
                    () -> assertEquals(2, bookRepresentation.getScoreRegistry().size()),
                    () -> assertEquals(4.5, bookRepresentation.getScore())
            );
        }
    }

    @Nested
    class ReturnFilteredBooksTests {
        @Test
        public void should_ReturnBooksByTitle_When_TitleExists() {
            when(bookRepository.findAllByTitle("Witcher")).thenReturn(Arrays.asList(bookEntity3, bookEntity7));

            List<BookEntity> bookRepresentation = libraryService.getBookByTitle("Witcher");

            assertAll(
                    () -> assertEquals(bookEntity3.getTitle(), bookRepresentation.get(0).getTitle()),
                    () -> assertEquals(bookEntity7.getTitle(), bookRepresentation.get(1).getTitle())
            );
        }

        @Test
        public void should_ReturnBooksByGenre_When_GenreExists() {
            when(bookRepository.findAllByGenre("fantasy")).thenReturn(Arrays.asList(bookEntity2, bookEntity3));

            List<BookEntity> fantasyBookRepresentations = libraryService.getBooksByGenre("fantasy");

            assertAll(
                    () -> assertEquals(2, fantasyBookRepresentations.size()),
                    () -> fantasyBookRepresentations.forEach(book -> assertEquals("fantasy", book.getGenre()))
            );
        }

        @Test
        public void should_ReturnMostPopularBook_When_ThereIsOne() throws Exception {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity, bookEntity2, bookEntity7, bookEntity5, bookEntity6, bookEntity3));

            List<BookEntity> mostPopularBookRepresentation = libraryService.getMostPopularBook();

            assertAll(
                    () -> assertEquals(bookEntity2.getTitle(), mostPopularBookRepresentation.get(0).getTitle()),
                    () -> assertEquals(bookEntity7.getTitle(), mostPopularBookRepresentation.get(1).getTitle())
            );
        }

        @Test
        public void should_ReturnMostPopularBooksByGenre_When_GenreExists() {
            when(bookRepository.findAllByGenre("fantasy")).thenReturn(Arrays.asList(bookEntity2, bookEntity7, bookEntity3));

            List<BookEntity> sortedLibraryBookRepresentations = libraryService.getSortedScoreByGenre("fantasy");

            assertAll(
                    () -> assertEquals(3, sortedLibraryBookRepresentations.size()),
                    () -> assertEquals(bookEntity2.getTitle(), sortedLibraryBookRepresentations.get(0).getTitle()),
                    () -> assertEquals(5.0, sortedLibraryBookRepresentations.get(0).getScore()),
                    () -> assertEquals(bookEntity3.getTitle(), sortedLibraryBookRepresentations.get(1).getTitle()),
                    () -> assertEquals(3.0, sortedLibraryBookRepresentations.get(1).getScore()),
                    () -> assertEquals(bookEntity7.getTitle(), sortedLibraryBookRepresentations.get(2).getTitle()),
                    () -> assertEquals(0.0, sortedLibraryBookRepresentations.get(2).getScore())
            );
        }

        @Test
        public void should_ReturnHighestRatedBook_When_ExistsOne() throws Exception {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity, bookEntity2, bookEntity7, bookEntity5, bookEntity6, bookEntity3));

            List<BookEntity> highestRatedBookRepresentations = libraryService.getHighestRatedBook();

            assertAll(
                    () -> assertEquals(5.0, highestRatedBookRepresentations.get(0).getScore()),
                    () -> assertEquals(bookEntity2.getTitle(), highestRatedBookRepresentations.get(0).getTitle())
            );
        }
    }

    @Nested
    class SortingTests {
        @Test
        public void should_SortBooksByAuthor() {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity, bookEntity2, bookEntity7, bookEntity5, bookEntity6));

            List<BookEntity> sortedLibraryBookRepresentations = libraryService.sortBooksByAuthor();

            assertAll(
                    () -> assertEquals(bookEntity7.getAuthor(), sortedLibraryBookRepresentations.get(0).getAuthor()),
                    () -> assertEquals(bookEntity2.getAuthor(), sortedLibraryBookRepresentations.get(1).getAuthor()),
                    () -> assertEquals(bookEntity.getAuthor(), sortedLibraryBookRepresentations.get(2).getAuthor()),
                    () -> assertEquals(bookEntity6.getAuthor(), sortedLibraryBookRepresentations.get(3).getAuthor()),
                    () -> assertEquals(bookEntity5.getAuthor(), sortedLibraryBookRepresentations.get(4).getAuthor())
            );
        }

        @Test
        public void should_SortBooksByTitle() {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity, bookEntity2, bookEntity7, bookEntity5, bookEntity6));

            List<BookEntity> sortedLibraryBookRepresentations = libraryService.sortBooksByTitle();

            assertAll(
                    () -> assertEquals(bookEntity6.getTitle(), sortedLibraryBookRepresentations.get(0).getTitle()),
                    () -> assertEquals(bookEntity.getTitle(), sortedLibraryBookRepresentations.get(1).getTitle()),
                    () -> assertEquals(bookEntity2.getTitle(), sortedLibraryBookRepresentations.get(2).getTitle()),
                    () -> assertEquals(bookEntity5.getTitle(), sortedLibraryBookRepresentations.get(3).getTitle()),
                    () -> assertEquals(bookEntity7.getTitle(), sortedLibraryBookRepresentations.get(4).getTitle())
            );
        }

        @Test
        public void should_SortBooksByScoreAscending() {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity, bookEntity2, bookEntity7, bookEntity5, bookEntity6, bookEntity3));

            List<BookEntity> sortedLibraryBookRepresentations = libraryService.sortBooksByScoreAscending();

            assertAll(
                    () -> assertEquals(0.0, sortedLibraryBookRepresentations.get(0).getScore()),
                    () -> assertEquals(2.0, sortedLibraryBookRepresentations.get(1).getScore()),
                    () -> assertEquals(3.0, sortedLibraryBookRepresentations.get(2).getScore()),
                    () -> assertEquals(3.5, sortedLibraryBookRepresentations.get(3).getScore()),
                    () -> assertEquals(4.5, sortedLibraryBookRepresentations.get(4).getScore()),
                    () -> assertEquals(5.0, sortedLibraryBookRepresentations.get(5).getScore())
            );
        }

        @Test
        public void should_SortBooksByScoreDescending() {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(bookEntity, bookEntity2, bookEntity7, bookEntity5, bookEntity6, bookEntity3));

            List<BookEntity> sortedLibraryBookRepresentations = libraryService.sortBooksByScoreDescending();

            assertAll(
                    () -> assertEquals(5.0, sortedLibraryBookRepresentations.get(0).getScore()),
                    () -> assertEquals(4.5, sortedLibraryBookRepresentations.get(1).getScore()),
                    () -> assertEquals(3.5, sortedLibraryBookRepresentations.get(2).getScore()),
                    () -> assertEquals(3.0, sortedLibraryBookRepresentations.get(3).getScore()),
                    () -> assertEquals(2.0, sortedLibraryBookRepresentations.get(4).getScore()),
                    () -> assertEquals(0.0, sortedLibraryBookRepresentations.get(5).getScore())
            );
        }

    }

    @Nested
    class ExceptionTests {

//        @Test
        public void should_ThrowExceptionOnGettingByGenre_When_GenreDoesNotExist() {
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    libraryService.getBooksByGenre("fantasyyy"));
            assertEquals("No genre fantasyyy in a library.", exception.getMessage());
        }

        @Test
        public void should_ThrowExceptionOnAddingDuplicateBookToLibrary_When_BookAlreadyExists() {
            when(bookRepository.findBySignature("F01")).thenReturn(bookEntity);

            Exception exception = assertThrows(Exception.class, () -> libraryService.checkSignatureAndAddBook(bookEntity));

            assertEquals("Book with provided signature F01 already in a library.", exception.getMessage());
        }

        @Test
        public void should_ThrowExceptionOnRemoving_When_BookDoesNotExist() {
            when(bookRepository.findById(3)).thenThrow(new NoSuchElementException("No requested book with id=8 in a library."));

            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    libraryService.checkIdAndRemoveBook(3));

            assertEquals("No requested book with id=8 in a library.", exception.getMessage());
        }


        @Test
        public void should_ThrowExceptionOnUpdating_When_BookIdDoesNotExist() {
            when(bookRepository.findById(5)).thenReturn(null);

            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    libraryService.checkIdAndUpdateBook(5, bookEntity5));

            assertEquals("No requested book with id=5 in a library.", exception.getMessage());
        }

        @Test
        public void should_ThrowExceptionOnGettingByTitle_When_TitleDoesNotExist() {
            when(bookRepository.findAllByTitle("unknown")).thenReturn(Collections.emptyList());

            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    libraryService.getBookByTitle("unknown"));

            assertEquals("No requested book with title unknown in a library.", exception.getMessage());
        }

        @Test
        public void should_ThrowExceptionOnHighestRatedBook_When_NoneIsRated() {
            when(bookRepository.findAll()).thenReturn(Collections.singletonList(bookEntity7));

            Exception exception = assertThrows(Exception.class, () ->
                    libraryService.getHighestRatedBook());

            assertEquals("Couldn't get the highest rated book. All rate to 0.0", exception.getMessage());
        }

        @Test
        public void should_ThrowExceptionMostPopularBook_When_ThereIsAny() {
            when(bookRepository.findAll()).thenReturn(Collections.singletonList(bookEntity));

            Exception exception = assertThrows(Exception.class, () ->
                    libraryService.getMostPopularBook());

            assertEquals("Couldn't get the most popular book. No votes yet.", exception.getMessage());
        }
    }
}