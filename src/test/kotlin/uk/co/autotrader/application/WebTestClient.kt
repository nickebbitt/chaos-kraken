package uk.co.autotrader.application

import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

fun webTestClient(context: ApplicationContext, restDocumentation: RestDocumentationContextProvider): WebTestClient {
    return WebTestClient.bindToApplicationContext(context)
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(Preprocessors.prettyPrint())
                    .withResponseDefaults(Preprocessors.prettyPrint())
            )
            .build()
}
