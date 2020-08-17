package uk.co.autotrader.application

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*
import java.util.stream.IntStream

interface Simulation {
    suspend fun run()
}

@Component
class Cpu : Simulation {

    override suspend fun run() {
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
