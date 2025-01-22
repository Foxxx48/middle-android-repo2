package ru.yandex.praktikumchatapp.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private var currentDelay = INITIAL_DELAY

    fun getReplyMessage() = flow {
            emitAll(api.getReply())
        }.retryWhen { exception, attempt ->
            if (exception is Exception && attempt < MAX_RETRIES) {
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR.toLong()
                true
            } else {
                false
            }
        }.catch {exception ->
        Log.e(TAG, exception.message, exception)
            throw exception
        }

    companion object {
        private const val MAX_RETRIES = 5
        private const val INITIAL_DELAY = 1000L
        private const val DELAY_FACTOR = 2
        private const val TAG = "ChatRepository"

    }
}