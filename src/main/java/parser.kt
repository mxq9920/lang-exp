import java.util.*

/**
 * Created by ming on 2016/3/29.
 */
class Parser(val tokens: List<Token>) {

    private var idx = 0

    fun parse(): List<ASTNode.Stat> {
        val stats = ArrayList<ASTNode.Stat>()
        while (true) {
            stats.add(parseStat())
            if (idx >= tokens.size - 1) {
                break
            }
        }
        return stats
    }

    private fun parseStat(): ASTNode.Stat {
        val token = tokens.get(idx)
        // BlockStat
        if (token is Token.LBR) {
            idx++
            val stats = ArrayList<ASTNode.Stat>()
            val resultStat = ASTNode.Stat.BlockStat(stats)
            while (true) {
                val curToken = tokens.get(idx)
                if (curToken is Token.RBR) {
                    idx++
                    break
                }
                val stat = parseStat()
                stats.add(stat)
            }
            return resultStat
        }

        if (token is Token.ID) {
            return when (token.id) {
                "if" -> parseIfStat()
                "while" -> parseWhileStat()
                "break" -> ASTNode.Stat.BreakStat()
                "continue" -> ASTNode.Stat.ContinueStat()
                else -> {
                    if (isMatchAssignStat()) {
                        return parseAssignStat()
                    } else if (isMatchFuncCallStat()) {
                        return parseFunCallStat()
                    } else throw ParseFailedException("failed to recognize first token: ${token.id}")
                }
            }
        }

        throw ParseFailedException("failed to parse stat, first token: $token")
    }

    private fun parseFunCallStat(): ASTNode.Stat.FuncCallStat {
        val id = tokens[idx] as Token.ID
        var symbol = ASTNode.Expr.SymbolExpr(id.id)
        var exprList = ArrayList<ASTNode.Expr>()
        idx += 2
        while (true) {
            val token = tokens[idx]
            if (token is Token.RB) {
                idx++
                break
            }
            val expr = parseExpr()
            exprList.add(expr)
        }
        return ASTNode.Stat.FuncCallStat(symbol, exprList)
    }

    private fun parseFactor(): ASTNode.Expr? {
        val token = tokens[idx]
        if (token is Token.NUM) {
            idx++
            return ASTNode.Expr.NumExpr(token.num)
        }

        if (token is Token.ID) {
            idx++
            return ASTNode.Expr.SymbolExpr(token.id)
        }

        if (token is Token.LB) {
            idx++
            var expr = parseExpr()
            assert(tokens[idx + 1] is Token.RB)
            idx++
            return expr
        }

        return null
    }

    private fun parseExpr(): ASTNode.Expr {
        val opStack = Stack<Token.CAL>()
        val pStack = Stack<ASTNode.Expr>()
        while (true) {
            val p = parseFactor() ?: break
            pStack.push(p)
            val opToken = tokens[idx]
            if (opToken !is Token.CAL || (!opStack.isEmpty() && opStack.lastElement().pr >= opToken.symbol.toCAL().pr)) {
                while (!opStack.empty()) {
                    val rp = pStack.pop()
                    val lp = pStack.pop()
                    val op = opStack.pop()
                    val exp = ASTNode.Expr.CalExpr(op.symbol.toOP(), lp, rp)
                    pStack.push(exp)
                }
            }

            if (opToken !is Token.CAL) {
                break
            }

            opStack.push(opToken.symbol.toCAL())
            idx++
        }
        return pStack.pop()
    }

    private fun swallow(token: Token) {
        assert(tokens[idx++] == token)
    }

    private fun parseAssignStat(): ASTNode.Stat.AssignStat {
        val symbol = tokens[idx++] as Token.ID
        swallow(Token.EQ())
        return ASTNode.Stat.AssignStat(ASTNode.Expr.SymbolExpr(symbol.id), parseExpr())
    }

    private fun parseWhileStat(): ASTNode.Stat.WhileStat {
        swallow(Token.ID("while"))
        swallow(Token.LB())
        val cond = parseExpr()
        swallow(Token.RB())
        return ASTNode.Stat.WhileStat(cond, parseStat())
    }

    private fun parseIfStat(): ASTNode.Stat.IfStat {
        swallow(Token.ID("if"))
        swallow(Token.LB())
        val cond = parseExpr()
        swallow(Token.RB())
        return ASTNode.Stat.IfStat(cond, parseStat())
    }

    private fun isMatchFuncCallStat(): Boolean {
        return tokens[idx + 1] is Token.LB
    }

    private fun isMatchAssignStat(): Boolean {
        return tokens[idx + 1] is Token.ASSIGN
    }

}

class ParseFailedException(msg: String) : Exception(msg)

