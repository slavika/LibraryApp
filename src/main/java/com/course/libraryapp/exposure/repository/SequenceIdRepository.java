package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.SequenceIdEntity;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public interface SequenceIdRepository extends MongoRepository<SequenceIdEntity, Integer> {

    default int getNextSequenceNumber(String uri) {
        MongoCollection<Document> counterCollection = connectToDbAndGetCounterCollection(uri);

        String sequenceNumberField = "seq";
        Bson updates = Updates.inc(sequenceNumberField, 1);
        UpdateOptions options = new UpdateOptions().upsert(true);
        Document query = new Document().append("_id", "bookId");
        counterCollection.updateOne(query, updates, options);

        int sequenceNumber = (int) Objects.requireNonNull(counterCollection.find(Filters.eq("_id", "bookId"))
                .first()).get(sequenceNumberField);
        return sequenceNumber;
    }

    private MongoCollection<Document> connectToDbAndGetCounterCollection(String uri){
        String sequenceCollection = "counters";
        String databaseName = "lib";

        MongoClientURI mongoClientURI = new MongoClientURI(uri);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(sequenceCollection);
    }
}
