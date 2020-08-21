package uk.co.autotrader.application

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EchoStatusRouteShould(private val context: ApplicationContext) {

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.webTestClient = webTestClient(context, restDocumentation)
    }

    @Test
    fun `respond with provided valid status code`() {
        webTestClient.get().uri("/echostatus/418")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT)
    }

    @Test
    fun `respond with bad request for invalid status code`() {
        webTestClient.get().uri("/echostatus/999")
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun `respond with bad request non-numeric status code`() {
        webTestClient.get().uri("/echostatus/sdfsdf")
                .exchange()
                .expectStatus().isBadRequest
    }
}
