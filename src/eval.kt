import java.util.*

/**
 * Created by ming on 2016/3/25.
 */

private var lang = """
// start
a = 1
b = 2
c = a
print(c)
print(a)
print(b)
print(123)
"""

class Env(private val cache: HashMap<String, Int> = HashMap()) {

    fun store(id: String, value: Int) {
        cache.put(id, value)
    }

    fun fetch(id: String) = cache.get(id)
}

interface Visitor<T> {
    fun visit(env: Env, exp: Stat): T
    fun visit(env: Env, exp: Assign): T
    fun visit(env: Env, exp: Expr): T
    fun visit(env: Env, exp: Expr.IntExpr): T
    fun visit(env: Env, exp: Expr.SymbolExpr): T
    fun visit(env: Env, exp: FuncCall): T
}

class EvalVisitor : Visitor<Int> {

    override fun visit(env: Env, exp: Stat): Int {
        return when (exp) {
            is Assign -> visit(env, exp)
            is FuncCall -> visit(env, exp)
            else -> throw RuntimeException()
        }
    }

    override fun visit(env: Env, exp: Assign): Int {
        val v = visit(env, exp.value)
        env.store(exp.id, v)
        return v
    }

    override fun visit(env: Env, exp: Expr): Int {
        return when (exp) {
            is Expr.IntExpr -> visit(env, exp)
            is Expr.SymbolExpr -> visit(env, exp)
        }
    }

    override fun visit(env: Env, exp: Expr.IntExpr): Int {
        return exp.value
    }

    override fun visit(env: Env, exp: Expr.SymbolExpr): Int {
        return env.fetch(exp.id) ?: throw RuntimeException()
    }

    override fun visit(env: Env, exp: FuncCall): Int {
        var v = visit(env, exp.param)
        println("--- $v ---")
        return v
    }

}

// statement
abstract class Stat {
    open fun<T> apply(env: Env, visitor: Visitor<T>) {
        visitor.visit(env, this)
    }
}

// assignment
class Assign(val id: String, val value: Expr) : Stat() {
    override fun<T> apply(env: Env, visitor: Visitor<T>) {
        visitor.visit(env, this)
    }
}

// expression
sealed class Expr : Stat() {
    class IntExpr(val value: Int) : Expr() {
        override fun<T> apply(env: Env, visitor: Visitor<T>) {
            visitor.visit(env, this)
        }
    }

    class SymbolExpr(val id: String) : Expr() {
        override fun<T> apply(env: Env, visitor: Visitor<T>) {
            visitor.visit(env, this)
        }
    }

    override fun<T> apply(env: Env, visitor: Visitor<T>) {
        visitor.visit(env, this)
    }
}

// function call
class FuncCall(val name: String, val param: Expr) : Stat() {
    override fun<T> apply(env: Env, visitor: Visitor<T>) {
        visitor.visit(env, this)
    }
}

fun main(args: Array<String>) {
    // statements
    var lines = lang.lines().filter { !it.trim().isEmpty() }.filter { !it.trim().startsWith("//") }.map { it.trim() }
    var stats = ArrayList<Stat>()
    lines.forEach { line ->
        if (line.contains("=")) {
            var (id, exprStr) = line.split("\\s*=\\s*".toRegex())
            val expr: Expr
            if (exprStr.matches("\\d+".toRegex())) {
                expr = Expr.IntExpr(exprStr.toInt())
            } else {
                expr = Expr.SymbolExpr(exprStr)
            }
            stats.add(Assign(id, expr))
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