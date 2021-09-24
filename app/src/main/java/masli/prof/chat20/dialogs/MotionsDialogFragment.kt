package masli.prof.chat20.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import masli.prof.chat20.MainActivity
import masli.prof.chat20.R
import masli.prof.chat20.models.Method
import masli.prof.chat20.models.ResponseMessage

class MotionsDialogFragment(private val activity: MainActivity, private val method: Method) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            val motionsView = layoutInflater.inflate(R.layout.motions_dialog, null, false)

            motionsView.findViewById<TextView>(R.id.dialog_response).setOnClickListener {
                activity.setMessageForResponse(ResponseMessage(name = method.arguments.name, text = method.arguments.text))
                dismiss()
            }

            builder.setView(motionsView)
            builder.setCancelable(false)


            builder.create()
        }
    }

    companion object {
        fun newInstance(activity: MainActivity, method: Method): MotionsDialogFragment {
            return MotionsDialogFragment(activity, method)
        }
    }
}