package org.acme.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.acme.quickstart.service.CounterService;
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
    private CounterService counterService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMongoObject() {
        var counter = counterService.incrementAndGetCounter();
        return counter.toJson();
    }

}