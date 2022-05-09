package com.libraryApp.service;

import com.libraryApp.persistence.model.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import com.libraryApp.persistence.repository.BookRepository;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ApplicationScope
public class LibraryService {

    private List<BookEntity> bookEntities;
    private final BookRepository bookRepository;

    @Autowired
    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookEntities = new ArrayList<>();
    }

    public BookEntity checkSignatureAndAddBook(BookEntity bookEntity) throws Exception {
        if (isInLibraryBySignature(bookEntity)) {
            throw new Exception("Book with provided signature " + bookEntity.getSignature() + " already in a library.");
        } else {
            bookRepository.saveCustomized(bookEntity);
            return bookEntity;
        }
    }

    public List<BookEntity> checkSignaturesAndAddBooks(List<BookEntity> booksToAdd) throws Exception {
        for (BookEntity bookEntity : booksToAdd) {
            checkSignatureAndAddBook(bookEntity);
        }
        return booksToAdd;
    }

    public List<BookEntity> checkIdAndRemoveBook(int bookId) {
        BookEntity bookEntity = getBookById(bookId);
        bookRepository.delete(bookEntity);
        return null;
    }

    // TODO dodac sprawdzenie sygnatury
    public BookEntity checkIdAndUpdateBook(int bookId, BookEntity newBookEntity) {
        getBookById(bookId);
        newBookEntity.setId(bookId);
        bookRepository.save(newBookEntity);
        return newBookEntity;
    }

    public List<BookEntity> getAllBooks() {
        return findAllBooks();
    }

    public List<BookEntity> getBookByTitle(String title) {
        List<BookEntity> listOfBooksEntitiesByTitle = bookRepository.findAllByTitle(title);
        if (listOfBooksEntitiesByTitle.isEmpty()) {
            throw new NoSuchElementException("No requested book with title " + title + " in a library.");
        } else {
            return listOfBooksEntitiesByTitle;
        }
    }

    public BookEntity getBookById(int id) {
        BookEntity bookEntity = bookRepository.findById(id);
        if (bookEntity != null) {
            return bookEntity;
        } else {
            throw new NoSuchElementException("No requested book with id=" + id + " in a library.");
        }
    }

    public List<BookEntity> getBooksByGenre(String genre) {
        try {
            return bookRepository.findAllByGenre(genre);
        } catch (Exception e) {
            throw new NoSuchElementException("No genre " + genre + " in a library.");
        }
    }

    public List<BookEntity> sortBooksByAuthor() {
        List<BookEntity> bookEntities = findAllBooks();

        return getSortedFunction(bookEntities, BookEntity::getAuthor).collect(Collectors.toList());
    }

    public List<BookEntity> sortBooksByTitle() {
        List<BookEntity> bookEntities = findAllBooks();

        return getSortedFunction(bookEntities, BookEntity::getTitle).collect(Collectors.toList());
    }

    public List<BookEntity> sortBooksByScoreAscending() {
        List<BookEntity> bookEntities = findAllBooks();

        return getSortedDoubleFunction(bookEntities, BookEntity::getScore).collect(Collectors.toList());
    }

    public List<BookEntity> sortBooksByScoreDescending() {
        List<BookEntity> bookEntities = findAllBooks();

        return getSortedReversedFunction(bookEntities, BookEntity::getScore).collect(Collectors.toList());
    }

    public List<BookEntity> getMostPopularBook() throws Exception {
        List<BookEntity> bookEntities = findAllBooks();

        int mostVoted = findMostVotesNumber(bookEntities);
        if (mostVoted == 0) {
            throw new Exception("Couldn't get the most popular book. No votes yet.");
        } else {
            List<BookEntity> mostVotedBookRepresentations = bookEntities.stream().filter(book -> book.getScoreRegistry().size() == mostVoted).collect(Collectors.toList());
            return mostVotedBookRepresentations;
        }
    }

    public List<BookEntity> getSortedScoreByGenre(String genre) {
        List<BookEntity> bookEntities = bookRepository.findAllByGenre(genre);

        return getBookPredicate(bookEntities, book -> book.getGenre().equalsIgnoreCase(genre))
                .sorted(Comparator.comparing(BookEntity::getScore).reversed()).collect(Collectors.toList());
    }

    public List<BookEntity> getHighestRatedBook() throws Exception {
        List<BookEntity> bookEntities = findAllBooks();

        double highestRate = findHighestRate(bookEntities);
        if (highestRate == 0.0) {
            throw new Exception("Couldn't get the highest rated book. All rate to 0.0");
        } else {
            List<BookEntity> highestRatedBookRepresentations = bookEntities.stream().filter(book -> book.getScore() == highestRate).collect(Collectors.toList());
            return highestRatedBookRepresentations;
        }
    }

    public BookEntity checkIdAndRateABook(int bookId, int rate) {
        BookEntity bookEntityToRate = getBookById(bookId);
        calculateAverageScoreAndSetOnBook(bookEntityToRate, rate);
        bookRepository.save(bookEntityToRate);
        return bookEntityToRate;
    }

    private boolean isInLibraryBySignature(BookEntity bookEntity) {
        BookEntity book = bookRepository.findBySignature(bookEntity.getSignature());
        return book != null;
    }

    private Stream<BookEntity> getBookPredicate(List<BookEntity> bookEntities, Predicate<BookEntity> predicate) {
        return bookEntities.stream().filter(predicate);
    }

    private Stream<BookEntity> getSortedFunction(List<BookEntity> bookEntities, Function<BookEntity, String> function) {
        return bookEntities.stream().sorted(Comparator.comparing(function));
    }

    private Stream<BookEntity> getSortedDoubleFunction(List<BookEntity> bookEntities, Function<BookEntity, Double> function) {
        return bookEntities.stream().sorted(Comparator.comparing(function));
    }

    private Stream<BookEntity> getSortedReversedFunction(List<BookEntity> bookEntities, Function<BookEntity, Double> function) {
        return bookEntities.stream().sorted(Comparator.comparing(function).reversed());
    }

    private int findMostVotesNumber(List<BookEntity> bookEntities) {
        Optional<BookEntity> optionalBook = bookEntities.stream().max(Comparator.comparing(book -> book.getScoreRegistry().size()));
        int mostVotes = -1;
        if (optionalBook.isPresent()) {
            mostVotes = optionalBook.get().getScoreRegistry().size();
        }
        return mostVotes;
    }

    private double findHighestRate(List<BookEntity> bookEntities) {
        Optional<BookEntity> optionalBook = bookEntities.stream().max(Comparator.comparing(BookEntity::getScore));
        double highestRate = -1;
        if (optionalBook.isPresent()) {
            highestRate = optionalBook.get().getScore();
            return highestRate;
        }
        return highestRate;
    }

    private void calculateAverageScoreAndSetOnBook(BookEntity bookEntity, int rate) {
        bookEntity.getScoreRegistry().add(rate);
        DoubleSummaryStatistics scoreSummary = bookEntity.getScoreRegistry().stream().mapToDouble(value -> value).summaryStatistics();
        double newScore = scoreSummary.getAverage();
        double newScoreRounded = Math.round(newScore * 100.0) / 100.0;
        bookEntity.setScore(newScoreRounded);
    }

//    private BookEntity mapRepToEntity(BookRepresentation bookRepresentation) {
//        return BookMapper.INSTANCE.bookRepToEntity(bookRepresentation);
//    }
//
//    private BookRepresentation mapEntityToRep(BookEntity bookEntity) {
//        return BookMapper.INSTANCE.entityToBookRep(bookEntity);
//    }
//
    private List<BookEntity> findAllBooks() {
        List<BookEntity> bookEntities = bookRepository.findAll();
        return bookEntities;
    }

}
