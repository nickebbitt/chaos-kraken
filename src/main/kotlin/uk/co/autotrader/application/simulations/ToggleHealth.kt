package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import uk.co.autotrader.application.HealthCheck

@Component("toggle-service-health")
class ToggleHealth(private val healthCheck: HealthCheck) : Simulation {
    override suspend fun run() {
        healthCheck.healthy = !healthCheck.healthy
    }
}
