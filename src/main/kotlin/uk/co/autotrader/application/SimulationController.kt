package uk.co.autotrader.application

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("simulate/v2")
class SimulationController(val cpu: Cpu) {

    @PostMapping("/cpu")
    fun cpu(): ResponseEntity<String> {
        GlobalScope.launch {
            cpu.run()
        }
        return ResponseEntity.ok().body("done")
    }

}
