package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class CustomizedBookRepositoryImpl implements  CustomizedBookRepository{

    @Autowired
    SequenceIdRepository SequenceIdRepository;
    BookRepository bookRepository;

    @Value( "${spring.data.mongodb.uri}" )
    private String uri;

    @Override
    public void saveCustomized(BookEntity bookEntity) {
        int seq = SequenceIdRepository.getLastSequenceNumber(uri);
        bookEntity.setId(seq);
        bookRepository.save(bookEntity);
    }
}
