package dev.ruffrick.dicebot.command

import dev.ruffrick.dicebot.util.DefaultEmbedBuilder
import dev.ruffrick.jda.commands.BaseCommand
import dev.ruffrick.jda.commands.Command
import dev.ruffrick.jda.commands.CommandOption
import dev.ruffrick.jda.commands.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

@Command
class HelpCommand : SlashCommand() {

    @BaseCommand
    suspend fun help(
        event: SlashCommandEvent,
        @CommandOption(name = "command") name: String?
    ) {
        if (name != null) {
            val command = commandRegistry.commandsByName[name.lowercase()] ?: return event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setDescription("\uD83D\uDE15 Unknown command. Use `/help` to view a list of available commands")
                    .build()
            ).setEphemeral(true).queue()
            return event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setTitle("/${command.commandData.name}")
                    .setDescription(command.commandData.description)
                    .addField(
                        "Required arguments",
                        command.commandData.options.filter { it.isRequired }
                            .joinToString("\n") { "`${it.name}: ${it.type.name.lowercase()}` - ${it.description}" }
                            .ifEmpty { "None" },
                        false
                    )
                    .addField(
                        "Optional arguments",
                        command.commandData.options.filter { !it.isRequired }
                            .joinToString("\n") { "`${it.name}: ${it.type.name.lowercase()}` - ${it.description}" }
                            .ifEmpty { "None" },
                        false
                    )
                    .build()
            ).setEphemeral(true).queue()
        }

        event.replyEmbeds(
            DefaultEmbedBuilder()
                .setTitle("\uD83D\uDCA1 Available commands:")
                .setDescription(commandRegistry.commandsByName.values.joinToString("\n") { command ->
                    "`/${command.commandData.name}${
                        command.commandData.options.joinToString("") {
                            if (it.isRequired) {
                                " [${it.name}: ${it.type.name.lowercase()}]"
                            } else {
                                " <${it.name}: ${it.type.name.lowercase()}>"
                            }
                        }
                    }`"
                })
                .setFooter("[] required | <> optional")
                .build()
        ).setEphemeral(true).queue()
    }

}
