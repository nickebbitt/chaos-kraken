package uk.co.autotrader.application.simulations

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MemoryLeak : Simulation {
    override suspend fun run() {
        val allocatedMemory = ArrayList<ByteArray>()

        while (true) {
            try {
                allocatedMemory.add(ByteArray(ONE_KILOBYTE))
            } catch (outOfMemory: OutOfMemoryError) {
                LOG.debug("Swallowing OOM")
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MemoryLeak::class.java)
    }
}

@Component
class MemoryLeakOom : Simulation {
    override suspend fun run() {
        val allocatedMemory = ArrayList<ByteArray>()

        while (true) {
            allocatedMemory.add(ByteArray(ONE_KILOBYTE))
        }
    }
}
