package uk.co.autotrader.application.simulations

interface SimulationOptions

interface Simulation {
    suspend fun run(options: SimulationOptions? = null)
}
