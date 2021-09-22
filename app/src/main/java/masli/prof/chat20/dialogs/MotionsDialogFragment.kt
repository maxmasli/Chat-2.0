package masli.prof.chat20.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import masli.prof.chat20.MainActivity
import masli.prof.chat20.R
import masli.prof.chat20.models.Message

class MotionsDialogFragment(private val activity: MainActivity, private val message: Message) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            val motionsView = layoutInflater.inflate(R.layout.motions_dialog, null, false)

            motionsView.findViewById<TextView>(R.id.dialog_response).setOnClickListener {
                activity.setMessageForResponse(message)
                dismiss()
            }

            builder.setView(motionsView)
            builder.setCancelable(false)


            builder.create()
        }
    }

    companion object {
        fun newInstance(activity: MainActivity, _message: Message): MotionsDialogFragment {
            return MotionsDialogFragment(activity, _message)
        }
    }
}