package dev.ruffrick.dicebot.command.utility

import dev.ruffrick.dicebot.command.Command
import dev.ruffrick.dicebot.command.CommandCategory
import dev.ruffrick.dicebot.command.SlashCommand
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import dev.ruffrick.dicebot.util.extension.getStringOrNull
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

@Command(category = CommandCategory.UTILITY)
class HelpCommand : SlashCommand("help", "Display some information on how to use the bot") {

    init {
        this.addOption(OptionType.STRING, "command", "The command to view help for", false)
    }

    override suspend fun execute(event: SlashCommandEvent) {
        event.getStringOrNull("command")?.let { name ->
            val command = commandRegistry.byName[name.lowercase()] ?: return event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setDescription("\uD83D\uDE15 Unknown command. Use `/help` to view a list of available commands")
                    .build()
            ).setEphemeral(true).queue()
            return event.replyEmbeds(
                DefaultEmbedBuilder()
                    .setTitle("${command.category.emoji} /${command.name}")
                    .setDescription(command.description)
                    .addField(
                        "Required arguments",
                        command.options.filter { it.isRequired }
                            .joinToString("\n") { "`${it.name}: ${it.type.name.lowercase()}` - ${it.description}" }
                            .ifEmpty { "None" },
                        false
                    )
                    .addField(
                        "Optional arguments",
                        command.options.filter { !it.isRequired }
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
                    "`/${command.name}${
                        command.options.joinToString("") {
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
