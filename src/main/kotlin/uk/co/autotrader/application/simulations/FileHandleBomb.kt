package uk.co.autotrader.application.simulations

import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@Component
class FileHandleBomb : Simulation {
    override suspend fun run(options: SimulationOptions?) {
        val readers = ArrayList<FileReader>()

        while (true) {
            try {
                val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".handle.run")
                val fileReader = FileReader(tempFile)
                readers.add(fileReader)
            } catch (ignored: IOException) {
            }
        }
    }
}
