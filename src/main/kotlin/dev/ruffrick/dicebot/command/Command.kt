package dev.ruffrick.dicebot.command

import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Command(
    val category: CommandCategory,
    val scope: CommandScope = CommandScope.BOTH,
    val requiredPermissions: Array<Permission> = []
)
