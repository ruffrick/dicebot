package dev.ruffrick.dicebot

import dev.ruffrick.dicebot.config.Config
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(Config::class)
class DiceBotApplication

fun main() {
    runApplication<DiceBotApplication>()
}
