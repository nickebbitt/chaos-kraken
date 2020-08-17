package uk.co.autotrader.application.simulations

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class KillApp(val systemExit: SystemExit) : Simulation {

    override suspend fun run() {
        LOG.error("Application was killed by calling the 'killapp' failure")
        systemExit.exit(1)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KillApp::class.java)
    }
}

interface SystemExit {
    fun exit(status: Int)
}

@Component
class SystemExitImpl : SystemExit {
    override fun exit(status: Int) {
        exitProcess(status)
    }
}
