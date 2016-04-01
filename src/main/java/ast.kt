import java.util.*

/**
 * Created by ming on 2016/3/29.
 */
sealed class ASTNode {
    sealed class Stat : ASTNode() {
        class IfStat(val cond: Expr, val block: Stat) : Stat()
        class WhileStat(val cond: Expr, val block: Stat) : Stat()
        class BreakStat : Stat()
        class ContinueStat : Stat()
        class AssignStat(val id: Expr.SymbolExpr, val expr: Expr) : Stat()
        class FuncCallStat(val id: Expr.SymbolExpr, val params: List<Expr>) : Stat()
        class BlockStat(val stats: ArrayList<Stat>) : Stat()
    }

    sealed class Expr : ASTNode() {
        class NumExpr(val num: Int) : Expr()
        class SymbolExpr(val id: String) : Expr()
        class CalExpr(val opr: OP, val lp: Expr, val rp: Expr) : Expr()
    }
}

enum class OP {
    ADD, SUB, MUL, DIV
}

fun String.toCAL() = when (this) {
    "+" -> Token.CAL.ADD()
    "-" -> Token.CAL.SUB()
    "*" -> Token.CAL.MUL()
    "/" -> Token.CAL.DIV()
    else -> throw RuntimeException()
}

fun String.toOP() = when (this) {
    "+" -> OP.ADD
    "-" -> OP.SUB
    "*" -> OP.MUL
    "/" -> OP.DIV
    else -> throw RuntimeException()
}