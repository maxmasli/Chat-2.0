package masli.prof.chat20

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import masli.prof.chat20.dialogs.ColorPickerDialogFragment
import masli.prof.chat20.dialogs.MotionsDialogFragment
import masli.prof.chat20.dialogs.UsernameDialogFragment
import masli.prof.chat20.dialogs.UsersDialogFragment
import masli.prof.chat20.models.Arguments
import masli.prof.chat20.models.Message
import masli.prof.chat20.viewmodels.ChatViewModel

private const val USERNAME_DIALOG = "username_dialog"
private const val COLOR_DIALOG = "color_dialog"
private const val USERS_DIALOG = "users_dialog"
private const val MOTIONS_DIALOG = "motions_dialog"

class MainActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    private lateinit var chatLinearLayout: LinearLayout
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var responseMessageTab: ConstraintLayout
    private lateinit var responseNameTextView: TextView
    private lateinit var responseMessageTextView: TextView
    private lateinit var chatScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatScrollView = findViewById(R.id.chat_sv)
        chatLinearLayout = findViewById(R.id.chat_ll)
        messageEditText = findViewById(R.id.message_ed)
        sendButton = findViewById(R.id.send_btn)
        responseMessageTab = findViewById(R.id.response_message_tab)
        responseNameTextView = findViewById(R.id.response_name_tv)
        responseMessageTextView = findViewById(R.id.response_message_tv)

        ChatApplication.getInstance().users.value = mutableListOf()

        chatViewModel.messageListLiveData.observe(this, { list ->
            addMessages(list)
        })

        chatViewModel.lastMessageLiveData.observe(this, { message ->
            if (lifecycle.currentState != Lifecycle.State.STARTED) {
                if (message.isInfo) {
                    addInfoMessage(message)
                } else {
                    addMessage(message)
                }
            }
        })

        chatViewModel.messageForResponse.observe(this, {message ->
            if (chatViewModel.messageForResponse.value == null) {
                responseMessageTab.visibility = View.GONE
            } else {
                responseMessageTab.visibility = View.VISIBLE
                responseNameTextView.text = message?.arguments?.name
                responseMessageTextView.text = message?.arguments?.text
            }
        })

        sendButton.setOnClickListener {
            if (messageEditText.text.toString().isNotEmpty()) {
                chatViewModel.outputMessage(
                    Message(
                        "message",
                        Arguments(text = messageEditText.text.toString().trim())
                    )
                )
                messageEditText.text.clear()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chatLinearLayout.removeAllViews()
        chatViewModel.addMessages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.set_name -> {
                val udf = UsernameDialogFragment.newInstance(this)
                udf.show(supportFragmentManager, USERNAME_DIALOG)
                return true
            }

            R.id.set_color -> {
                val cdf = ColorPickerDialogFragment.newInstance(this)
                cdf.show(supportFragmentManager, COLOR_DIALOG)
                return true
            }

            R.id.get_users -> {
                val udf = UsersDialogFragment.newInstance(this)
                udf.show(supportFragmentManager, USERS_DIALOG)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeColor(color: Int) {
        chatViewModel.changeColor(color)
    }

    fun saveUsername(username: String) {
        chatViewModel.changeName(username)
    }

    fun setMessageForResponse(message: Message) {
        chatViewModel.messageForResponse.value = message
    }

    private fun addInfoMessage(_message: Message) {
        val infoView = layoutInflater.inflate(R.layout.info_message_item, chatScrollView, false)
        val text = infoView.findViewById<TextView>(R.id.info_tv)
        val lParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        text.text = _message.arguments.text
        text.layoutParams = lParams

        chatLinearLayout.addView(infoView)

        chatScrollView.scrollDown()
    }

    private fun addMessage(_message: Message) {
        val itemView = layoutInflater.inflate(R.layout.message_item, chatScrollView, false)
        val name = itemView.findViewById<TextView>(R.id.name_tv)
        val message = itemView.findViewById<TextView>(R.id.message_tv)

        val response = itemView.findViewById<View>(R.id.response_message)
        val responseName = itemView.findViewById<TextView>(R.id.res_name_tv)
        val responseMessage = itemView.findViewById<TextView>(R.id.res_message_tv)

        val lParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        //задаю bg у сообщения
        //_message.arguments.color?.let { itemView.background.colorFilter = // неработает на кирпиче
        //   BlendModeColorFilter(_message.arguments.color, BlendMode.SRC_ATOP) }

        itemView.setOnClickListener {
            val mdf = MotionsDialogFragment.newInstance(this, _message)
            mdf.show(supportFragmentManager, MOTIONS_DIALOG)
        }

        _message.arguments.color?.let {
            itemView.background.setColorFilter(
                it,
                PorterDuff.Mode.MULTIPLY
            )
        }// по возможности переделать

        name.text = _message.arguments.name

        if (_message.arguments.message != null) {
            response.visibility = View.VISIBLE
            responseName.text = _message.arguments.message!!.arguments.name
            responseMessage.text = _message.arguments.message!!.arguments.text
        }

        message.text = _message.arguments.text

        lParams.bottomMargin = 8
        if (_message.arguments.uuid == ChatApplication.getInstance().uuid) lParams.gravity =
            Gravity.END
        itemView.layoutParams = lParams
        chatLinearLayout.addView(itemView)

        chatScrollView.scrollDown()
    }


    private fun addMessages(messages: MutableList<Message>) {
        if (messages.isEmpty()) return

        for (message in messages) {
            if (message.isInfo) {
                addInfoMessage(message)
            } else {
                addMessage(message)
            }
        }
    }

}