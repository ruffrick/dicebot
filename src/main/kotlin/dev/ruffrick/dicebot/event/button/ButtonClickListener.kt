package dev.ruffrick.dicebot.event.button

import dev.ruffrick.dicebot.command.CommandRegistry
import dev.ruffrick.dicebot.event.AbstractListener
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import org.springframework.stereotype.Component
import kotlin.reflect.full.callSuspend

@Component
class ButtonClickListener(
    private val commandRegistry: CommandRegistry
) : AbstractListener() {

    private val start = System.currentTimeMillis()

    override suspend fun onEvent(event: GenericEvent) {
        if (event !is ButtonClickEvent || (event.messageIdLong shr 22) + 1420070400000 < start) return

        val (commandName, buttonId, userId) = event.componentId.split('.').takeIf { it.size == 3 }
            ?: throw IllegalArgumentException("Invalid button: id='${event.componentId}'")

        val command = commandRegistry.byName[commandName]
            ?: throw IllegalArgumentException("Invalid button: id='${event.componentId}'")

        val userIdLong = userId.toLongOrNull()
            ?: throw IllegalArgumentException("Invalid button: id='${event.componentId}'")

        val (function, private) = commandRegistry.buttons["$commandName.$buttonId"]
            ?: throw IllegalArgumentException("No button mapping found: id='${event.componentId}'")

        if (private && event.user.idLong != userIdLong) {
            return event.replyEmbeds(
                DefaultEmbedBuilder().setDescription("\uD83D\uDEAB This can only be used by <@$userId>!").build()
            ).setEphemeral(true).queue()
        }

        function.callSuspend(command, event, userIdLong)
    }

}
