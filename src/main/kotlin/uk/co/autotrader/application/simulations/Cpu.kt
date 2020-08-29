package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*
import java.util.stream.IntStream

@Component
class Cpu : Simulation {

    override suspend fun run(params: Map<String, String>) {
        IntStream.range(0, Runtime.getRuntime().availableProcessors())
                .forEach { _ -> Thread(Runnable { this.hashRandomBytes() }).start() }
    }

    private fun hashRandomBytes() {
        while (true) {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(UUID.randomUUID().toString().toByteArray())
        }
    }
}
