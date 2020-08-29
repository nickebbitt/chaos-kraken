package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*
import java.util.stream.IntStream

@Component
class WasteCpu : Simulation {

    override suspend fun run(options: SimulationOptions?) {
        IntStream.range(0, Runtime.getRuntime().availableProcessors())
                .forEach { _ -> Thread { this.hashRandomBytes() }.start() }
    }

    private fun hashRandomBytes() {
        while (true) {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(UUID.randomUUID().toString().toByteArray())
        }
    }
}
