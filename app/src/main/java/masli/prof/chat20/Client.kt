package masli.prof.chat20

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import masli.prof.chat20.models.Message
import masli.prof.chat20.viewmodels.ChatViewModel
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.net.SocketException

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
            Log.d("TAAAG", e.message.toString())
        }

    }

    fun listen() {
        try {
            while (socket?.isConnected == true) {
                val data = inputStream?.readLine()
                val message = gson.fromJson(data, Message::class.java)

                when (message.method) {
                    "connect" -> {
                        Log.d("TAAAG", message.arguments.uuid.toString() + " connect")
                        MainActivity.uuid = message.arguments.uuid.toString()
                    }
                    "server_message" -> {
                        Thread.sleep(1000) //фууу каааал
                        Log.d("TAAAG", message.arguments.text.toString() + " info")
                        message.isInfo = true
                        chatViewModel.inputMessage(message)
                    }
                    else -> {
                        chatViewModel.inputMessage(message)
                    }
                }
            }
        } catch (e: SocketException) {
            //пользователь закрыл сообщение
        }
    }

    fun sendMessage(message: Message) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val json = gson.toJson(message)
                Log.d("TAAAG", json)
                outputStream?.write(json)
                outputStream?.flush()
            } catch (e: Exception) {
                Log.e("TAAAG", e.message.toString())
            }

        }
    }

    fun close() {
        outputStream?.close()
        inputStream?.close()
        socket?.close()
    }
}