/**
 * Created by ming on 2016/3/29.
 */
sealed class Node {
    sealed class Stat : Node() {
        class IfStat(val cond: Expr, val block: BlockStat) : Stat()
        class WhileStat(val cond: Expr, val block: BlockStat) : Stat()
        class BreakStat : Stat()
        class ContinueStat : Stat()
        class AssignStat(val id: Expr.SymbolExpr, val expr: Expr) : Stat()
        class FuncCallStat(val id: Expr.SymbolExpr, val params: List<Expr>) : Stat()
        class BlockStat(val stats: List<Stat>) : Stat()
    }

    sealed class Expr : Node() {
        class NumExpr(val num: Int) : Expr()
        class SymbolExpr(val id: String) : Expr()
        class CalExpr(val opr: String, lp: Expr, rp: Expr) : Expr()
    }
}