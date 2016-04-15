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
        sealed class NumTypeExpr: Expr() {
            class NumSymbol(val id: String): NumTypeExpr()
            class NumLiteral(val num: Int) : NumTypeExpr()
            class MathCalExpr(val opr: MathOP, val lp: NumTypeExpr, val rp: NumTypeExpr) : NumTypeExpr()
        }

        sealed class BoolTypeExpr: Expr() {
            class BoolSymbol(val id: String): BoolTypeExpr()
            class BoolLiteral(val value: Boolean) : BoolTypeExpr()
            class BoolCalExpr(val op: LogicOP, val lp: BoolTypeExpr, val rp: BoolTypeExpr)
            class CmpCalExpr(val op: CmpOP, val lp: NumTypeExpr, val rp: NumTypeExpr)
        }

        class SymbolExpr(val id: String) : Expr()
    }
}