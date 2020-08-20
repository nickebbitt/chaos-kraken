package uk.co.autotrader.application

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
import uk.co.autotrader.application.simulations.SystemExit
import java.net.URI

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("SimulationController should")
class SimulationControllerShould(private val context: ApplicationContext) {

    private lateinit var webTestClient: WebTestClient
    @MockBean
    private lateinit var systemExit: SystemExit


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
        webTestClient.post()
                .uri("/simulate/v2/memoryleak")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith { exchangeResult ->
                    assertThat(exchangeResult.responseBody).isEqualTo("memoryleak simulation started".toByteArray())
                }
                .consumeWith(WebTestClientRestDocumentation.document("memoryleak"))
    }
}
