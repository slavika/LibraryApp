package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.BookEntity;

public interface CustomizedBookRepository {

    void saveCustomized(BookEntity bookEntity);
}
