import java.util.*

/**
 * Created by ming on 2016/3/25.
 */

private var lang = """
// start
a = 1
b = 2
c = 3
print(c)
print(a)
print(b)
print(123)
"""

class Env

interface Visitor {
    fun visit(env: Env, exp: Stat)
    fun visit(env: Env, exp: Assign)
    fun visit(env: Env, exp: Expr)
    fun visit(env: Env, exp: Expr.IntExpr)
    fun visit(env: Env, exp: Expr.SymbolExpr)
    fun visit(env: Env, exp: FuncCall)
}

class EvalVisitor: Visitor {

    override fun visit(env: Env, exp: Stat) {
        throw UnsupportedOperationException()
    }

    override fun visit(env: Env, exp: Assign) {
        throw UnsupportedOperationException()
    }

    override fun visit(env: Env, exp: Expr) {
        throw UnsupportedOperationException()
    }

    override fun visit(env: Env, exp: Expr.IntExpr) {
        throw UnsupportedOperationException()
    }

    override fun visit(env: Env, exp: Expr.SymbolExpr) {
        throw UnsupportedOperationException()
    }

    override fun visit(env: Env, exp: FuncCall) {
        throw UnsupportedOperationException()
    }

}

// statement
abstract class Stat {
    open fun apply(env: Env, visitor: Visitor) {
        visitor.visit(env, this)
    }
}

// assignment
class Assign(val id: String, val value: Expr) : Stat() {
    override fun apply(env: Env, visitor: Visitor) {
        visitor.visit(env, this)
    }
}

// expression
sealed class Expr : Stat() {
    class IntExpr(val value: Int) : Expr() {
        override fun apply(env: Env, visitor: Visitor) {
            visitor.visit(env, this)
        }
    }

    class SymbolExpr(val id: String) : Expr() {
        override fun apply(env: Env, visitor: Visitor) {
            visitor.visit(env, this)
        }
    }

    override fun apply(env: Env, visitor: Visitor) {
        visitor.visit(env, this)
    }
}

// function call
class FuncCall(val name: String, vararg params: Expr) : Stat() {
    override fun apply(env: Env, visitor: Visitor) {
        visitor.visit(env, this)
    }
}

fun main(args: Array<String>) {
    // statements
    var lines = lang.lines().filter { !it.trim().isEmpty() }.filter { !it.trim().startsWith("//") }.map { it.trim() }
    var stats = ArrayList<Stat>()
    lines.forEach { line ->
        if (line.contains("=")) {
            var (id, expr) = line.split("\\s*=\\s*".toRegex())
            stats.add(Assign(id, Expr.IntExpr(expr.toInt())))
        } else if (line.startsWith("print")) {
            var exprStr = line.substringAfter("print(").substringBeforeLast(')')
            val expr: Expr
            if (exprStr.matches("\\d+".toRegex())) {
                expr = Expr.IntExpr(exprStr.toInt())
            } else {
                expr = Expr.SymbolExpr(exprStr)
            }
            stats.add(FuncCall("print", expr))
        }
    }
    var env = Env()
    var visitor = EvalVisitor()
    stats.forEach { visitor.visit(env, it) }
}