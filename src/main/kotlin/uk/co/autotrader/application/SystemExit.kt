package uk.co.autotrader.application

import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component


interface SystemExit {
    fun exit(status: Int)
}

@Component
class SystemExitImpl(val applicationContext: ApplicationContext) : SystemExit {
    override fun exit(status: Int) {
        SpringApplication.exit(applicationContext, ExitCodeGenerator { status })
    }
}
