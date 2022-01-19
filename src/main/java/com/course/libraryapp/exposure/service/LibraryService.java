package com.course.libraryapp.exposure.service;

import com.course.libraryapp.exposure.mapper.BookMapper;
import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.exposure.repository.BookRepository;
import com.course.libraryapp.persistance.model.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final List<BookRepresentation> bookRepresentations;
    private final BookRepository bookRepository;

    @Autowired
    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookRepresentations = new ArrayList<>();
    }

    public List<BookRepresentation> checkSignatureAndAddBook(BookRepresentation bookRepresentation) throws Exception {
        if (isInLibrary(bookRepresentation)) {
            throw new Exception("Book with provided signature " + bookRepresentation.getSignature() + " already in a library.");
        } else {
            this.bookRepresentations.add(bookRepresentation);
//            BookEntity bookEntity = BookMapper.INSTANCE.bookRepToEntity(bookRepresentation);
//            bookRepository.save(bookEntity);
            return this.bookRepresentations;
        }
    }

    public List<BookRepresentation> checkSignaturesAndAddBooks(List<BookRepresentation> booksToAdd) throws Exception {
        for (BookRepresentation bookRepresentation : booksToAdd) {
            checkSignatureAndAddBook(bookRepresentation);
        }
        return booksToAdd;
    }

    public List<BookRepresentation> checkIdAndRemoveBook(int bookId) {
        BookRepresentation bookRepresentation = getBookById(bookId);
//        BookEntity bookEntity = BookMapper.INSTANCE.bookRepToEntity(bookRepresentation);
//        bookRepository.delete(bookEntity);
        this.bookRepresentations.remove(bookRepresentation);
        return this.bookRepresentations;
    }

    public BookRepresentation checkIdAndUpdateBook(int bookId, BookRepresentation newBookRepresentation) {
        BookRepresentation bookRepresentationToBeUpdated = getBookById(bookId);
        newBookRepresentation.setId(bookId);
        this.bookRepresentations.set(bookRepresentations.indexOf(bookRepresentationToBeUpdated), newBookRepresentation);
        return this.bookRepresentations.get(bookRepresentations.indexOf(newBookRepresentation));
    }

    public List<BookRepresentation> getAllBooks() {
        return this.bookRepresentations;
    }

    public List<BookRepresentation> getBookByTitle(String title) {
        List<BookRepresentation> listOfBooksByTitle = getBookPredicate(book -> book.getTitle().equalsIgnoreCase(title)).collect(Collectors.toList());
//        List<BookRepresentation> listOfBooksByTitle = bookRepository.findByTitle(title);
        if (listOfBooksByTitle.isEmpty()) {
            throw new NoSuchElementException("No requested book with title " + title + " in a library.");
        } else {
            return listOfBooksByTitle;
        }
    }

    public BookRepresentation getBookById(int id) {
        Optional<BookRepresentation> optionalBook = getBookPredicate(book -> book.getId() == id).findAny();
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new NoSuchElementException("No requested book with id=" + id + " in a library.");
        }
    }

    public List<BookRepresentation> getBooksByGenre(String genre) {
        return getBookPredicate(book -> book.getGenre().equalsIgnoreCase(genre)).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByAuthor() {
        return getSortedFunction(BookRepresentation::getAuthor).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByTitle() {
        return getSortedFunction(BookRepresentation::getTitle).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByScoreAscending() {
        return getSortedDoubleFunction(BookRepresentation::getScore).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByScoreDescending() {
        return getSortedReversedFunction(BookRepresentation::getScore).collect(Collectors.toList());
    }

    public List<BookRepresentation> getMostPopularBook() throws Exception {
        int mostVoted = findMostVotesNumber();
        List<BookRepresentation> mostVotedBookRepresentations = this.bookRepresentations.stream().filter(book -> book.getScoreRegistry().size() == mostVoted).collect(Collectors.toList());
        if (mostVotedBookRepresentations.isEmpty()) {
            throw new Exception("Couldn't get the most popular book.");
        } else {
            return mostVotedBookRepresentations;
        }
    }

    public List<BookRepresentation> getSortedScoreByGenre(String genre) {
        return getBookPredicate(book -> book.getGenre().equalsIgnoreCase(genre))
                .sorted(Comparator.comparing(BookRepresentation::getScore).reversed()).collect(Collectors.toList());
    }

    public List<BookRepresentation> getHighestRatedBook() throws Exception {
        double highestRate = findHighestRate();
        List<BookRepresentation> highestRatedBookRepresentations = this.bookRepresentations.stream().filter(book -> book.getScore() == highestRate).collect(Collectors.toList());
        if (highestRatedBookRepresentations.isEmpty()) {
            throw new Exception("Couldn't get the highest rated book.");
        } else {
          return highestRatedBookRepresentations;
        }
    }

    public BookRepresentation checkIdAndRateABook(int bookId, int rate) {
        BookRepresentation bookRepresentationToRate = getBookById(bookId);
        calculateAverageScoreAndSetOnBook(bookRepresentationToRate, rate);
        return bookRepresentationToRate;
    }

    private boolean isInLibrary(BookRepresentation bookRepresentation) {
//        BookRepresentation bookRep = bookRepository.findBySignature(bookRepresentation.getSignature());
//        return bookRep != null;
        return this.bookRepresentations.stream().anyMatch(bookItem -> bookItem.getSignature().equals(bookRepresentation.getSignature()));
    }

    private Stream<BookRepresentation> getBookPredicate(Predicate<BookRepresentation> predicate) {
        return this.bookRepresentations.stream().filter(predicate);
    }

    private Stream<BookRepresentation> getSortedFunction(Function<BookRepresentation, String> function) {
        return this.bookRepresentations.stream().sorted(Comparator.comparing(function));
    }

    private Stream<BookRepresentation> getSortedDoubleFunction(Function<BookRepresentation, Double> function) {
        return this.bookRepresentations.stream().sorted(Comparator.comparing(function));
    }

    private Stream<BookRepresentation> getSortedReversedFunction(Function<BookRepresentation, Double> function) {
        return this.bookRepresentations.stream().sorted(Comparator.comparing(function).reversed());
    }

    private int findMostVotesNumber() {
        Optional<BookRepresentation> optionalBook = this.bookRepresentations.stream().max(Comparator.comparing(book -> book.getScoreRegistry().size()));
        int mostVotes = -1;
        if (optionalBook.isPresent()) {
            mostVotes = optionalBook.get().getScoreRegistry().size();
        }
        return mostVotes;
    }

    private double findHighestRate() {
        Optional<BookRepresentation> optionalBook = this.bookRepresentations.stream().max(Comparator.comparing(BookRepresentation::getScore));
        double highestRate = -1;
        if (optionalBook.isPresent()) {
            highestRate = optionalBook.get().getScore();
            return highestRate;
        }
        return highestRate;
    }

    private void calculateAverageScoreAndSetOnBook(BookRepresentation bookRepresentation, int rate) {
        bookRepresentation.getScoreRegistry().add(rate);
        DoubleSummaryStatistics scoreSummary = bookRepresentation.getScoreRegistry().stream().mapToDouble(value -> value).summaryStatistics();
        double newScore = scoreSummary.getAverage();
        double newScoreRounded = Math.round(newScore * 100.0) / 100.0;
        bookRepresentation.setScore(newScoreRounded);
    }
}
