package dev.ruffrick.dicebot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "dice")
data class Config(
    val shardsTotal: Int = 1,
    val token: Token
) {

    data class Token(
        val discord: String
    )

}
