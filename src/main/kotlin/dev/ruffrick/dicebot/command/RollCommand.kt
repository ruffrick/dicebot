package dev.ruffrick.dicebot.command

import dev.ruffrick.dicebot.mapping.Dice
import dev.ruffrick.dicebot.util.DefaultEmbedBuilder
import dev.ruffrick.jda.commands.*
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.components.Button
import kotlin.random.Random

@Command
class RollCommand : SlashCommand() {

    private val rolls = mutableMapOf<Long, Roll>()

    @BaseCommand
    suspend fun roll(
        event: SlashCommandEvent,
        @CommandOption dice: Dice,
        @CommandOption modifier: Long?,
        @CommandOption gm: Boolean?
    ) {
        if (modifier != null && modifier <= 0) return event.replyEmbeds(
            DefaultEmbedBuilder().setDescription("\uD83D\uDEAB Your modifier can't be less than one!").build()
        ).setEphemeral(true).queue()
        val roll = Roll(dice.die, dice.count, modifier, gm ?: false)
        rolls[event.user.idLong] = roll
        event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83C\uDFB2 $roll").build())
            .addActionRow(
                Button.secondary("${commandData.name}.reroll.${event.user.idLong}", "Reroll")
                    .withEmoji(Emoji.fromUnicode("\uD83C\uDFB2"))
            )
            .setEphemeral(roll.gm)
            .queue()
    }

    @CommandButton(private = true)
    suspend fun reroll(event: ButtonClickEvent) {
        val roll = rolls[event.user.idLong]
            ?: throw IllegalStateException("No previous roll for user: userId='${event.user.idLong}'")
        event.replyEmbeds(DefaultEmbedBuilder().setDescription("\uD83C\uDFB2 $roll").build())
            .addActionRow(
                Button.secondary("${commandData.name}.reroll.${event.user.idLong}", "Reroll")
                    .withEmoji(Emoji.fromUnicode("\uD83C\uDFB2"))
            )
            .setEphemeral(roll.gm)
            .queue()
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
