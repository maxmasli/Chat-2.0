package masli.prof.chat20

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import masli.prof.chat20.models.Message
import masli.prof.chat20.models.User
import masli.prof.chat20.viewmodels.ChatViewModel
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.TimeUnit

class Client(private val chatViewModel: ChatViewModel) {

    private var socket: Socket? = null
    private var outputStream: BufferedWriter? = null
    private var inputStream: BufferedReader? = null

    private val gson = GsonBuilder().create()

    companion object {
        private const val IP = "127.0.0.1"
        private const val PORT = 8888
    }

    fun connect() {
        try {
            socket = Socket(IP, PORT)

            if (socket != null) {
                outputStream = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
                inputStream = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            }

        } catch (e: IOException) {
        }

    }

    fun listen() {
        try {
            while (socket?.isConnected == true) {

                val data = inputStream?.readLine()
                val message = gson.fromJson(data, Message::class.java)

                when (message.method) {
                    "connect" -> {
                        ChatApplication.getInstance().uuid = message.arguments.uuid.toString()
                        val app = ChatApplication.getInstance()

                        for (key in message.arguments.users.keys) {
                            message.arguments.users[key]?.let { app.users.value!!.add(it) }
                            message.arguments.users[key]?.uuid = key
                        }

                    }
                    "server_message" -> {
                        TimeUnit.MILLISECONDS.sleep(500)
                        message.isInfo = true
                        chatViewModel.inputMessage(message)
                    }
                    "enter" -> {
                        ChatApplication.getInstance().users.value!!.add(
                            User(
                                uuid = message.arguments.uuid,
                                name = message.arguments.name,
                                color = message.arguments.color
                            )
                        )

                    }
                    "leave" -> {
                        ChatApplication.getInstance().users.value!!.removeIf { user ->
                            user.uuid == message.arguments.uuid
                        }

                    }
                    "change_name" -> {
                        for (user in ChatApplication.getInstance().users.value!!) {
                            if (message.arguments.uuid == user.uuid) {
                                user.name = message.arguments.name
                                break
                            }
                        }

                    }
                    "change_color" -> {

                        for (user in ChatApplication.getInstance().users.value!!) {
                            if (message.arguments.uuid == user.uuid) {
                                user.color = message.arguments.color
                                break
                            }
                        }

                    }

                    "" -> {

                    }

                    else -> {
                        chatViewModel.inputMessage(message)
                    }
                }
                updateUsersLiveData()
            }
        } catch (e: SocketException) {
            Log.d("TAAAG", "пользователь закрыл приложение")
        }

    }

    fun sendMessage(message: Message) {
        GlobalScope.launch(Dispatchers.IO) {
            val json = gson.toJson(message)
            outputStream?.write(json)
            outputStream?.flush()
        }
    }

    fun close() {
        outputStream?.close()
        inputStream?.close()
        socket?.close()
    }

    private fun updateUsersLiveData() {
        ChatApplication.getInstance().users.postValue(ChatApplication.getInstance().users.value)///////
    }
}