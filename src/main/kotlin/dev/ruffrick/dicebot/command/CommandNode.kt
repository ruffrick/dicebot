package dev.ruffrick.dicebot.command

import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KFunction

data class CommandNode(
    val function: KFunction<*>? = null,
    val args: Map<String, OptionType>? = null
) {

    val children = mutableMapOf<String, CommandNode>()

}
