package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.BookEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final SequenceIdRepository sequenceIdRepository;
    private final MongoTemplate mongoTemplate;

    public BookRepositoryCustomImpl(SequenceIdRepository sequenceIdRepository, MongoTemplate mongoTemplate) {
        this.sequenceIdRepository = sequenceIdRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Override
    public void saveCustomized(BookEntity bookEntity) {
        int seq = sequenceIdRepository.getLastSequenceNumber(uri);
        bookEntity.setId(seq);
        mongoTemplate.save(bookEntity);
    }
}
