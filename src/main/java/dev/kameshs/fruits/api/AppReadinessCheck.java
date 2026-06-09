package dev.kameshs.fruits.api;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Readiness gate driven by config `app.ready` (default true).
 * The %regression profile sets app.ready=false so the canary reports NOT ready,
 * which makes K8sCanaryDeploy fail its steady-state wait and triggers auto-rollback.
 */
@Readiness
@ApplicationScoped
public class AppReadinessCheck implements HealthCheck {

    @ConfigProperty(name = "app.ready", defaultValue = "true")
    boolean ready;

    @Override
    public HealthCheckResponse call() {
        return ready
            ? HealthCheckResponse.up("app-readiness")
            : HealthCheckResponse.down("app-readiness");
    }
}
