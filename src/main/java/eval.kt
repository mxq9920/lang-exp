/**
 * Created by ming on 2016/3/28.
 */
@file:JvmName("LittleSimpleLangEval")

val sample = """
a = (1 + 2) * 3
b = 4 - a * 5
print(a * b)
if (a + b == 10) {
    print(123)
}
i = 0
while (i <= 10) {
    print(i)
    i = i - 1
}
"""

fun main(args: Array<String>) {
    val tokenizer = Tokenizer(sample)
    val tokens = tokenizer.toTokens()
    println(tokens)
    //    val parser = Parser(tokens)
    //    val ast = parser.parse()
    //    println(ast)
}