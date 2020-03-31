package org.acme.quickstart;

import com.mongodb.client.MongoClient;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMongoObject() {
        final Document id = new Document("_id",
                new ObjectId("cafebabe0123456789012345"));
        final Document updateOperation = new Document("$inc",
                new Document("counter", 1));
        mongoClient
                .getDatabase("hello")
                .getCollection("hello").updateOne(id, updateOperation,
                        new UpdateOptions().upsert(true));
        Document hello = mongoClient.getDatabase("hello")
                .getCollection("hello")
                .find(id).first();
        return hello.toJson();
    }

}