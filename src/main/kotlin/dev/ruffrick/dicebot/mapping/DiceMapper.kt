package dev.ruffrick.dicebot.mapping

import dev.ruffrick.jda.commands.mapping.StringMapper

class DiceMapper : StringMapper<Dice> {

    private val pattern = Regex("^(\\d*)[dD](\\d+)\$").toPattern()

    override suspend fun transform(value: String): Dice {
        val matcher = pattern.matcher(value)
        require(matcher.matches()) { "Please enter the dice you want to roll, e. g. `1d20` or `4d8`!" }
        val count = matcher.group(1).toIntOrNull() ?: 1
        require(count >= 1) { "You can't roll less than one die!" }
        require(count <= 8) { "You can't roll more than eight dice!" }
        val die = matcher.group(2).toInt()
        require(die >= 4) { "Your dice can't have less than four faces!" }
        require(die <= 120) { "Your dice can't have more than 120 faces!" }

        return Dice(die, count)
    }

}

data class Dice(
    val die: Int,
    val count: Int
)
