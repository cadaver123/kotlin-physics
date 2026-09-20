package components.generic

enum class Flag(val bit: Int) {
}

@JvmInline
value class Flags(val bits: Int = 0) {
    operator fun contains(p: Flag) = bits and p.bit != 0
    operator fun plus(p: Flag) = Flags(bits or p.bit)
    operator fun minus(p: Flag) = Flags(bits and p.bit.inv())

    fun toSet() = Flag.entries.filter { it in this }.toSet()
    override fun toString() = toSet().joinToString(prefix = "[", postfix = "]")
}

fun flagsOf(vararg ps: Flag) =
    Flags(ps.fold(0) { acc, p -> acc or p.bit })