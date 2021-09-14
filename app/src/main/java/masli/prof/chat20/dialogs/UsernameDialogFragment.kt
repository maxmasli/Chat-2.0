package masli.prof.chat20.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import masli.prof.chat20.MainActivity
import masli.prof.chat20.R

class UsernameDialogFragment(private val activity: MainActivity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(layoutInflater.inflate(R.layout.set_username_dialog, null, false))
            builder.setCancelable(false)
            builder.setPositiveButton("OK") {_, _ ->
                activity.saveUsername(dialog?.findViewById<EditText>(R.id.edit_nickname_dialog_ed)?.text.toString())
            }
            builder.create()
        }
    }

    companion object {
        fun newInstance(activity: MainActivity): UsernameDialogFragment {
            return UsernameDialogFragment(activity)
        }
    }
}