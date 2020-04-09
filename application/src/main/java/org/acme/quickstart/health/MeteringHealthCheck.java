package org.acme.quickstart.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.metrics.MetricFilter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class MeteringHealthCheck implements ApplicationHealthCheck {

    @ConfigProperty(name = "metering.healthcheck.enabled", defaultValue = "false")
    private boolean meteringHealthCheckEnabled;
    @Inject
    MetricRegistry metricRegistry;

    @Override
    public HealthCheckResponse call() {
        var responseBuilder = HealthCheckResponse.named("Metering health check");
        if (!meteringHealthCheckEnabled) {
            responseBuilder.withData(ENABLED_KEY, false);
            responseBuilder.up();
        } else {
            responseBuilder.withData(ENABLED_KEY, true);
            System.out.println(metricRegistry.getMetricIDs());
            System.out.println(metricRegistry.getGauges());
            System.out.println(metricRegistry.getMetrics());
            responseBuilder.up();
        }
        return responseBuilder.build();
    }
}