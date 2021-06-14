package dev.ruffrick.dicebot.command

enum class CommandCategory(val effectiveName: String, val emoji: String) {
    ROLL("Roll", "\uD83C\uDFB2"),
    UTILITY("Utility", "\uD83E\uDDED")
}
