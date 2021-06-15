package dev.ruffrick.dicebot.command.roll

import dev.ruffrick.dicebot.command.*
import dev.ruffrick.dicebot.util.embed.DefaultEmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import kotlin.random.Random

@Command(category = CommandCategory.ROLL)
class RollCommand : SlashCommand() {

    private val pattern = Regex("^(\\d*)[dD](\\d+)\$").toPattern()

    @BaseCommand
    suspend fun roll(
        event: SlashCommandEvent,
        @CommandOption dice: String,
        @CommandOption modifier: Long?,
        @CommandOption gm: Boolean?
    ) {
        try {
            val roll = parse(dice, modifier, gm)
            event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83C\uDFB2 $roll").build())
                .setEphemeral(roll.gm)
                .queue()
        } catch (e: IllegalArgumentException) {
            event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83D\uDEAB ${e.message}").build())
                .setEphemeral(true)
                .queue()
        }
    }

    private fun parse(dice: String, modifier: Long?, gm: Boolean?): Roll {
        val matcher = pattern.matcher(dice)
        require(matcher.matches()) { "Please enter the dice you want to roll, e. g. `1d20` or `4d8`!" }
        val count = matcher.group(1).toIntOrNull() ?: 1
        require(count >= 1) { "You can't roll less than one die!" }
        require(count <= 8) { "You can't roll more than eight dice!" }
        val die = matcher.group(2).toInt()
        require(die >= 4) { "Your dice can't have less than four faces!" }
        require(die <= 120) { "Your dice can't have more than 120 faces!" }
        if (modifier != null) require(modifier >= 1) { "Your modifier can't be less than one!" }
        return Roll(die, count, modifier, gm ?: false)
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
