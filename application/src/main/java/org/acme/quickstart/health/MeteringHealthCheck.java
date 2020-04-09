package org.acme.quickstart.health;

import org.acme.quickstart.rest.MongoResource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class MeteringHealthCheck implements ApplicationHealthCheck {

    @ConfigProperty(name = "metering.healthcheck.enabled", defaultValue = "false")
    private boolean meteringHealthCheckEnabled;
    @ConfigProperty(name = "metering.healthcheck.failure-threshold", defaultValue = "0.2")
    private double failureThreshold;
    @ConfigProperty(name = "metering.healthcheck.max-eviction-seconds", defaultValue = "20")
    private long maxEvictionSeconds;
    @Inject
    @Metric(name = MongoResource.METRIC_INCREMENT_AND_GET_FAILED_COUNTER, absolute = true)
    Counter incrementAndGetFailed;
    @Inject
    @Metric(name = MongoResource.METRIC_INCREMENT_AND_GET_COUNTER, absolute = true)
    Counter incrementAndGetCounter;
    @Inject
    @Metric(name = MongoResource.METRIC_LAST_INVOKED_MILLIS, absolute = true)
    Gauge<Long> lastInvokedMillis;

    @Override
    public HealthCheckResponse call() {
        var responseBuilder = HealthCheckResponse.named("Metering health check");
        if (!meteringHealthCheckEnabled) {
            responseBuilder.withData(ENABLED_KEY, false);
            responseBuilder.up();
        } else {
            responseBuilder.withData(ENABLED_KEY, true);
            var failedRatio = getFailedRatio();
            responseBuilder.withData("failed ratio", failedRatio);
            var lastCallSince = getLastCallSince();
            responseBuilder.withData("last call since(ms)", lastCallSince);
            if (failedRatio > failureThreshold
                    && lastCallSince > maxEvictionSeconds * 1000) {
                responseBuilder.down();
            } else {
                responseBuilder.up();
            }
        }
        return responseBuilder.build();
    }

    private long getLastCallSince() {
        try {
            return System.currentTimeMillis() - lastInvokedMillis.getValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    private long getFailedRatio() {
        if (incrementAndGetCounter.getCount() == 0) {
            return 0;
        }
        return incrementAndGetFailed.getCount() / incrementAndGetCounter.getCount();
    }
}