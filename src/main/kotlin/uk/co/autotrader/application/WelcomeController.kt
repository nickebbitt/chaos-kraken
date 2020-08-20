package uk.co.autotrader.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class WelcomeController {
    private val WELCOME_MESSAGE = """
        This kraken is running and ready to cause some chaos.
        <p>
        Read the <a href="docs/index.html">docs</a>.
    """.trimIndent()

    @GetMapping
    fun welcome(): ResponseEntity<String> {
        return ResponseEntity.ok().body(WELCOME_MESSAGE)
    }

}
