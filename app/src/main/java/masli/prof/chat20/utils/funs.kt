package masli.prof.chat20

import android.view.View
import android.widget.ScrollView

fun ScrollView.scrollDown(){
    this.post {
        Runnable {
            this.fullScroll(View.FOCUS_DOWN)
        }.run()
    }
}