package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.nio.ByteBuffer

@Component
class DirectMemoryLeak : Simulation {
    override suspend fun run(options: SimulationOptions?) {
        if (options is DirectMemoryLeakOptions) {
            val allocatedMemory = ArrayList<ByteBuffer>()
            val check =
                    options.limitMB.let { limit ->
                        {
                            limit > allocatedMemory.size
                        }
                    }

            while (check.invoke()) {
                allocatedMemory.add(ByteBuffer.allocateDirect(ONE_KILOBYTE * ONE_KILOBYTE))
            }
        }
    }
}

data class DirectMemoryLeakOptions(val limitMB: Int) : SimulationOptions
