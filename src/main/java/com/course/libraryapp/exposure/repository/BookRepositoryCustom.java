package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.BookEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepositoryCustom {

    BookEntity saveCustomized(BookEntity bookEntity);
}
