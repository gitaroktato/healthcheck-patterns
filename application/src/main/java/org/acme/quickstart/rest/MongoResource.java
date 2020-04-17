package org.acme.quickstart.rest;

import org.acme.quickstart.service.CounterService;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/mongo")
public class MongoResource {

    public static final String METRIC_INCREMENT_AND_GET_FAILED_COUNTER = "incrementAndGetFailedCounter";
    public static final String METRIC_INCREMENT_AND_GET_COUNTER = "incrementAndGetCounter";
    public static final String METRIC_LAST_INVOKED_MILLIS = "lastInvokedMillis";

    @Inject
    CounterService counterService;
    @Inject
    @Metric(name = METRIC_INCREMENT_AND_GET_FAILED_COUNTER, absolute = true)
    Meter incrementAndGetFailed;
    private long lastInvokedMillis;
    private AtomicInteger failedCount = new AtomicInteger(0);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Metered(name = METRIC_INCREMENT_AND_GET_COUNTER, absolute = true)
    public String getMongoObject() {
        lastInvokedMillis = System.currentTimeMillis();
        try {
            var counter = counterService.incrementAndGetCounter();
            return counter.toJson();
        } catch (RuntimeException ex) {
            incrementAndGetFailed.mark(failedCount.incrementAndGet());
            throw ex;
        }
    }

    @Gauge(name= METRIC_LAST_INVOKED_MILLIS, unit = MetricUnits.NONE, absolute = true)
    public Long getLastInvokedMillis() {
        return lastInvokedMillis;
    }
}