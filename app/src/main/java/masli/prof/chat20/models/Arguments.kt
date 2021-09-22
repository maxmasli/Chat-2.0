package masli.prof.chat20.models

data class Arguments(
    val name: String? = null,
    val uuid: String? = null,
    val text: String? = null,
    val color: Int? = null,
    var message: Message? = null,
    val users: HashMap<String, User> = HashMap()
) {
    override fun toString(): String {
        return "$name $uuid $text $color"
    }
}