package com.course.libraryapp.exposure.repository;

import com.course.libraryapp.persistance.model.SequenceIdEntity;
import com.mongodb.*;
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

    default int getLastSequenceNumber() {

        String sequenceCollection = "counters";
        String sequenceField = "seq";
        String databaseName = "lib";

        MongoClientURI mongoClientURI = new MongoClientURI("mongodb+srv://slavika:slavika@qacourse.ymd9j.mongodb.net");
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> seq = database.getCollection(sequenceCollection); // get the collection (this will create it if needed)

        Document query = new Document().append("_id", "bookId");

        Bson updates = Updates.inc(sequenceField, 1);
        UpdateOptions options = new UpdateOptions().upsert(true);

        seq.updateOne(query, updates, options);

        int i = (int) Objects.requireNonNull(seq.find(Filters.eq("_id", "bookId")).first()).get(sequenceField);
        return i;
    }
}
