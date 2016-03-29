/**
 * Created by ming on 2016/3/28.
 */
@file:JvmName("LittleSimpleLangEval")

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
        val cs = code.toCharArray()
        var idx = 0
        val buf = StringBuilder()
        while (true) {
            if (idx == cs.size - 1) {
                break
            }
            val c = cs[idx]
            if (buf.isEmpty()) {
                var specialToken = true
                when (c) {
                    '(' -> tokens.add(Token.LB())
                    ')' -> tokens.add(Token.RB())
                    '{' -> tokens.add(Token.LBR())
                    '}' -> tokens.add(Token.RBR())
                    '=' -> tokens.add(Token.EQ())
                    '+' -> tokens.add(Token.CAL.ADD())
                    '-' -> tokens.add(Token.CAL.SUB())
                    '*' -> tokens.add(Token.CAL.MUL())
                    '/' -> tokens.add(Token.CAL.DIV())
                    else -> specialToken = false
                }

                if (specialToken) {
                    idx++
                    continue
                }
            }

            if ((c.isLetterOrDigit() || c == '_') && idx != cs.size - 1) {
                buf.append(c)
                idx++
                continue
            } else if (!buf.isEmpty()) {
                val token = buf.toString()
                buf.setLength(0)
                if (token.matches("\\d+".toRegex())) {
                    tokens.add(Token.NUM(token.toInt()))
                } else {
                    tokens.add(Token.ID(token))
                }
                continue
            }

            idx++
        }
        return tokens
    }

}

fun main(args: Array<String>) {
    val tokenizer = Tokenizer(sample)
    val tokens = tokenizer.toTokens()
    println(tokens)
}