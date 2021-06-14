package dev.ruffrick.dicebot.util.embed

import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class DefaultEmbedBuilder : EmbedBuilder() {

    companion object {
        private val color = Color.decode("#dd2e44")
    }

    init {
        setColor(color)
    }

}
