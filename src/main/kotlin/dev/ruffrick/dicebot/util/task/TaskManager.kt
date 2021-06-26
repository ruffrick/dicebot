package dev.ruffrick.dicebot.util.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

object TaskManager {

    private val coroutineDispatcher = Executors.newWorkStealingPool().asCoroutineDispatcher()
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    fun launch(block: suspend CoroutineScope.() -> Unit) = coroutineScope.launch {
        block()
    }

}
