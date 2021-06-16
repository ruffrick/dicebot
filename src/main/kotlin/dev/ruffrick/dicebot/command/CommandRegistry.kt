package dev.ruffrick.dicebot.command

import dev.ruffrick.dicebot.util.logging.logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Service
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

@Service
class CommandRegistry(private val commands: List<SlashCommand>) {

    private val log = logger<CommandRegistry>()
    private val descriptions = Json.decodeFromString<Map<String, String>>(
        this::class.java.getResourceAsStream("/lang/descriptions.json")?.bufferedReader()?.readText()
            ?: throw IllegalArgumentException("Missing resource: path='/lang/descriptions.json'")
    )
    private val optionTypes = mapOf(
        String::class to OptionType.STRING,
        Long::class to OptionType.INTEGER,
        Boolean::class to OptionType.BOOLEAN,
        User::class to OptionType.USER,
        GuildChannel::class to OptionType.CHANNEL,
        Role::class to OptionType.ROLE
    )

    final val byName = mutableMapOf<String, SlashCommand>()
    final val byCategory = mutableMapOf<CommandCategory, MutableList<SlashCommand>>()
    final val byKey = mutableMapOf<String, Pair<KFunction<*>, List<Pair<String, OptionType>>>>()
    final val buttons = mutableMapOf<String, Pair<KFunction<*>, Boolean>>()

    init {
        commands.forEach {
            val command = it::class.findAnnotation<Command>()
                ?: throw IllegalArgumentException("${it::class.simpleName}: @Command annotation missing!")

            it.category = command.category
            it.scope = command.scope
            it.requiredPermissions = command.requiredPermissions
            it.commandRegistry = this

            val commandName = command.name.ifEmpty { it::class.simpleName!!.removeSuffix("Command").lowercase() }
            val commandData = CommandData(commandName, getDescription(commandName))
            val subcommandGroups = mutableListOf<SubcommandGroupData>()

            it::class.memberFunctions.forEach { function ->
                when {
                    function.hasAnnotation<BaseCommand>() -> {
                        val options = parseOptions(function, it, commandData.name)
                        commandData.addOptions(options)
                        byKey[commandName] = function to options.map { optionData ->
                            optionData.name to optionData.type
                        }
                    }
                    function.hasAnnotation<Subcommand>() -> {
                        val subcommand = function.findAnnotation<Subcommand>()!!
                        val name = subcommand.name.ifEmpty { function.name.lowercase() }
                        if (subcommand.group.isNotEmpty()) {
                            val group = subcommand.group
                            val root = "$commandName.$group.$name"
                            val options = parseOptions(function, it, root)
                            val subcommandData = SubcommandData(name, getDescription(root))
                                .addOptions(options)
                            subcommandGroups.firstOrNull { subcommandGroupData ->
                                subcommandGroupData.name == subcommand.group
                            }?.addSubcommands(subcommandData) ?: subcommandGroups.add(
                                SubcommandGroupData(subcommand.group, getDescription("$commandName.$group"))
                                    .addSubcommands(subcommandData)
                            )
                            byKey[root] = function to options.map { optionData ->
                                optionData.name to optionData.type
                            }
                        } else {
                            val root = "$commandName.$name"
                            val options = parseOptions(function, it, root)
                            val subcommandData = SubcommandData(name, getDescription(root))
                                .addOptions(options)
                            commandData.addSubcommands(subcommandData)
                            byKey[root] = function to options.map { optionData ->
                                optionData.name to optionData.type
                            }
                        }
                    }
                    function.hasAnnotation<CommandButton>() -> {
                        require(function.parameters.size == 3) {
                            "Function ${function.name} in class ${it::class.simpleName} must have 2 arguments of " +
                                    "type (ButtonClickEvent, Long)!"
                        }
                        val classifier1 = function.parameters[1].type.classifier
                        require(classifier1 == ButtonClickEvent::class) {
                            "Parameter ${function.parameters[1].name} in function ${function.name} in class " +
                                    "${it::class.simpleName} must be of type ButtonClickEvent but is of type " +
                                    "${(classifier1 as KClass<*>).simpleName}!"
                        }
                        val classifier2 = function.parameters[2].type.classifier
                        require(classifier2 == Long::class) {
                            "Parameter ${function.parameters[2].name} in function ${function.name} in class " +
                                    "${it::class.simpleName} must be of type Long but is of type " +
                                    "${(classifier2 as KClass<*>).simpleName}!"
                        }
                        val commandButton = function.findAnnotation<CommandButton>()!!
                        val id = commandButton.id.ifEmpty { function.name.lowercase() }
                        buttons["$commandName.$id"] = function to commandButton.private
                    }
                }
            }
            if (subcommandGroups.isNotEmpty()) commandData.addSubcommandGroups(subcommandGroups)

            it.commandData = commandData

            byName[commandName] = it
            byCategory.getOrPut(it.category) { mutableListOf() }.add(it)
        }

        log.info("Registered ${commands.size} commands and ${buttons.size} buttons")
    }

    private fun getDescription(key: String): String {
        return descriptions[key] ?: throw IllegalArgumentException("Missing description: key='$key'")
    }

    private fun parseOptions(function: KFunction<*>, command: SlashCommand, root: String): List<OptionData> {
        val options = mutableListOf<OptionData>()
        function.parameters.forEach { parameter ->
            val classifier = parameter.type.classifier
            when {
                parameter.index == 0 -> require(classifier == command::class) { "How did we get here?" }
                parameter.index == 1 -> require(classifier == SlashCommandEvent::class) {
                    "Parameter ${parameter.name} in function ${function.name} in class ${command::class.simpleName} " +
                            "must be of type SlashCommandEvent but is of type ${(classifier as KClass<*>).simpleName}!"
                }
                parameter.index > 1 -> {
                    val commandOption = requireNotNull(parameter.findAnnotation<CommandOption>()) {
                        "Parameter ${parameter.name} in function ${function.name} in class " +
                                "${command::class.simpleName} must be annotated with @CommandOption!"
                    }
                    val type = requireNotNull(optionTypes[classifier]) {
                        "Parameter ${parameter.name} in function ${function.name} in class " +
                                "${command::class.simpleName} must not be of type " +
                                "${(classifier as KClass<*>).simpleName}!"
                    }
                    val name = commandOption.name.ifEmpty { parameter.name!!.lowercase() }
                    options.add(
                        OptionData(type, name, getDescription("$root.$name"), !parameter.type.isMarkedNullable)
                    )
                }
            }
        }
        return options
    }

    fun updateCommands(shardManager: ShardManager) {
        shardManager.shards.forEach { jda ->
            jda.updateCommands()
                .addCommands(commands.map { it.commandData })
                .queue()
        }
    }

    fun updateCommands(guild: Guild) {
        guild.updateCommands()
            .addCommands(commands.map { it.commandData })
            .queue()
    }

}