import java.util.*

/**
 * Created by ming on 2016/3/29.
 */
class Parser(val tokens: List<Token>) {

    private var idx = 0

    fun parse(): List<Node.Stat> {
        val stats = ArrayList<Node.Stat>()
        while (true) {
            stats.add(parseStat())
            if (idx >= stats.size - 1) {
                break
            }
        }
        return stats
    }

    private fun parseStat(): Node.Stat {
        val token = tokens.get(idx)
        // BlockStat
        if (token is Token.LBR) {
            idx++
            val stats = ArrayList<Node.Stat>()
            val resultStat = Node.Stat.BlockStat(stats)
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
            idx--
            return when (token.id) {
                "if" -> parseIfStat()
                "while" -> parseWhileStat()
                "break" -> Node.Stat.BreakStat()
                "continue" -> Node.Stat.ContinueStat()
                else -> {
                    if (isMatchAssignStat()) {
                        return parseAssignStat()
                    } else if (isMatchFuncCallStat()) {
                        return parseFunCallStat()
                    } else throw ParseFailedExceptin()
                }
            }
        }

        throw ParseFailedExceptin()
    }

    private fun parseFunCallStat(): Node.Stat.FuncCallStat {
        val id = tokens[idx] as Token.ID
        var symbol = Node.Expr.SymbolExpr(id.id)
        var exprList = ArrayList<Node.Expr>()
        idx += 2
        while (true) {
            val token = tokens[idx]
            if (token is Token.LB) {
                idx++
                break
            }
            val expr = parseExpr()
            exprList.add(expr)
        }
        return Node.Stat.FuncCallStat(symbol, exprList)
    }

    private fun parseExpr(): Node.Expr {

    }

    private fun parseAssignStat(): Node.Stat.AssignStat {
        return Node.Stat.AssignStat()
    }

    private fun parseWhileStat(): Node.Stat.WhileStat {
        return Node.Stat.WhileStat()
    }

    private fun parseIfStat(): Node.Stat.IfStat {
        return Node.Stat.IfStat()
    }

    private fun isMatchFuncCallStat(): Boolean {
        return tokens[idx + 1] is Token.LB
    }

    private fun isMatchAssignStat(): Boolean {
        return tokens[idx + 1] is Token.EQ
    }

}

class ParseFailedExceptin : Exception() {

}