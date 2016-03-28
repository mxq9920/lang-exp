/**
 * Created by ming on 2016/3/28.
 */
@file:JvmName("SimpleLangEval")

import java.util.*

val sample = """
a = (1 + 2) * 3
b = 4 - a * 5
print(a * b)
"""

sealed class Token {
    // variable/function name
    class ID(val id: String) : Token()

    // =
    class EQ : Token()

    // 1-9+
    class NUM(val num: Int) : Token()

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
    }
}

class Tokenizer(val code: String) {

    fun toTokens(): List<Token> {
        var tokens = ArrayList<Token>()
        return tokens
    }

}