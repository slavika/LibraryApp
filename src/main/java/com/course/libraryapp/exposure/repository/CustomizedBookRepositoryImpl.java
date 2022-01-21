package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomizedBookRepositoryImpl implements  CustomizedBookRepository{

    @Autowired
    SequenceIdRepository SequenceIdRepository;
    BookRepository bookRepository;

    @Override
    public void saveCustomized(BookEntity bookEntity) {
        int seq = SequenceIdRepository.getLastSequenceNumber();
        bookEntity.setId(seq);
        bookRepository.save(bookEntity);
    }
}
