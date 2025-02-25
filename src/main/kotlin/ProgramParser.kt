import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

class ProgramParser {
    object ExprGrammar : Grammar<Expr>() {
        init {
            regexToken("\\s+", ignored = true)
            regexToken("\\n+", ignored = true)
        }

        private val comma by literalToken(",")
        private val lpar by literalToken("(")
        private val rpar by literalToken(")")
        private val add by literalToken("+")
        private val sub by literalToken("-")
        private val mul by literalToken("*")
        private val div by literalToken("/")
        private val les by literalToken("<")
        private val gre by literalToken(">")
        private val eq by literalToken("=")
        private val lbra by literalToken("[")
        private val rbra by literalToken("]")
        private val lcur by literalToken("{")
        private val rcur by literalToken("}")


        private val int by regexToken("-?\\d+") map { Expr.Num(it.text.toLong()) }
        private val chr by regexToken("'.'") map { Expr.Chr(it.text[1]) }
        private val str by regexToken("\".*\"") map { Expr.Lst(it.text.drop(1).dropLast(1)) }
        val variable by regexToken("[a-zA-Z_][a-zA-Z0-9_]*")  map { Expr.Var(it.text) }

        private val atom by int or chr or str or variable

        private val listItems by separated(ref(::expr), comma) map { Expr.Lst(it) }
        private val list by parser { skip(lcur) * listItems() * skip(rcur) }

        private val paren: Parser<Expr> by parser {
            skip(lpar)
            val v = expr()
            skip(rpar)
            v
        }

        private val primary: Parser<Expr> by atom or paren or list

        val subscr: Parser<Expr> by parser {
            val arr = primary()
            skip(lbra)
            val ind = expr()
            skip(rbra)
            Expr.Subscript(arr, ind)
        }

        private val mulp: Parser<Expr> by parser {
            reduce(subscr or primary /*primary*/, mul or div) { l, o, r ->
                if (o.token == mul) Expr.Mul(l, r) else Expr.Div(l, r)
            }
        }

        private val addp: Parser<Expr> by parser {
            reduce(mulp, add or sub) { l, o, r ->
                if (o.token == add) Expr.Add(l, r) else Expr.Sub(l, r)
            }
        }

        private val eqneqp: Parser<Expr> by parser {
            reduce(addp, les or gre or eq) { l, o, r ->
                when (o.token) {
                    les -> Expr.Less(l, r)
                    gre -> Expr.Greater(l, r)
                    eq -> Expr.Eq(l, r)
                    else -> throw RuntimeException("Wrong operator got inside")
                }
            }
        }
        val expr = eqneqp

        override val root by expr
    }

    object ProgramGrammar : Grammar<Program>() {
        init {
            regexToken("\\s+", ignored = true)
            regexToken("(\\n|\\n\\r)+", ignored = true)
        }

        private val comma by literalToken(",")
        private val colon by literalToken(":")
        private val semicolon by literalToken(";")
        private val assign by literalToken(":=")
        private val goto by literalToken("goto")
        private val ifT by literalToken("if")
        private val elseT by literalToken("else")
        private val returnT by literalToken("return")
        private val readT by literalToken("read")

        private val label by regexToken("[a-zA-Z0-9_]+")  map { Label(it.text) }
        private val assignment: Parser<Assignment> by parser {
            val variable = ExprGrammar.variable()
            skip(assign)
            val ex = ExprGrammar.expr()
            Assignment(variable, ex)
        }
        private val gotop: Parser<Jump.Goto> by parser {
            skip(goto)
            val lab = label()
            Jump.Goto(lab)
        }
        private val ifp: Parser<Jump.If> by parser {
            skip(ifT)
            val cond = ExprGrammar.expr()
            skip(goto)
            val labTrue = label()
            skip(elseT)
            val labFalse = label()
            Jump.If(cond, labTrue, labFalse)
        }
        private val returnp: Parser<Jump.Return> by parser {
            skip(returnT)
            val exp = ExprGrammar.expr()
            Jump.Return(exp)
        }
        private val jump: Parser<Jump> = gotop or ifp or returnp

        private val basicBlock: Parser<BasicBlock> by parser {
            val lab = label()
            skip(colon)
            val assignments = separated(assignment, comma)()
            val jumpC = jump()
            BasicBlock(lab, assignments, jumpC)
        }

        private val read: Parser<Read> by parser {
            skip(readT)
            val vars = separated(ExprGrammar.variable, comma)()
            skip(semicolon)
            Read(vars)
        }

        val program: Parser<Program> by parser {
            val readC = read()
            val blocks = separated(basicBlock, semicolon)()
            skip(semicolon)
            Program(readC, blocks)
        }

        override val root: Parser<Program> by program
    }
}