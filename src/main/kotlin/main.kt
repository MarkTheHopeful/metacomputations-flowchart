import java.io.File

fun main() {
    val filename = readln()
    val fileText = File(filename).readText()
    println(fileText)
    println(ProgramParser.ProgramGrammar.parse(fileText))
}