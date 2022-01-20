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

    private List<BookRepresentation> bookRepresentations;
    private final BookRepository bookRepository;

    @Autowired
    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookRepresentations = new ArrayList<>();
    }

    public List<BookRepresentation> checkSignatureAndAddBook(BookRepresentation bookRepresentation) throws Exception {
        BookEntity bookEntity = mapRepToEntity(bookRepresentation);
        if (isInLibrary(bookEntity)) {
            throw new Exception("Book with provided signature " + bookEntity.getSignature() + " already in a library.");
        } else {
            bookRepository.save(bookEntity);
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
        BookEntity bookEntity = mapRepToEntity(bookRepresentation);
        bookRepository.delete(bookEntity);
        return null;
    }


    //dodac sprawdzenie sygnatury
    public BookRepresentation checkIdAndUpdateBook(int bookId, BookRepresentation newBookRepresentation) {
        getBookById(bookId);
        newBookRepresentation.setId(bookId);
        BookEntity updatedBookEntity = mapRepToEntity(newBookRepresentation);
        bookRepository.save(updatedBookEntity);
        return newBookRepresentation;
    }

    public List<BookRepresentation> getAllBooks() {
        return findAllBooksAndMapToRep();
    }

    public List<BookRepresentation> getBookByTitle(String title) {
        List<BookEntity> listOfBooksEntitiesByTitle = bookRepository.findAllByTitle(title);
        if (listOfBooksEntitiesByTitle.isEmpty()) {
            throw new NoSuchElementException("No requested book with title " + title + " in a library.");
        } else {
            return listOfBooksEntitiesByTitle.stream().map(this::mapEntityToRep).collect(Collectors.toList());
        }
    }

    public BookRepresentation getBookById(int id) {
        BookEntity bookEntity = bookRepository.findById(id);
        if (bookEntity != null) {
            return mapEntityToRep(bookEntity);
        } else {
            throw new NoSuchElementException("No requested book with id=" + id + " in a library.");
        }
    }

    public List<BookRepresentation> getBooksByGenre(String genre) {
        List<BookEntity> bookEntities = bookRepository.findAllByGenre(genre);
        return bookEntities.stream().map(this::mapEntityToRep).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByAuthor() {
        findAllBooksAndMapToRep();

        return getSortedFunction(bookRepresentations, BookRepresentation::getAuthor).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByTitle() {
        findAllBooksAndMapToRep();

        return getSortedFunction(bookRepresentations, BookRepresentation::getTitle).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByScoreAscending() {
        findAllBooksAndMapToRep();

        return getSortedDoubleFunction(bookRepresentations, BookRepresentation::getScore).collect(Collectors.toList());
    }

    public List<BookRepresentation> sortBooksByScoreDescending() {
        findAllBooksAndMapToRep();

        return getSortedReversedFunction(bookRepresentations, BookRepresentation::getScore).collect(Collectors.toList());
    }

    public List<BookRepresentation> getMostPopularBook() throws Exception {
        findAllBooksAndMapToRep();

        int mostVoted = findMostVotesNumber(bookRepresentations);
        if (mostVoted == 0) {
            throw new Exception("Couldn't get the most popular book. No votes yet.");
        } else {
            List<BookRepresentation> mostVotedBookRepresentations = bookRepresentations.stream().filter(book -> book.getScoreRegistry().size() == mostVoted).collect(Collectors.toList());
            return mostVotedBookRepresentations;
        }
    }

    public List<BookRepresentation> getSortedScoreByGenre(String genre) {
        List<BookEntity> bookEntities = bookRepository.findAllByGenre(genre);
        List<BookRepresentation> bookRepresentations = bookEntities.stream().map(this::mapEntityToRep).collect(Collectors.toList());

        return getBookPredicate(bookRepresentations, book -> book.getGenre().equalsIgnoreCase(genre))
                .sorted(Comparator.comparing(BookRepresentation::getScore).reversed()).collect(Collectors.toList());
    }

    public List<BookRepresentation> getHighestRatedBook() throws Exception {
        findAllBooksAndMapToRep();

        double highestRate = findHighestRate(bookRepresentations);
        if (highestRate == 0.0) {
            throw new Exception("Couldn't get the highest rated book. All rate to 0.0");
        } else {
            List<BookRepresentation> highestRatedBookRepresentations = bookRepresentations.stream().filter(book -> book.getScore() == highestRate).collect(Collectors.toList());
            return highestRatedBookRepresentations;
        }
    }

    public BookRepresentation checkIdAndRateABook(int bookId, int rate) {
        BookRepresentation bookRepresentationToRate = getBookById(bookId);
        calculateAverageScoreAndSetOnBook(bookRepresentationToRate, rate);
        BookEntity bookEntity = mapRepToEntity(bookRepresentationToRate);
        bookRepository.save(bookEntity);
        return bookRepresentationToRate;
    }

    private boolean isInLibrary(BookEntity bookEntity) {
        BookEntity book = bookRepository.findBySignature(bookEntity.getSignature());
        return book != null;
    }

    private Stream<BookRepresentation> getBookPredicate(List<BookRepresentation> bookRepresentations, Predicate<BookRepresentation> predicate) {
        return bookRepresentations.stream().filter(predicate);
    }

    private Stream<BookRepresentation> getSortedFunction(List<BookRepresentation> bookRepresentations, Function<BookRepresentation, String> function) {
        return bookRepresentations.stream().sorted(Comparator.comparing(function));
    }

    private Stream<BookRepresentation> getSortedDoubleFunction(List<BookRepresentation> bookRepresentations, Function<BookRepresentation, Double> function) {
        return bookRepresentations.stream().sorted(Comparator.comparing(function));
    }

    private Stream<BookRepresentation> getSortedReversedFunction(List<BookRepresentation> bookRepresentations, Function<BookRepresentation, Double> function) {
        return bookRepresentations.stream().sorted(Comparator.comparing(function).reversed());
    }

    private int findMostVotesNumber(List<BookRepresentation> bookRepresentations) {
        Optional<BookRepresentation> optionalBook = bookRepresentations.stream().max(Comparator.comparing(book -> book.getScoreRegistry().size()));
        int mostVotes = -1;
        if (optionalBook.isPresent()) {
            mostVotes = optionalBook.get().getScoreRegistry().size();
        }
        return mostVotes;
    }

    private double findHighestRate(List<BookRepresentation> bookRepresentations) {
        Optional<BookRepresentation> optionalBook = bookRepresentations.stream().max(Comparator.comparing(BookRepresentation::getScore));
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

    private BookEntity mapRepToEntity(BookRepresentation bookRepresentation) {
        return BookMapper.INSTANCE.bookRepToEntity(bookRepresentation);
    }

    private BookRepresentation mapEntityToRep(BookEntity bookEntity) {
        return BookMapper.INSTANCE.entityToBookRep(bookEntity);
    }

    private List<BookRepresentation> findAllBooksAndMapToRep() {
        List<BookEntity> bookEntities = bookRepository.findAll();
        bookRepresentations = bookEntities.stream().map(this::mapEntityToRep).collect(Collectors.toList());
        return bookRepresentations;
    }

}
