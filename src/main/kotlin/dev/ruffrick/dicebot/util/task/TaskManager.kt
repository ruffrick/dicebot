package dev.ruffrick.dicebot.util.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class TaskManager {

    private val coroutineDispatcher = Executors.newWorkStealingPool().asCoroutineDispatcher()
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    fun async(block: suspend CoroutineScope.() -> Unit) = coroutineScope.launch {
        block()
    }

}
