package masli.prof.chat20

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import masli.prof.chat20.models.Method
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
                val method = gson.fromJson(data, Method::class.java)

                when (method.method) {
                    "connect" -> {
                        ChatApplication.getInstance().uuid = method.arguments.uuid.toString()
                        val app = ChatApplication.getInstance()

                        for (key in method.arguments.users.keys) {
                            method.arguments.users[key]?.let { app.users.value!!.add(it) }
                            method.arguments.users[key]?.uuid = key
                        }

                    }
                    "server_message" -> {
                        TimeUnit.MILLISECONDS.sleep(500)
                        method.isInfo = true
                        chatViewModel.inputMessage(method)
                    }
                    "enter" -> {
                        ChatApplication.getInstance().users.value!!.add(
                            User(
                                uuid = method.arguments.uuid,
                                name = method.arguments.name,
                                color = method.arguments.color
                            )
                        )

                    }
                    "leave" -> {
                        ChatApplication.getInstance().users.value!!.removeIf { user ->
                            user.uuid == method.arguments.uuid
                        }
                    }

                    "change_name" -> {
                        for (user in ChatApplication.getInstance().users.value!!) {
                            if (method.arguments.uuid == user.uuid) {
                                user.name = method.arguments.name
                                break
                            }
                        }

                    }
                    "change_color" -> {

                        for (user in ChatApplication.getInstance().users.value!!) {
                            if (method.arguments.uuid == user.uuid) {
                                user.color = method.arguments.color
                                break
                            }
                        }
                    }

                    "message" -> {
                        chatViewModel.inputMessage(method)
                    }

                    else -> {
                        Log.e("TAAAG", "invalid method")
                    }
                }
                updateUsersLiveData()
            }
        } catch (e: SocketException) {
            Log.d("TAAAG", "пользователь закрыл приложение")
        }

    }

    fun sendMessage(method: Method) {
        GlobalScope.launch(Dispatchers.IO) {
            val json = gson.toJson(method)
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