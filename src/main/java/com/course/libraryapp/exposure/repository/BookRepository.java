package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.persistance.model.BookEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<BookEntity, Long> {

    List<BookRepresentation> findByTitle(String title);

    BookRepresentation findBySignature(String signature);

}
