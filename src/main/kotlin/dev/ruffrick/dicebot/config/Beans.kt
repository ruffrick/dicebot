package dev.ruffrick.dicebot.config

import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Beans {

    @Bean
    fun httpClient() = OkHttpClient()

}
