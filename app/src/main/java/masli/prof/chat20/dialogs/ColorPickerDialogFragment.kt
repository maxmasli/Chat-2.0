package masli.prof.chat20.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import masli.prof.chat20.ChatApplication
import masli.prof.chat20.MainActivity
import masli.prof.chat20.R

class ColorPickerDialogFragment(private val activity: MainActivity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        return activity.let {
            val builder = AlertDialog.Builder(it)
            val view = layoutInflater.inflate(R.layout.set_color_dialog, null, false)
            builder.setView(view)

            val background = view.findViewById<View>(R.id.color)
            val red = view.findViewById<SeekBar>(R.id.red_sb)
            val green = view.findViewById<SeekBar>(R.id.green_sb)
            val blue = view.findViewById<SeekBar>(R.id.blue_sb)

            val seekBarListener = object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {

                    background.setBackgroundColor(
                        Color.rgb(red.progress, green.progress, blue.progress)
                    )
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }

            red.progress = ChatApplication.getInstance().red
            green.progress = ChatApplication.getInstance().green
            blue.progress = ChatApplication.getInstance().blue

            background.setBackgroundColor(Color.rgb(red.progress, green.progress, blue.progress))

            red.setOnSeekBarChangeListener(seekBarListener)
            green.setOnSeekBarChangeListener(seekBarListener)
            blue.setOnSeekBarChangeListener(seekBarListener)

            builder.setPositiveButton("Apply") { _, _ ->

                ChatApplication.getInstance().apply {
                    this.red = red.progress
                    this.green = green.progress
                    this.blue = blue.progress
                }

                activity.changeColor(Color.rgb(red.progress, green.progress, blue.progress))
            }
            builder.create()
        }
    }

    companion object {
        fun newInstance(activity: MainActivity): ColorPickerDialogFragment {
            return ColorPickerDialogFragment(activity)
        }
    }
}