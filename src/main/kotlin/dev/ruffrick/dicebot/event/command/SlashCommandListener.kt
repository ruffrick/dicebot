package dev.ruffrick.dicebot.event.command

import dev.ruffrick.dicebot.command.CommandRegistry
import dev.ruffrick.dicebot.command.CommandScope
import dev.ruffrick.dicebot.command.SlashCommand
import dev.ruffrick.dicebot.event.AbstractListener
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import dev.ruffrick.dicebot.util.logging.logger
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.springframework.stereotype.Component
import kotlin.reflect.full.callSuspend
import kotlin.system.measureTimeMillis

@Component
class SlashCommandListener(
    private val commandRegistry: CommandRegistry
) : AbstractListener() {

    private val log = logger<SlashCommandListener>()

    override suspend fun onEvent(event: GenericEvent) {
        if (event !is SlashCommandEvent) return

        val command = commandRegistry.byName[event.name]
            ?: throw IllegalArgumentException("Invalid command: name='${event.name}'")

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

        val key = when {
            event.subcommandGroup != null -> "${event.name}.${event.subcommandGroup}.${event.subcommandName}"
            event.subcommandName != null -> "${event.name}.${event.subcommandName}"
            else -> event.name
        }

        val (function, options) = commandRegistry.byKey[key]
            ?: throw IllegalArgumentException(
                "No command mapping found: " +
                        "name='${event.name}', " +
                        "subcommandGroup='${event.subcommandGroup}', " +
                        "subcommandName='${event.subcommandName}'"
            )

        val args = Array(options.size) {
            val (name, type) = options[it]
            when (type) {
                OptionType.STRING -> event.getOption(name)?.asString
                OptionType.INTEGER -> event.getOption(name)?.asLong
                OptionType.BOOLEAN -> event.getOption(name)?.asBoolean
                OptionType.USER -> event.getOption(name)?.asUser
                OptionType.CHANNEL -> event.getOption(name)?.asGuildChannel
                OptionType.ROLE -> event.getOption(name)?.asRole
                else -> throw IllegalArgumentException("Invalid option: name='$name', type='$type'")
            }
        }

        val duration = measureTimeMillis {
            function.callSuspend(command, event, *args)
        }
        log.info(
            "Executed command: " +
                    "name='${command.commandData.name}', " +
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
