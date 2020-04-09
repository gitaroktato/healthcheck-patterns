package org.acme.quickstart.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CounterService {

    @Inject
    MongoClient mongoClient;
    private static final String COLLECTION_ID = "hello";
    private static final Document ID = new Document("_id",
            new ObjectId("cafebabe0123456789012345"));

    public Document incrementAndGetCounter() {
        incrementCounter();
        return getCounter();
    }

    private Document getCounter() {
        return getCollection().find(ID).first();
    }

    private void incrementCounter() {
        final var updateOperation = new Document("$inc",
                new Document("counter", 1));
        getCollection().updateOne(ID, updateOperation,
                new UpdateOptions().upsert(true));
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient
                .getDatabase(COLLECTION_ID)
                .getCollection(COLLECTION_ID);
    }
}
