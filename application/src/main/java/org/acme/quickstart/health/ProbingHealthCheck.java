package org.acme.quickstart.health;

import io.vertx.core.json.Json;
import org.acme.quickstart.rest.MongoResource;
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
    boolean probingHealthCheckEnabled;
    @Inject
    MongoResource mongoResource;

    @Override
    public HealthCheckResponse call() {
        var responseBuilder = HealthCheckResponse.named("Probing health check");
        if (!probingHealthCheckEnabled) {
            responseBuilder.withData(ENABLED_KEY, false);
            responseBuilder.up();
        } else {
            var result = probe();
            responseBuilder.withData(ENABLED_KEY, true);
            responseBuilder.withData("result", result);
            responseBuilder.up();
        }
        return responseBuilder.build();
    }

    private String probe() {
        return mongoResource.getMongoObject();
    }
}