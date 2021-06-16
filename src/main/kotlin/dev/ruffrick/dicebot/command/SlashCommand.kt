package dev.ruffrick.dicebot.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class SlashCommand {

    lateinit var category: CommandCategory
    lateinit var scope: CommandScope
    lateinit var requiredPermissions: Array<Permission>
    lateinit var commandRegistry: CommandRegistry
    lateinit var commandData: CommandData

    protected val log: Logger = LogManager.getLogger(this::class.java)

}
