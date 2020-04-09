package org.acme.quickstart.health;

import org.eclipse.microprofile.health.HealthCheck;

public interface ApplicationHealthCheck extends HealthCheck {
    static final String ENABLED_KEY = "enabled";
}
