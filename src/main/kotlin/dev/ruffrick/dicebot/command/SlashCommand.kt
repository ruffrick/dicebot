package dev.ruffrick.dicebot.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class SlashCommand(name: String, description: String) : CommandData(name, description) {

    lateinit var category: CommandCategory
    lateinit var scope: CommandScope
    lateinit var requiredPermissions: Array<Permission>
    lateinit var commandRegistry: CommandRegistry

    protected val log: Logger = LogManager.getLogger(this::class.java)

    abstract suspend fun execute(event: SlashCommandEvent)

    open suspend fun handle(event: ButtonClickEvent, id: String, user: String) {
        log.error("Received a ButtonClickEvent but no implementation was found!")
    }

}
