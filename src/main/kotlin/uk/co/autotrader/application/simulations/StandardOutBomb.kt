package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.timer

@Component
class StandardOutBomb : Simulation {
    override suspend fun run(options: SimulationOptions?) {
        if (options is StandardOutBombOptions) {
            timer(period = options.periodMillis,
                  action = {
                      println("Standard Out Bomb: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                  }
            )
        }
    }
}

data class StandardOutBombOptions(val periodMillis: Long) : SimulationOptions
