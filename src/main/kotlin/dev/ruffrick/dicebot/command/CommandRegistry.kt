package dev.ruffrick.dicebot.command

import dev.ruffrick.dicebot.util.logging.logger
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Component
import kotlin.reflect.full.findAnnotation

@Component
class CommandRegistry(
    private val commands: List<SlashCommand>
) {

    private val log = logger<CommandRegistry>()

    final val byName = mutableMapOf<String, SlashCommand>()
    final val byCategory: Map<CommandCategory, List<SlashCommand>>

    init {
        commands.forEach {
            val annotation = it::class.findAnnotation<Command>()
                ?: throw IllegalStateException("${it::class.simpleName}: @Command annotation missing!")

            it.category = annotation.category
            it.scope = annotation.scope
            it.requiredPermissions = annotation.requiredPermissions
            it.commandRegistry = this

            byName[it.name] = it

        }
        byCategory = commands.groupBy { it.category }
        log.info("Registered ${commands.size} commands")
    }

    fun updateCommands(shardManager: ShardManager) {
        shardManager.shards.forEach { it.updateCommands().addCommands(commands).queue() }
    }

    fun updateCommands(guild: Guild) {
        guild.updateCommands().addCommands(commands).queue()
    }

}
