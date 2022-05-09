package com.libraryApp.persistence.repository;

import com.libraryApp.persistence.model.GenreEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends MongoRepository<GenreEntity, String> {
}
