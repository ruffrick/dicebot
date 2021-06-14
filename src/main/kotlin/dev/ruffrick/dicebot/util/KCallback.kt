package dev.ruffrick.dicebot.util

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class KCallback(private val block: (Response?) -> Unit) : Callback {

    override fun onFailure(call: Call, e: IOException) = block.invoke(null)

    override fun onResponse(call: Call, response: Response) = block.invoke(response)

}
