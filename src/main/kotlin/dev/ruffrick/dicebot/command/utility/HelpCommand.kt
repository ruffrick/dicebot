package dev.ruffrick.dicebot.command.utility

import dev.ruffrick.dicebot.command.*
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

@Command(category = CommandCategory.UTILITY)
class HelpCommand : SlashCommand() {

    @BaseCommand
    suspend fun help(
        event: SlashCommandEvent,
        @CommandOption(name = "command") name: String?
    ) {
        if (name != null) {
            val command = commandRegistry.byName[name.lowercase()] ?: return event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setDescription("\uD83D\uDE15 Unknown command. Use `/help` to view a list of available commands")
                    .build()
            ).setEphemeral(true).queue()
            return event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setTitle("${command.category.emoji} /${command.commandData.name}")
                    .setDescription("")
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
        val embedBuilder = DefaultEmbedBuilder()
            .setTitle("\uD83D\uDCA1 Available commands:")
            .setFooter("[] required | <> optional")
        commandRegistry.byCategory.forEach { entry ->
            embedBuilder.addField(
                "${entry.key.emoji} **${entry.key.effectiveName}**",
                entry.value.joinToString("\n") { command ->
                    "`/${command.commandData.name}${
                        command.commandData.options.joinToString("") {
                            if (it.isRequired) {
                                " [${it.name}: ${it.type.name.lowercase()}]"
                            } else {
                                " <${it.name}: ${it.type.name.lowercase()}>"
                            }
                        }
                    }`"
                },
                false
            )
        }
        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue()
    }

}
