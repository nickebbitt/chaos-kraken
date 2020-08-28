package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component

@Component
class ThreadBomb : Simulation {

    override suspend fun run() {
        while (true) {
            val thread = Thread { while (true); }
            thread.start()
        }
    }
}
