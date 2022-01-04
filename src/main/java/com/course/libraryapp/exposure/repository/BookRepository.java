package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.exposure.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
}
