package uk.co.autotrader.application

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.co.autotrader.application.simulations.ONE_KILOBYTE
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer

interface Failure {
    fun fail(params: Map<String, String> = emptyMap())
}

private val LOG = LoggerFactory.getLogger(FailureSimulator::class.java)

@Component
class FailureSimulator(private val failures: Map<String, Failure>) {

    fun run(type: String?, params: Map<String, String> = emptyMap()): Boolean {
        if (!type.isNullOrBlank()) {
            val failure: Failure? = failures[type]
            return if (failure == null) {
                LOG.error("Unknown failure '$type'")
                false
            } else {
                LOG.info("Triggering '$type'")
                failure.fail(params)
                true
            }
        }
        return false
    }
}

@Component("directmemoryleak")
class DirectMemoryLeak : Failure {
    override fun fail(params: Map<String, String>) {
        val allocatedMemory = ArrayList<ByteBuffer>()
        val check =
                params["limitMB"]?.toIntOrNull()?.let { limit ->
                    {
                        limit > allocatedMemory.size
                    }
                } ?: { true }

        while (check.invoke()) {
            allocatedMemory.add(ByteBuffer.allocateDirect(ONE_KILOBYTE * ONE_KILOBYTE))
        }
    }
}
