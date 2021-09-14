package masli.prof.chat20.models

data class Arguments(
    val name: String? = null,
    val uuid: String? = null,
    val text: String? = null,
    val color: Int? = null
) {
    override fun toString(): String {
        return "$name $uuid $text $color"
    }
}