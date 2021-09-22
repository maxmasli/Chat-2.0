package masli.prof.chat20.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import masli.prof.chat20.Client
import masli.prof.chat20.models.Arguments
import masli.prof.chat20.models.Message


class ChatViewModel : ViewModel() {

    private lateinit var client: Client
    private val messages = mutableListOf<Message>()

    val messageForResponse = MutableLiveData<Message?>()
    val messageListLiveData = MutableLiveData<MutableList<Message>>()
    val lastMessageLiveData = MutableLiveData<Message>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            client = Client(this@ChatViewModel)
            client.connect()
            client.listen()
        }
    }

    fun inputMessage(message: Message) {
        if (messageForResponse.value != null) {
            message.arguments.message = messageForResponse.value
            messageForResponse.postValue(null)
        }
        messages.add(message)
        lastMessageLiveData.postValue(message)

    }

    fun outputMessage(message: Message) {
        client.sendMessage(message)
    }

    fun changeName(username: String) {
        client.sendMessage(Message(method = "change_name", Arguments(name = username)))
    }

    fun changeColor(color: Int) {
        client.sendMessage(Message(method = "change_color", Arguments(color = color)))
    }

    fun addMessages() {
        messageListLiveData.value = messages
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }

}