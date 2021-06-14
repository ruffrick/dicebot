package dev.ruffrick.dicebot

import dev.ruffrick.dicebot.command.CommandRegistry
import dev.ruffrick.dicebot.config.Config
import dev.ruffrick.dicebot.event.EventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.cache.CacheFlag
import okhttp3.OkHttpClient
import org.springframework.stereotype.Service

@Service
class DiceBot(
    config: Config,
    eventManager: EventManager,
    httpClient: OkHttpClient,
    commandRegistry: CommandRegistry
) {

    final val shardManager = DefaultShardManagerBuilder
        .create(
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS
        )
        .setShardsTotal(config.shardsTotal)
        .setToken(config.token.discord)
        .setHttpClient(httpClient)
        .setAutoReconnect(true)
        .setChunkingFilter(ChunkingFilter.NONE)
        .setEventManagerProvider { eventManager }
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.VOICE_STATE,
            CacheFlag.EMOTE,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.ROLE_TAGS,
            CacheFlag.ONLINE_STATUS
        )
        .build()

    init {
        commandRegistry.updateCommands(shardManager)
    }

}