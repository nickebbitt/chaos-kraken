package uk.co.autotrader.application

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import uk.co.autotrader.application.simulations.DiskBomb
import uk.co.autotrader.application.simulations.FileCreator
import uk.co.autotrader.application.simulations.FileHandleBomb
import uk.co.autotrader.application.simulations.Kill
import uk.co.autotrader.application.simulations.MemoryLeak
import uk.co.autotrader.application.simulations.MemoryLeakOom
import uk.co.autotrader.application.simulations.SelfConnectionsBomb
import uk.co.autotrader.application.simulations.StandardOutBomb
import uk.co.autotrader.application.simulations.StandardOutBombOptions
import uk.co.autotrader.application.simulations.ThreadBomb
import uk.co.autotrader.application.simulations.ToggleHealth
import uk.co.autotrader.application.simulations.WasteCpu
import javax.annotation.PostConstruct

@SpringBootApplication
class Application
@Autowired
@SuppressWarnings("LongParameterList")
constructor(
    val wasteCpu: WasteCpu,
    val kill: Kill,
    val toggleHealth: ToggleHealth,
    val memoryLeak: MemoryLeak,
    val memoryLeakOom: MemoryLeakOom,
    val threadBomb: ThreadBomb,
    val diskBomb: DiskBomb,
    val fileCreator: FileCreator,
    val stdOutBomb: StandardOutBomb,
    val fileHandleBomb: FileHandleBomb,
    val selfConnectionsBomb: SelfConnectionsBomb
) {
    @PostConstruct
    fun failOnStart() {
        GlobalScope.launch {
            when (System.getenv("FAIL_ON_START")) {
                "wastecpu" -> wasteCpu.run()
                "killapp" -> kill.run()
                "toggle-health" -> toggleHealth.run()
                "memoryleak" -> memoryLeak.run()
                "memoryleak-oom" -> memoryLeakOom.run()
                "threadbomb" -> threadBomb.run()
                "diskbomb" -> diskBomb.run()
                "filecreator" -> fileCreator.run()
                "stdoutbomb" -> stdOutBomb.run(StandardOutBombOptions(1L))
                "filehandlebomb" -> fileHandleBomb.run()
                "selfconnectionsbomb" -> selfConnectionsBomb.run()
            }
        }
    }
}

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
