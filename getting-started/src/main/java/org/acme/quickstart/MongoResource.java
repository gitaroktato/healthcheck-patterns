package org.acme.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mongo")
public class MongoResource {

    @Inject
    private MongoClient mongoClient;
    private static final String COLLECTION_ID = "hello";
    private static final Document ID = new Document("_id",
            new ObjectId("cafebabe0123456789012345"));

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMongoObject() {
        incrementCounter();
        Document counter = getCounter();
        return counter.toJson();
    }

    private Document getCounter() {
        return getCollection().find(ID).first();
    }

    private void incrementCounter() {
        final Document updateOperation = new Document("$inc",
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