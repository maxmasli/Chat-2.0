package masli.prof.chat20

import android.app.Application

class ChatApplication : Application() {

    var red: Int = 0
    var green: Int = 255
    var blue: Int = 15

    companion object {

        private lateinit var app: ChatApplication

        fun getInstance(): ChatApplication {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}