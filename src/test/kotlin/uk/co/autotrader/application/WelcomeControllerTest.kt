package uk.co.autotrader.application

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.Charset

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("WelcomeController should")
class WelcomeControllerTest(private val context: ApplicationContext) {

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.webTestClient = webTestClient(context, restDocumentation)
    }

    @Test
    fun `return a welcome message`() {
        webTestClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith { exchangeResult ->
                    val body = exchangeResult.responseBody!!.toString(Charset.forName("UTF-8"))
                    Assertions.assertThat(body).contains("This kraken is running and ready to cause some chaos.")
                    Assertions.assertThat(body).contains("Read the <a href=\"docs/index.html\">docs</a>.")
                }
                .consumeWith(WebTestClientRestDocumentation.document("welcome"))

    }
}
