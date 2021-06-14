package dev.ruffrick.dicebot.util.extension

import dev.ruffrick.dicebot.util.KCallback
import kotlinx.coroutines.CompletableDeferred
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.concurrent.Task
import okhttp3.Call
import okhttp3.Response
import okhttp3.ResponseBody
import java.util.concurrent.CompletionStage

suspend inline fun <T> RestAction<T>.await() = completeAsync().await()

fun <T> RestAction<T>.completeAsync() = CompletableDeferred<T?>().apply {
    queue(
        { complete(it) },
        { complete(null) }
    )
}

suspend inline fun <T> Task<T>.await() = completeAsync().await()

fun <T> Task<T>.completeAsync() = CompletableDeferred<T?>().apply {
    onSuccess { complete(it) }
    onError { complete(null) }
}

suspend inline fun <T> CompletionStage<T>.await() = completeAsync().await()

fun <T> CompletionStage<T>.completeAsync() = CompletableDeferred<T?>().apply {
    whenComplete { value, throwable ->
        if (throwable != null) {
            complete(null)
        } else {
            complete(value)
        }
    }
}

suspend inline fun Call.await() = completeAsync().await()

fun Call.completeAsync() = CompletableDeferred<Response?>().apply {
    enqueue(
        KCallback {
            complete(it)
        }
    )
}

suspend inline fun ResponseBody.asString() = asStringAsync().await()

fun ResponseBody.asStringAsync() = CompletableDeferred<String?>().apply {
    complete(string())
}
