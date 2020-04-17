package org.acme.quickstart.health;


import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Liveness
public class DiskHealthCheck implements ApplicationHealthCheck {

    @ConfigProperty(name = "disk.healthcheck.enabled", defaultValue = "true")
    boolean diskHealthCheckEnabled;
    @ConfigProperty(name = "disk.healthcheck.minimum-bytes", defaultValue = "65535")
    double failureThreshold;

    @Override
    public HealthCheckResponse call() {
        var responseBuilder = HealthCheckResponse.named("Disk health check");
        if (!diskHealthCheckEnabled) {
            responseBuilder.withData(ENABLED_KEY, false);
            responseBuilder.up();
        } else {
            try {
                var store = Files.getFileStore(Path.of("."));
                var usableSpace = store.getUsableSpace();
                responseBuilder.name("disk")
                        .withData("usable bytes", usableSpace)
                        .state(usableSpace > failureThreshold);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return responseBuilder.build();
    }
}