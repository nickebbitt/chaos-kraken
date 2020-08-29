package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import uk.co.autotrader.application.HealthCheck

@Component
class ToggleHealth(private val healthCheck: HealthCheck) : Simulation {
    override suspend fun run(options: SimulationOptions?) {
        healthCheck.healthy = !healthCheck.healthy
    }
}
