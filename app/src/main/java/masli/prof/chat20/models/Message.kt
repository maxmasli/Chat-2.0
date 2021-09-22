package masli.prof.chat20.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Message(
    val method: String,
    val arguments: Arguments,
    @SerializedName("isInfo") var isInfo: Boolean = false
)