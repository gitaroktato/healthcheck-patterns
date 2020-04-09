package org.acme.quickstart.health;

import org.acme.quickstart.service.CounterService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class ProbingHealthCheck implements ApplicationHealthCheck {

    @ConfigProperty(name = "probing.healthcheck.enabled", defaultValue = "false")
    private boolean probingHealthCheckEnabled;
    @Inject
    CounterService counterService;

    @Override
    public HealthCheckResponse call() {
        var responseBuilder = HealthCheckResponse.named("Probing health check");
        if (!probingHealthCheckEnabled) {
            responseBuilder.withData(ENABLED_KEY, false);
            responseBuilder.up();
        } else {
            var counter = probe();
            responseBuilder.withData(ENABLED_KEY, true);
            responseBuilder.withData("Successful transactions", counter);
            responseBuilder.up();
        }
        return responseBuilder.build();
    }

    private int probe() {
        var counter = counterService.incrementAndGetCounter();
        return counter.getInteger("counter");
    }
}