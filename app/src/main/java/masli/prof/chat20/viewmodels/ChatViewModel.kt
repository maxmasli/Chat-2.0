package masli.prof.chat20.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import masli.prof.chat20.ChatApplication
import masli.prof.chat20.Client
import masli.prof.chat20.models.Arguments
import masli.prof.chat20.models.Method
import masli.prof.chat20.models.ResponseMessage


class ChatViewModel : ViewModel() {

    private lateinit var client: Client
    private val messages = mutableListOf<Method>()

    val messageForResponse = MutableLiveData<ResponseMessage?>()
    val messageListLiveData = MutableLiveData<MutableList<Method>>()
    val lastMessageLiveData = MutableLiveData<Method>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            client = Client(this@ChatViewModel)
            client.connect()
            client.listen()
        }
    }

    fun inputMessage(method: Method) {
        if (messageForResponse.value != null && ChatApplication.getInstance().uuid == method.arguments.uuid) {
            method.arguments.message = messageForResponse.value
            messageForResponse.postValue(null)
        }
        messages.add(method)
        lastMessageLiveData.postValue(method)

    }

    fun outputMessage(method: Method) {
        client.sendMessage(method)
    }

    fun changeName(username: String) {
        client.sendMessage(Method(method = "change_name", Arguments(name = username)))
    }

    fun changeColor(color: Int) {
        client.sendMessage(Method(method = "change_color", Arguments(color = color)))
    }

    fun addMessages() {
        messageListLiveData.value = messages
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }

}