package dev.ruffrick.dicebot.rest

import dev.ruffrick.dicebot.DiceBot
import dev.ruffrick.dicebot.command.CommandRegistry
import dev.ruffrick.dicebot.util.task.TaskManager
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import kotlin.system.exitProcess

@RestController
class DebugController(
    private val diceBot: DiceBot,
    private val taskManager: TaskManager,
    private val commandRegistry: CommandRegistry
) {

    private val debugStates = mutableMapOf<Long, Boolean>()

    @PostMapping("/shutdown", "/shutdown/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun shutdown(@PathVariable(required = false) id: Int?) {
        if (id != null) {
            diceBot.shardManager.shutdown(id)
        } else {
            diceBot.shardManager.setStatus(OnlineStatus.DO_NOT_DISTURB)
            diceBot.shardManager.setActivity(Activity.playing("Shutting down"))
            diceBot.shardManager.shutdown()
            taskManager.async {
                delay(5000)
                exitProcess(0)
            }
        }
    }

    @PostMapping("/restart", "/restart/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun restart(@PathVariable(required = false) id: Int?) {
        if (id != null) {
            diceBot.shardManager.restart(id)
        } else {
            diceBot.shardManager.restart()
        }
    }

    @PostMapping("/debug/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun debug(@PathVariable id: Long) {
        diceBot.shardManager.getGuildById(id)?.let {
            if (debugStates[it.idLong] == true) {
                it.updateCommands().queue()
                debugStates[it.idLong] = false
            } else {
                commandRegistry.updateCommands(it)
                debugStates[it.idLong] = true
            }
        }
    }

}
