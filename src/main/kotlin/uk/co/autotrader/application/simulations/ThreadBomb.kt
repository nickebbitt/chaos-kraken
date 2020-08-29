package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component

@Component
class ThreadBomb : Simulation {

    override suspend fun run(params: Map<String, String>) {
        while (true) {
            val thread = Thread { while (true); }
            thread.start()
        }
    }
}
