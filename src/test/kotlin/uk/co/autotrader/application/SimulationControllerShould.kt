package uk.co.autotrader.application

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import uk.co.autotrader.application.simulations.DirectMemoryLeak
import uk.co.autotrader.application.simulations.DirectMemoryLeakOptions
import uk.co.autotrader.application.simulations.DiskBomb
import uk.co.autotrader.application.simulations.FileCreator
import uk.co.autotrader.application.simulations.FileHandleBomb
import uk.co.autotrader.application.simulations.MemoryLeak
import uk.co.autotrader.application.simulations.MemoryLeakOom
import uk.co.autotrader.application.simulations.SelfConnectionsBomb
import uk.co.autotrader.application.simulations.StandardOutBomb
import uk.co.autotrader.application.simulations.StandardOutBombOptions
import uk.co.autotrader.application.simulations.SystemExit
import uk.co.autotrader.application.simulations.ThreadBomb
import java.net.URI

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("SimulationController should")
class SimulationControllerShould(private val context: ApplicationContext) {

    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var systemExit: SystemExit

    @MockBean
    private lateinit var memoryLeak: MemoryLeak

    @MockBean
    private lateinit var memoryLeakOom: MemoryLeakOom

    @MockBean
    private lateinit var threadBomb: ThreadBomb

    @MockBean
    private lateinit var diskBomb: DiskBomb

    @MockBean
    private lateinit var fileCreator: FileCreator

    @MockBean
    private lateinit var standardOutBomb: StandardOutBomb

    @MockBean
    private lateinit var fileHandleBomb: FileHandleBomb

    @MockBean
    private lateinit var selfConnectionsBomb: SelfConnectionsBomb

    @MockBean
    private lateinit var directMemoryLeak: DirectMemoryLeak

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.webTestClient = webTestClient(context, restDocumentation)
    }

    @Test
    fun `trigger cpu simulation`() {
        webTestClient.post()
                .uri("/simulate/v2/cpu")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith { exchangeResult ->
                    assertThat(exchangeResult.responseBody).isEqualTo("cpu simulation started".toByteArray())
                }
                .consumeWith(WebTestClientRestDocumentation.document("cpu"))
    }

    @Test
    fun `trigger killapp simulation`() {
        webTestClient.post()
                .uri("/simulate/v2/killapp")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith { exchangeResult ->
                    assertThat(exchangeResult.responseBody).isEqualTo("killapp simulation started".toByteArray())
                }
                .consumeWith(WebTestClientRestDocumentation.document("killapp"))

        verify(systemExit, times(1)).exit(1)
    }

    @Test
    fun `toggle health to service unavailable`() {
        webTestClient.get()
                .uri(URI("/actuator/health"))
                .exchange()
                .expectStatus().isOk

        webTestClient.post()
                .uri("/simulate/v2/toggle-health")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith { exchangeResult ->
                    assertThat(exchangeResult.responseBody).isEqualTo("health toggled".toByteArray())
                }
                .consumeWith(WebTestClientRestDocumentation.document("toggle-health"))

        webTestClient.get()
                .uri(URI("/actuator/health"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)

        webTestClient.post()
                .uri("/simulate/v2/toggle-health")
                .exchange()
                .expectStatus().isOk

        webTestClient.get()
                .uri(URI("/actuator/health"))
                .exchange()
                .expectStatus().isOk
    }

    @Test
    fun `trigger memoryleak simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/memoryleak")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody).isEqualTo("memoryleak simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("memoryleak"))

            verify(memoryLeak, times(1)).run()
        }
    }

    @Test
    fun `trigger memoryleak-oom simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/memoryleak-oom")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody)
                                .isEqualTo("memoryleak-oom simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("memoryleak-oom"))

            verify(memoryLeakOom, times(1)).run()
        }
    }

    @Test
    fun `trigger threadbomb simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/threadbomb")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody).isEqualTo("threadbomb simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("threadbomb"))

            verify(threadBomb, times(1)).run()
        }
    }

    @Test
    fun `trigger diskbomb simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/diskbomb")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody).isEqualTo("diskbomb simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("diskbomb"))

            verify(diskBomb, times(1)).run()
        }
    }

    @Test
    fun `trigger filecreator simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/filecreator")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody)
                                .isEqualTo("filecreator simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("filecreator"))

            verify(fileCreator, times(1)).run()
        }
    }

    @Test
    fun `trigger stdoutbomb simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri { uriBuilder ->
                        uriBuilder
                                .path("/simulate/v2/stdoutbomb")
                                .queryParam("periodMillis", "5")
                                .build()
                    }
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody).isEqualTo("stdoutbomb simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("stdoutbomb"))

            verify(standardOutBomb, times(1)).run(StandardOutBombOptions(5))
        }
    }

    @Test
    fun `trigger filehandlebomb simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/filehandlebomb")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody)
                                .isEqualTo("filehandlebomb simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("filehandlebomb"))

            verify(fileHandleBomb, times(1)).run()
        }
    }

    @Test
    fun `trigger selfconnectionsbomb simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri("/simulate/v2/selfconnectionsbomb")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody)
                                .isEqualTo("selfconnectionsbomb simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("selfconnectionsbomb"))

            verify(selfConnectionsBomb, times(1)).run()
        }
    }

    @Test
    fun `trigger directmemoryleak simulation`() {
        runBlocking {
            webTestClient.post()
                    .uri { uriBuilder ->
                        uriBuilder
                                .path("/simulate/v2/directmemoryleak")
                                .queryParam("limitMB", "200")
                                .build()
                    }
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { exchangeResult ->
                        assertThat(exchangeResult.responseBody)
                                .isEqualTo("directmemoryleak simulation started".toByteArray())
                    }
                    .consumeWith(WebTestClientRestDocumentation.document("directmemoryleak"))

            verify(directMemoryLeak, times(1)).run(DirectMemoryLeakOptions(limitMB = 200))
        }
    }
}
