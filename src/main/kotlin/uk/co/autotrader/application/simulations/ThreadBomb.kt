package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component

@Component
class ThreadBomb : Simulation {

    override suspend fun run(options: SimulationOptions?) {
        while (true) {
            val thread = Thread { while (true); }
            thread.start()
        }
    }
}
