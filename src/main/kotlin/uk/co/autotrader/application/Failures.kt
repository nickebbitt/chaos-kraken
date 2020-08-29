package uk.co.autotrader.application

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

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
