package dev.ruffrick.dicebot.util.extension

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

fun SlashCommandEvent.getString(name: String) = getOption(name)!!.asString

fun SlashCommandEvent.getStringOrNull(name: String) = getOption(name)?.asString

fun SlashCommandEvent.getLong(name: String) = getOption(name)!!.asLong

fun SlashCommandEvent.getLongOrNull(name: String) = getOption(name)?.asLong

fun SlashCommandEvent.getBoolean(name: String) = getOption(name)!!.asBoolean

fun SlashCommandEvent.getBooleanOrNull(name: String) = getOption(name)?.asBoolean

fun SlashCommandEvent.getUser(name: String) = getOption(name)!!.asUser

fun SlashCommandEvent.getUserOrNull(name: String) = getOption(name)?.asUser

fun SlashCommandEvent.getChannel(name: String) = getOption(name)!!.asGuildChannel

fun SlashCommandEvent.getChannelOrNull(name: String) = getOption(name)?.asGuildChannel

fun SlashCommandEvent.getRole(name: String) = getOption(name)!!.asRole

fun SlashCommandEvent.getRoleOrNull(name: String) = getOption(name)?.asRole
