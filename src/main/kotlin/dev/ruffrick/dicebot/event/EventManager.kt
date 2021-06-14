package dev.ruffrick.dicebot.event

import dev.ruffrick.dicebot.util.logging.logger
import dev.ruffrick.dicebot.util.task.TaskManager
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.IEventManager
import org.springframework.stereotype.Service

@Service
class EventManager(
    private val taskManager: TaskManager,
    private val eventListeners: List<AbstractListener>
) : IEventManager {

    private val log = logger<EventManager>()

    override fun register(listener: Any) {
        throw UnsupportedOperationException()
    }

    override fun unregister(listener: Any) {
        throw UnsupportedOperationException()
    }

    override fun handle(event: GenericEvent) {
        taskManager.async {
            eventListeners.forEach {
                try {
                    it.onEvent(event)
                } catch (e: Exception) {
                    log.error(e.message)
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getRegisteredListeners(): List<Any> {
        throw UnsupportedOperationException()
    }

}
