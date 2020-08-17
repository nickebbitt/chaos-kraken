package uk.co.autotrader.application

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimulationControllerShould(private val context: ApplicationContext,
                                 @LocalServerPort val randomServerPort: Int) {

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.webTestClient = webTestClient(context, restDocumentation)
    }

    @Test
    fun `trigger cpu failure`() {
        webTestClient.post()
                .uri("/simulate/v2/cpu")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("cpu"))

    }
}
