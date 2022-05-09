package com.libraryApp.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counters")
public class SequenceIdEntity {

    @Id
    private String id;
    private int seq;

    public SequenceIdEntity(){};
}
