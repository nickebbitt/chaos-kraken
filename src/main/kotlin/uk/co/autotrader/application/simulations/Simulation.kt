package uk.co.autotrader.application.simulations

interface Simulation {
    suspend fun run(params: Map<String, String> = emptyMap())
}
