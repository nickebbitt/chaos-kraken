import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.slf4j.LoggerFactory
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.lang.Exception
import java.time.Duration
import java.time.temporal.TemporalUnit


@Tag("integration")
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class IntegrationTests {
    private var log = LoggerFactory.getLogger(IntegrationTests::class.java)

    @Container
    private val container: GenericContainer<Nothing> = GenericContainer<Nothing>(
            ImageFromDockerfile()
                    .withFileFromFile("app.jar", File("./build/libs/chaos-kraken-0.0.1-SNAPSHOT.jar"))
                    .withDockerfileFromBuilder { builder ->
                        builder.from("adoptopenjdk/openjdk11")
                                .run("mkdir /opt/app")
                                .copy("app.jar", "/opt/app/app.jar")
                                .cmd("java", "-jar", "/opt/app/app.jar")
                                .env("JAVA_TOOL_OPTIONS", "-XX:+ExitOnOutOfMemoryError -Xmx200m")
                                .expose(8080)
                                .build()
                    }
    ).apply {
        addExposedPort(8080)
        waitingFor(Wait.forHttp("/"))
        start()
        followOutput(Slf4jLogConsumer(log))
    }


    @Test
    fun `should kill app`() {

        val webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://${container.containerIpAddress}:${container.getMappedPort(8080)}")
                .responseTimeout(Duration.ofSeconds(1))
                .build()

        try {
            webTestClient.post().uri("/simulate/killapp")
                    .exchange()
        } catch (e: Exception) {
            println("failed: ${e.message}")
        } finally {
            assertThat(container.containerInfo.state.exitCode, equalTo(0))
        }

    }
}