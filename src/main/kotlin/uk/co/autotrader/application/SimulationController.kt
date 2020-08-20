package uk.co.autotrader.application

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.co.autotrader.application.simulations.Cpu
import uk.co.autotrader.application.simulations.KillApp
import uk.co.autotrader.application.simulations.MemoryLeak
import uk.co.autotrader.application.simulations.ToggleHealth

@RestController
@RequestMapping("simulate/v2")
class SimulationController(
        val cpu: Cpu,
        val memoryLeak: MemoryLeak,
        val killApp: KillApp,
        val toggleHealth: ToggleHealth
) {

    @PostMapping("/cpu")
    fun cpu(): ResponseEntity<String> {
        GlobalScope.launch {
            cpu.run()
        }
        return ResponseEntity.ok().body("cpu simulation started")
    }

    @PostMapping("/memoryleak")
    fun memoryLeak(): ResponseEntity<String> {
        GlobalScope.launch {
            memoryLeak.run()
        }
        return ResponseEntity.ok().body("memoryleak simulation started")
    }

    @PostMapping("/killapp")
    fun killApp(): ResponseEntity<String> {
        GlobalScope.launch {
            killApp.run()
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


}
