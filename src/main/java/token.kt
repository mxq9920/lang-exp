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
    sealed class CAL : Token() {
        class ADD : CAL()
        class SUB : CAL()
        class MUL : CAL()
        class DIV : CAL()
        class MOD : CAL()
    }
}