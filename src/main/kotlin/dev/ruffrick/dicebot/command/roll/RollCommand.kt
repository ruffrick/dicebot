package dev.ruffrick.dicebot.command.roll

import dev.ruffrick.dicebot.command.Command
import dev.ruffrick.dicebot.command.CommandCategory
import dev.ruffrick.dicebot.command.SlashCommand
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import dev.ruffrick.dicebot.util.extension.getBooleanOrNull
import dev.ruffrick.dicebot.util.extension.getLongOrNull
import dev.ruffrick.dicebot.util.extension.getString
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.components.Button
import kotlin.random.Random

@Command(category = CommandCategory.ROLL)
class RollCommand : SlashCommand("roll", "Roll some dice") {

    private val pattern = Regex("^(\\d*)[dD](\\d+)\$").toPattern()
    private val rolls = mutableMapOf<String, Roll>()

    init {
        this.addOption(OptionType.STRING, "dice", "The amount and type of dice to roll", true)
            .addOption(OptionType.INTEGER, "modifier", "The modifier to add to your result", false)
            .addOption(OptionType.BOOLEAN, "gm", "Whether to roll as the game master or not", false)
    }

    override suspend fun execute(event: SlashCommandEvent) {
        val roll = parse(event) ?: return
        val user = event.user.idLong.toString(36)
        rolls[user] = roll
        event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83C\uDFB2 $roll").build())
            .addActionRow(Button.secondary("$name-roll-$user", "Reroll").withEmoji(Emoji.fromUnicode("\uD83C\uDFB2")))
            .setEphemeral(roll.gm)
            .queue()
    }

    override suspend fun handle(event: ButtonClickEvent, id: String, user: String) {
        if (event.user.idLong.toString(36) != user) return event.replyEmbeds(
            DefaultEmbedBuilder().setDescription("\uD83D\uDEAB This can only be used by <@${user.toLong(36)}>!").build()
        ).setEphemeral(true).queue()
        val roll = rolls[user] ?: throw IllegalStateException("No previous roll for user: userId='${event.user.id}'")
        event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83C\uDFB2 $roll").build())
            .addActionRow(Button.secondary("$name-roll-$user", "Reroll").withEmoji(Emoji.fromUnicode("\uD83C\uDFB2")))
            .setEphemeral(roll.gm)
            .queue()
    }

    private fun parse(event: SlashCommandEvent): Roll? {
        return try {
            val matcher = pattern.matcher(event.getString("dice"))
            require(matcher.matches()) { "Please enter the dice you want to roll, e. g. `1d20` or `4d8`!" }
            val count = matcher.group(1).toIntOrNull() ?: 1
            require(count >= 1) { "You can't roll less than one die!" }
            require(count <= 8) { "You can't roll more than eight dice!" }
            val die = matcher.group(2).toInt()
            require(die >= 4) { "Your dice can't have less than four faces!" }
            require(die <= 120) { "Your dice can't have more than 120 faces!" }
            val modifier = event.getLongOrNull("modifier")
            if (modifier != null) require(modifier >= 1) { "Your modifier can't be less than one!" }
            val gm = event.getBooleanOrNull("gm") ?: false
            Roll(die, count, modifier, gm)
        } catch (e: IllegalArgumentException) {
            event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83D\uDEAB ${e.message}").build())
                .setEphemeral(true)
                .queue()
            null
        }
    }

    private data class Roll(
        val die: Int,
        val count: Int,
        val modifier: Long?,
        val gm: Boolean
    ) {

        override fun toString(): String {
            return if (count == 1) {
                val result = Random.nextInt(die) + 1
                if (modifier != null) {
                    "Your result is **$result *+ $modifier* = ${result + modifier}**"
                } else {
                    "Your result is **$result**"
                }
            } else {
                val results = mutableListOf<Int>()
                repeat(count) { results.add(Random.nextInt(die) + 1) }
                if (modifier != null) {
                    "Your results are **(${results.joinToString(" + ")}) *+ $modifier* = ${results.sum() + modifier}**"
                } else {
                    "Your results are **(${results.joinToString(" + ")}) = ${results.sum()}**"
                }
            }
        }

    }

}
