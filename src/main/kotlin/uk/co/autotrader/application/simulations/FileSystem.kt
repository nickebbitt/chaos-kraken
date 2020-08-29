package uk.co.autotrader.application.simulations

import org.apache.commons.lang3.RandomUtils
import org.springframework.stereotype.Component
import uk.co.autotrader.application.simulations.FileWriter.writeRandomBytes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


@Component("diskbomb")
class DiskBomb : Simulation {

    override suspend fun run() {
        val directoryPaths = listAllDirectories()

        while (true) {
            try {
                val randomFile = findRandomElement(directoryPaths)
                        .resolve(UUID.randomUUID().toString() + ".disk-bomb.run")
                        .toFile()
                writeRandomBytes(randomFile, ONE_GIGABYTE)
            } catch (ignored: IOException) {
            }
        }
    }

    private fun <T> findRandomElement(list: List<T>): T {
        return list[Random().nextInt(list.size)]
    }

    private fun listAllDirectories(): List<Path> {
        return Arrays.stream(File.listRoots())
                .map { it.toPath() }
                .flatMap { this.listAllSubDirectories(it) }
                .collect(Collectors.toList())
    }

    private fun listAllSubDirectories(root: Path): Stream<Path> {
        return Files.walk(root)
                .filter { path -> path.toFile().isDirectory }
    }
}

@Component("filecreator")
class FileCreator : Simulation {

    override suspend fun run() {
        while (true) {
            try {
                val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".file-creator.run")
                writeRandomBytes(tempFile, ONE_KILOBYTE)
            } catch (ignored: IOException) {
            }
        }
    }
}

object FileWriter {

    @Throws(IOException::class)
    fun writeRandomBytes(file: File, size: Int) {
        BufferedOutputStream(FileOutputStream(file)).use { output ->
            var i = 0
            while (i < size) {
                output.write(RandomUtils.nextBytes(1))
                i += 1
            }
        }
    }
}
