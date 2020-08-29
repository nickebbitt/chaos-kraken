package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.timer

@Component
class StandardOutBomb : Simulation {
    override suspend fun run(params: Map<String, String>) {
        timer(
                period = params["periodMillis"]?.toLongOrNull() ?: 1L,
                action = {
                    println("Standard Out Bomb: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                }
        )
    }
}
