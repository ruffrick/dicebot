package dev.ruffrick.dicebot.event

import net.dv8tion.jda.api.events.GenericEvent

abstract class AbstractListener {

    abstract suspend fun onEvent(event: GenericEvent)

}
