package masli.prof.chat20.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import masli.prof.chat20.ChatApplication
import masli.prof.chat20.MainActivity
import masli.prof.chat20.R

class UsersDialogFragment(private val activity: MainActivity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let{
            val builder = AlertDialog.Builder(it)
            val view = layoutInflater.inflate(R.layout.get_users_dialog, null, false)

            val usersLinLayout= view.findViewById<LinearLayout>(R.id.users_ll)

            ChatApplication.getInstance().users.observe(this, {list ->
                Log.d("TAAAG", "меняется список лошков")
                usersLinLayout.removeAllViews()
                for (user in ChatApplication.getInstance().users.value!!) {
                    val itemView = layoutInflater.inflate(R.layout.user_item, null, false)
                    val name = itemView.findViewById<TextView>(R.id.dialog_name_tv)
                    val circle = itemView.findViewById<TextView>(R.id.dialog_circle)
                    name.text = user.name
                    user.color?.let{color ->
                        circle.setTextColor(color)
                    }

                    val lParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    itemView.layoutParams = lParams
                    usersLinLayout.addView(itemView)
                }
            })

            builder.setView(view)
            builder.setCancelable(false)

            builder.create()
        }
    }

    companion object {
        fun newInstance(activity: MainActivity): UsersDialogFragment {
            return UsersDialogFragment(activity)
        }
    }
}