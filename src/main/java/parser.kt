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

    private fun parseNumFactor(): ASTNode.Expr.NumTypeExpr? {
        val token = tokens[idx]
        if (token is Token.NUM) {
            idx++
            return ASTNode.Expr.NumTypeExpr.NumLiteral(token.num)
        }

        if (token is Token.ID) {
            idx++
            return ASTNode.Expr.NumTypeExpr.NumSymbol(token.id)
        }

        if (token is Token.LB) {
            idx++
            var expr = parseNumExpr()
            assert(tokens[idx + 1] is Token.RB)
            idx++
            return expr
        }

        return null
    }

    private fun parseExpr(): ASTNode.Expr {
        var expr: ASTNode.Expr? = parseNumExpr()
        if (expr != null) {
            return expr
        }

        expr = parseBoolExpr()
        if (expr != null) {
            return expr
        }

        throw ParseFailedException("failed to parse Expression")
    }

    // !expr
    // expr || expr
    // expr && expr
    // numExpr (> >= < <= == !=) numExpr
    private fun parseBoolExpr(): ASTNode.Expr.BoolTypeExpr? {
        val token = tokens[idx]
        // true
        if (token == Token.TRUE) {
            idx++
            return ASTNode.Expr.BoolTypeExpr.BoolLiteral(true)
        }

        // false
        if (token == Token.FALSE) {
            idx++
            return ASTNode.Expr.BoolTypeExpr.BoolLiteral(false)
        }

        // numExpr cmpOP numExpr
        var exprl = parseNumExpr()
        if (exprl != null) {
            val op = tokens[idx]
            if (op !is Token.OP || op.op !is CmpOP) {
                throw ParseFailedException("parse num cmp expr, but operator is invalid [$op]")
            }

            idx++
            var exprr: ASTNode.Expr.NumTypeExpr? = parseNumExpr() ?: throw ParseFailedException("parse num cmp expr failed, but second arg is not num expr")

            return ASTNode.Expr.BoolTypeExpr.CmpCalExpr(op.op, exprl, exprr!!)
        }

        // todo
        return null
    }

    private fun parseNumExpr(): ASTNode.Expr.NumTypeExpr? {
        val opStack = Stack<Token.OP>()
        val pStack = Stack<ASTNode.Expr.NumTypeExpr>()
        while (true) {
            val p = parseNumFactor() ?: break
            pStack.push(p)
            val opToken = tokens[idx]
            if (opToken !is Token.OP || opToken.op !is MathOP || (!opStack.isEmpty() && opStack.lastElement().op.priority >= opToken.op.priority)) {
                while (!opStack.empty()) {
                    val rp = pStack.pop()
                    val lp = pStack.pop()
                    val op = opStack.pop()
                    val exp = ASTNode.Expr.NumTypeExpr.MathCalExpr(op.op as MathOP, lp, rp)
                    pStack.push(exp)
                }
            }

            if (opToken !is Token.OP || opToken.op !is MathOP) {
                break
            }

            opStack.push(opToken)
            idx++
        }
        return if (pStack.empty()) null else pStack.pop()
    }

    private fun swallow(token: Token) {
        assert(tokens[idx++] == token)
    }

    private fun parseAssignStat(): ASTNode.Stat.AssignStat {
        val symbol = tokens[idx++] as Token.ID
        swallow(Token.ASSIGN)
        return ASTNode.Stat.AssignStat(ASTNode.Expr.SymbolExpr(symbol.id), parseExpr())
    }

    private fun parseWhileStat(): ASTNode.Stat.WhileStat {
        swallow(Token.ID("while"))
        swallow(Token.LB)
        val cond = parseBoolExpr()!!
        swallow(Token.RB)
        return ASTNode.Stat.WhileStat(cond, parseStat())
    }

    private fun parseIfStat(): ASTNode.Stat.IfStat {
        swallow(Token.ID("if"))
        swallow(Token.LB)
        val cond = parseBoolExpr()!!
        swallow(Token.RB)
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

