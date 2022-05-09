package com.libraryApp.persistence.repository;

import com.libraryApp.persistence.model.BookEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<BookEntity, Long>, BookRepositoryCustom {

    @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    List<BookEntity> findAllByTitle(String title);

    BookEntity findBySignature(String signature);

    BookEntity findById(int id);

    @Query(value = "{'genre': {$regex : ?0, $options: 'i'}}")
    List<BookEntity> findAllByGenre(String genre);
}
