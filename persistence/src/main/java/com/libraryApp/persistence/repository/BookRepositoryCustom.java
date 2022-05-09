package com.libraryApp.persistence.repository;

import com.libraryApp.persistence.model.BookEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepositoryCustom {

    BookEntity saveCustomized(BookEntity bookEntity);
}
