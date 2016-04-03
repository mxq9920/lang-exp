/**
 * Created by ming on 2016/3/29.
 */
sealed class Token {
    // variable/function name
    class ID(val id: String) : Token() {
        override fun toString(): String {
            return "ID:[$id]"
        }
    }

    // =
    class ASSIGN : Token()

    // ==
    class EQ : Token()

    // 1-9+
    class NUM(val num: Int) : Token() {
        override fun toString(): String {
            return "NUM:[$num]"
        }
    }

    // (
    class LB : Token()

    // )
    class RB : Token()

    // {
    class LBR : Token()

    // }
    class RBR : Token()

    // calculate operator
    class OP(val op: OPT) : Token()
}

interface OPT {
    val priority: Int
}

enum class MathOP(override  val priority: Int, val str: String) : OPT {
    ADD(4, "+"),
    SUB(4, "-"),
    MUL(5, "*"),
    DIV(5, "/"),
}

enum class BoolOP(override val priority: Int, val str: String) : OPT {
    AND(2, "&&"),
    OR(1, "||"),
    NOT(3, "!"),
}

enum class CmpOP(override val priority: Int,val str: String) : OPT {
    NE("!="),
    EQ("=="),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<=");

    private constructor(str: String): this(0, str)
}