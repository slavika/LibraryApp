package com.course.libraryapp.exposure.service;

import com.course.libraryapp.exposure.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ApplicationScope
public class LibraryService {

    private final List<Book> books;

    public LibraryService() {
        this.books = new ArrayList<>();
    }

    public List<Book> checkSignatureAndAddBook(Book book) throws Exception {
        if (isInLibrary(book)) {
            throw new Exception("Book with provided signature " + book.getSignature() + " already in a library.");
        } else {
            this.books.add(book);
            return this.books;
        }
    }

    public List<Book> checkSignaturesAndAddBooks(List<Book> booksToAdd) throws Exception {
        for (Book book : booksToAdd) {
            checkSignatureAndAddBook(book);
        }
        return booksToAdd;
    }

    public List<Book> checkIdAndRemoveBook(int bookId) {
        Book book = getBookById(bookId);
        this.books.remove(book);
        return this.books;
    }

    public Book checkIdAndUpdateBook(int bookId, Book newBook) {
        Book book = getBookById(bookId);
        newBook.setId(bookId);
        this.books.set(books.indexOf(book), newBook);
        return this.books.get(books.indexOf(newBook));
    }

    public List<Book> getAllBooks() {
        return this.books;
    }

    public Book getBookByTitle(String title) {
        Optional<Book> optionalBook = getBookPredicate(book -> book.getTitle().equals(title)).findAny();
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new NoSuchElementException("No requested book with title " + title + " in a library.");
        }
    }

    public Book getBookById(int id) {
        Optional<Book> optionalBook = getBookPredicate(book -> book.getId() == id).findAny();
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new NoSuchElementException("No requested book with id=" + id + " in a library.");
        }
    }

    public List<Book> getBooksByGenre(String genre) {
        return getBookPredicate(book -> book.getGenre().equals(genre)).collect(Collectors.toList());
    }

    public List<Book> sortBooksByAuthor() {
        return getSortedFunction(Book::getAuthor).collect(Collectors.toList());
    }

    public List<Book> sortBooksByTitle() {
        return getSortedFunction(Book::getTitle).collect(Collectors.toList());
    }

    public List<Book> sortBooksByScoreAscending() {
        return getSortedDoubleFunction(Book::getScore).collect(Collectors.toList());
    }

    public List<Book> sortBooksByScoreDescending() {
        return getSortedReversedFunction(Book::getScore).collect(Collectors.toList());
    }

    public Book getMostPopularBook() throws Exception {
        Optional<Book> optionalBook = this.books.stream().max(Comparator.comparing(book -> book.getScoreRegistry().size()));
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new Exception("Couldn't get the most popular book.");
        }
    }

    public List<Book> getSortedScoreByGenre(String genre) {
        return getBookPredicate(book -> book.getGenre().equals(genre))
                .sorted(Comparator.comparing(Book::getScore).reversed()).collect(Collectors.toList());
    }

    public Book getHighestRatedBook() throws Exception {
        Optional<Book> optionalBook = this.books.stream().max(Comparator.comparing(Book::getScore));
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new Exception("Couldn't get the highest rated book.");
        }
    }

    public Book checkIdAndRateABook(int bookId, int rate) {
        Book bookToRate = getBookById(bookId);
        calculateAverageScoreAndSetOnBook(bookToRate, rate);
        return bookToRate;
    }

    private boolean isInLibrary(Book book) {
        return this.books.stream().anyMatch(bookItem -> bookItem.getSignature().equals(book.getSignature()));
    }

    private Stream<Book> getBookPredicate(Predicate<Book> predicate) {
        return this.books.stream().filter(predicate);
    }

    private Stream<Book> getSortedFunction(Function<Book, String> function) {
        return this.books.stream().sorted(Comparator.comparing(function));
    }

    private Stream<Book> getSortedDoubleFunction(Function<Book, Double> function) {
        return this.books.stream().sorted(Comparator.comparing(function));
    }

    private Stream<Book> getSortedReversedFunction(Function<Book, Double> function) {
        return this.books.stream().sorted(Comparator.comparing(function).reversed());
    }

    private void calculateAverageScoreAndSetOnBook(Book book, int rate) {
        book.getScoreRegistry().add(rate);
        DoubleSummaryStatistics scoreSummary = book.getScoreRegistry().stream().mapToDouble(value -> value).summaryStatistics();
        double newScore = scoreSummary.getAverage();
        double newScoreRounded = Math.round(newScore * 100.0) / 100.0;
        book.setScore(newScoreRounded);
    }
}
