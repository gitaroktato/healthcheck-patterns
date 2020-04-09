package org.acme.quickstart.rest;

import org.acme.quickstart.service.CounterService;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mongo")
public class MongoResource {

    @Inject
    CounterService counterService;
    @Inject
    @Metric(name = "incrementAndGetFailedCounter")
    Counter incrementAndGetFailed;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "incrementAndGetCounter")
    @Timed(name = "incrementAndGetTimer", unit = MetricUnits.MILLISECONDS)
    public String getMongoObject() {
        try {
            var counter = counterService.incrementAndGetCounter();
            return counter.toJson();
        } catch (RuntimeException ex) {
            incrementAndGetFailed.inc();
            throw ex;
        }
    }
}