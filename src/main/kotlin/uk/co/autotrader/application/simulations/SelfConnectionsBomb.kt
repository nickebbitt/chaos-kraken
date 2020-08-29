package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

private const val CONNECTIONS = 5000

@Component
class SelfConnectionsBomb : Simulation {

    override suspend fun run(options: SimulationOptions?) {
        val openConnections = ArrayList<Socket>()

        try {
            ServerSocket(0, CONNECTIONS).use { serverSocket ->
                for (i in 0 until CONNECTIONS) {
                    openConnections.add(createLoopbackSocket(serverSocket.localPort))
                }
            }
        } catch (ignored: IOException) {
        } finally {
            closeConnections(openConnections)
        }
    }

    @Throws(IOException::class)
    private fun createLoopbackSocket(port: Int): Socket {
        return Socket(null as String?, port)
    }

    private fun closeConnections(openConnections: List<Socket>) {
        for (socket in openConnections) {
            try {
                socket.close()
            } catch (ignored: IOException) {
            }
        }
    }
}
