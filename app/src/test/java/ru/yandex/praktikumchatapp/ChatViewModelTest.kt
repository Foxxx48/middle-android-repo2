package ru.yandex.praktikumchatapp

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

/**
 * Тестирует класс [ChatViewModel]
 */
@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {

        val message = Message.MyMessage("TestMessage")

        viewModel.sendMyMessage(message.text)

        assertEquals(
            listOf(message),
            viewModel.messages.value
        )
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

        coroutineScope {
            val jobs = messagesToSend.map { messagesToSend ->
                launch {
                    viewModel.sendMyMessage(messagesToSend.text)
                }
            }
            jobs.joinAll()
        }

        val receivedMessages = viewModel.messages.value

        assertEquals("Количество сообщений должно быть 100", messagesToSend.size, receivedMessages.size)

        assertEquals("Содержимое сообщений должно совпадать", messagesToSend, receivedMessages)
    }
}