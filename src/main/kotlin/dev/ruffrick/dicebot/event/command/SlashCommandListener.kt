package dev.ruffrick.dicebot.event.command

import dev.ruffrick.dicebot.command.CommandRegistry
import dev.ruffrick.dicebot.command.CommandScope
import dev.ruffrick.dicebot.command.SlashCommand
import dev.ruffrick.dicebot.event.AbstractListener
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import dev.ruffrick.dicebot.util.logging.logger
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class SlashCommandListener(
    private val commandRegistry: CommandRegistry
) : AbstractListener() {

    private val log = logger<SlashCommandListener>()

    override suspend fun onEvent(event: GenericEvent) {
        if (event !is SlashCommandEvent) return

        val command = commandRegistry.byName[event.name]
            ?: throw IllegalStateException("Invalid command: name='${event.name}'")

        when (command.scope) {
            CommandScope.GUILD -> {
                if (!event.isFromGuild) return event.replyEmbeds(
                    DefaultEmbedBuilder().setDescription("This command is restricted to guild channels!").build()
                ).setEphemeral(true).queue()
                if (!hasRequiredPermissions(command, event)) return
            }
            CommandScope.PRIVATE -> if (event.isFromGuild) return event.replyEmbeds(
                DefaultEmbedBuilder().setDescription("This command is restricted to private channels!").build()
            ).setEphemeral(true).queue()
            CommandScope.BOTH -> if (event.isFromGuild && !hasRequiredPermissions(command, event)) return
        }

        val duration = measureTimeMillis {
            command.execute(event)
        }
        log.info(
            "Executed command: " +
                    "name='${command.name}', " +
                    "userId='${event.user.id}', " +
                    "guildId='${event.guild?.id ?: -1}', " +
                    "durationMs='$duration'"
        )
    }

    private fun hasRequiredPermissions(command: SlashCommand, event: SlashCommandEvent): Boolean {
        val missingPermissions = command.requiredPermissions
            .filter { event.member!!.hasPermission(event.textChannel, it) }
            .joinToString { "`${it.getName()}`" }
        if (missingPermissions.isNotEmpty()) {
            event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setDescription(
                        "You don't have the required permissions to do that! " +
                                "You are missing the following permissions: $missingPermissions"
                    )
                    .build()
            ).setEphemeral(true).queue()
            return false
        }
        return true
    }

}
