package dev.ruffrick.dicebot.event.button

import dev.ruffrick.dicebot.command.CommandRegistry
import dev.ruffrick.dicebot.event.AbstractListener
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import org.springframework.stereotype.Component

@Component
class ButtonClickListener(
    private val commandRegistry: CommandRegistry
) : AbstractListener() {

    private val start = System.currentTimeMillis()

    override suspend fun onEvent(event: GenericEvent) {
        if (event !is ButtonClickEvent || (event.messageIdLong shr 22) + 1420070400000 < start) return

        val args = event.componentId.split('-')
        if (args.size != 3) throw IllegalStateException("Invalid button: id='${event.componentId}'")

        val command = commandRegistry.byName[args[0]]
            ?: throw IllegalStateException("Invalid button: id='${event.componentId}'")

        command.handle(event, args[1], args[2])
    }

}
