package uk.co.autotrader.application

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.co.autotrader.application.simulations.Cpu
import uk.co.autotrader.application.simulations.DiskBomb
import uk.co.autotrader.application.simulations.FileCreator
import uk.co.autotrader.application.simulations.Kill
import uk.co.autotrader.application.simulations.MemoryLeak
import uk.co.autotrader.application.simulations.MemoryLeakOom
import uk.co.autotrader.application.simulations.StandardOutBomb
import uk.co.autotrader.application.simulations.ThreadBomb
import uk.co.autotrader.application.simulations.ToggleHealth

@RestController
@RequestMapping("simulate/v2")
@SuppressWarnings("LongParameterList")
class SimulationController(
    private val cpu: Cpu,
    private val kill: Kill,
    private val toggleHealth: ToggleHealth,
    private val memoryLeak: MemoryLeak,
    private val memoryLeakOom: MemoryLeakOom,
    private val threadBomb: ThreadBomb,
    private val diskBomb: DiskBomb,
    private val fileCreator: FileCreator,
    private val standardOutBomb: StandardOutBomb
) {

    @PostMapping("/cpu")
    fun cpu(): ResponseEntity<String> {
        GlobalScope.launch {
            cpu.run()
        }
        return ResponseEntity.ok().body("cpu simulation started")
    }

    @PostMapping("/killapp")
    fun killApp(): ResponseEntity<String> {
        GlobalScope.launch {
            kill.run()
        }
        return ResponseEntity.ok().body("killapp simulation started")
    }

    @PostMapping("/toggle-health")
    fun toggleHealth(): ResponseEntity<String> {
        GlobalScope.launch {
            toggleHealth.run()
        }
        return ResponseEntity.ok().body("health toggled")
    }

    @PostMapping("/memoryleak")
    fun memoryLeak(): ResponseEntity<String> {
        GlobalScope.launch {
            memoryLeak.run()
        }
        return ResponseEntity.ok().body("memoryleak simulation started")
    }

    @PostMapping("/memoryleak-oom")
    fun memoryLeakOom(): ResponseEntity<String> {
        GlobalScope.launch {
            memoryLeakOom.run()
        }
        return ResponseEntity.ok().body("memoryleak-oom simulation started")
    }

    @PostMapping("/threadbomb")
    fun threadBomb(): ResponseEntity<String> {
        GlobalScope.launch {
            threadBomb.run()
        }
        return ResponseEntity.ok().body("threadbomb simulation started")
    }

    @PostMapping("/diskbomb")
    fun diskBomb(): ResponseEntity<String> {
        GlobalScope.launch {
            diskBomb.run()
        }
        return ResponseEntity.ok().body("diskbomb simulation started")
    }

    @PostMapping("/filecreator")
    fun fileCreator(): ResponseEntity<String> {
        GlobalScope.launch {
            fileCreator.run()
        }
        return ResponseEntity.ok().body("filecreator simulation started")
    }

    @PostMapping("/stdoutbomb")
    fun standardOutBomb(): ResponseEntity<String> {
        GlobalScope.launch {
            standardOutBomb.run()
        }
        return ResponseEntity.ok().body("stdoutbomb simulation started")
    }
}
