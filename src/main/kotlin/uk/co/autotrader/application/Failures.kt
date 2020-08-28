package uk.co.autotrader.application

import org.apache.commons.lang3.RandomUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.co.autotrader.application.FileWriter.writeRandomBytes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

interface Failure {
    fun fail(params: Map<String, String> = emptyMap())
}

private val LOG = LoggerFactory.getLogger(FailureSimulator::class.java)

private const val ONE_KB = 1_024

@Component
class FailureSimulator(private val failures: Map<String, Failure>) {

    fun run(type: String?, params: Map<String, String> = emptyMap()): Boolean {
        if (!type.isNullOrBlank()) {
            val failure: Failure? = failures[type]
            return if (failure == null) {
                LOG.error("Unknown failure '${type}'")
                false
            } else {
                LOG.info("Triggering '${type}'")
                failure.fail(params)
                true
            }
        }
        return false
    }
}

@Component("threadbomb")
class ThreadBomb : Failure {

    override fun fail(params: Map<String, String>) {
        while (true) {
            val thread = Thread { while (true); }
            thread.start()
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

private const val ONE_GB = 1024 * 1024 * 1024

@Component("diskbomb")
class DiskBomb : Failure {

    override fun fail(params: Map<String, String>) {
        val directoryPaths = listAllDirectories()

        while (true) {
            try {
                val randomFile = findRandomElement(directoryPaths)
                        .resolve(UUID.randomUUID().toString() + ".disk-bomb.run")
                        .toFile()
                writeRandomBytes(randomFile, ONE_GB)
            } catch (ignored: IOException) {
            }
        }
    }

    private fun <R> findRandomElement(list: List<R>): R {
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

@Component("stdoutbomb")
class StandardOutBomb : Failure {
    override fun fail(params: Map<String, String>) {
        timer(
                period = params["periodMillis"]?.toLongOrNull() ?: 1L,
                action = {
                    println("Standard Out Bomb: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                }
        )
    }
}

@Component("filehandlebomb")
class FileHandleBomb : Failure {
    override fun fail(params: Map<String, String>) {
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

@Component("filecreator")
class FileCreator : Failure {

    override fun fail(params: Map<String, String>) {
        while (true) {
            try {
                val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".file-creator.run")
                writeRandomBytes(tempFile, ONE_KB)
            } catch (ignored: IOException) {
            }
        }
    }
}

private const val CONNECTIONS = 5000

@Component("selfconnectionsbomb")
class SelfConnectionsBomb : Failure {

    override fun fail(params: Map<String, String>) {
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

@Component("directmemoryleak")
class DirectMemoryLeak : Failure {
    override fun fail(params: Map<String, String>) {
        val allocatedMemory = ArrayList<ByteBuffer>()
        val check =
                params["limitMB"]?.toIntOrNull()?.let { limit ->
                    {
                        limit > allocatedMemory.size
                    }
                } ?: { true }

        while (check.invoke()) {
            allocatedMemory.add(ByteBuffer.allocateDirect(ONE_KB * ONE_KB))
        }
    }
}
