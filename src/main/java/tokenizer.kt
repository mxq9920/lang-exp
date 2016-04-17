import java.util.*

/**
 * Created by ming on 2016/3/29.
 */

class Tokenizer(val code: String) {

    private var idx: Int = 0

    private fun tokenMatch(tar: String) = code.substring(idx).startsWith(tar)

    fun toTokens(): List<Token> {
        var tokens = ArrayList<Token>()
        val cs = code.toCharArray()
        val buf = StringBuilder()
        while (true) {
            if (idx < cs.size - 1) {
                val c = cs[idx]
                if (buf.isEmpty()) {
                    when (c) {
                        '(' -> {
                            tokens.add(Token.LB())
                            idx++
                        }
                        ')' -> {
                            tokens.add(Token.RB())
                            idx++
                        }
                        '{' -> {
                            tokens.add(Token.LBR())
                            idx++
                        }
                        '}' -> {
                            tokens.add(Token.RBR())
                            idx++
                        }
                        '=' -> if (tokenMatch("==")) {
                            tokens.add(Token.OP(CmpOP.EQ))
                            idx += 2
                        } else {
                            tokens.add(Token.ASSIGN())
                            idx++
                        }
                        '>' -> if (tokenMatch(">=")) {
                            tokens.add(Token.OP(CmpOP.GE))
                            idx += 2
                        } else {
                            tokens.add(Token.OP(CmpOP.GT))
                            idx++
                        }
                        '<' -> if (tokenMatch("<=")) {
                            tokens.add(Token.OP(CmpOP.LE))
                            idx += 2
                        } else {
                            tokens.add(Token.OP(CmpOP.LT))
                            idx++
                        }
                        '+' -> {
                            tokens.add(Token.OP(MathOP.ADD))
                            idx++
                        }
                        '-' -> {
                            tokens.add(Token.OP(MathOP.SUB))
                            idx++
                        }
                        '*' -> {
                            tokens.add(Token.OP(MathOP.MUL))
                            idx++
                        }
                        '/' -> {
                            tokens.add(Token.OP(MathOP.DIV))
                            idx++
                        }
                        '!' -> if (tokenMatch("!=")) {
                            tokens.add(Token.OP(CmpOP.NE))
                            idx++
                        } else {
                            tokens.add(Token.OP(LogicOP.NOT))
                            idx++
                        }
                        '&' -> if (tokenMatch("&&")) {
                            tokens.add(Token.OP(LogicOP.AND))
                            idx++
                        } else {
                            throw RuntimeException("failed to match '&&'")
                        }
                        '|' -> if (tokenMatch("||")) {
                            tokens.add(Token.OP(LogicOP.OR))
                            idx++
                        } else {
                            throw RuntimeException("failed to match '||'")
                        }
                    // not special character
                        else -> if ((c.isLetterOrDigit() || c == '_')) {
                            buf.append(c)
                            idx++
                        } else {
                            idx++
                        }
                    }
                    continue
                }
                // buf is not empty
                else if (c.isLetterOrDigit() || c == '_') {
                    buf.append(c);
                    idx++
                    continue
                }
            }
            if (buf.isNotEmpty()) {
                val token = buf.toString()
                buf.setLength(0)
                if (token.matches("\\d+".toRegex())) {
                    tokens.add(Token.NUM(token.toInt()))
                } else if (token == "true") {
                    tokens.add(Token.TRUE)
                } else if (token == "false") {
                    tokens.add(Token.FALSE)
                } else {
                    tokens.add(Token.ID(token))
                }
            }

            if (idx == cs.size - 1) {
                break
            }
        }
        return tokens
    }

}
