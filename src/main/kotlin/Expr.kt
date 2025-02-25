sealed class Expr {
    data class Num(val value: Long) : Expr()
    data class Chr(val value: Char) : Expr()

    data class Var(val name: String) : Expr()
    data class Lst(val value: List<Expr>) : Expr() {
        constructor(value: String) : this(value.toList().map { Chr(it) })
    }

    data class Add(val lhs: Expr, val rhs: Expr) : Expr()
    data class Sub(val lhs: Expr, val rhs: Expr) : Expr()
    data class Mul(val lhs: Expr, val rhs: Expr) : Expr()
    data class Div(val lhs: Expr, val rhs: Expr) : Expr()
    data class Less(val lhs: Expr, val rhs: Expr) : Expr()
    data class Eq(val lhs: Expr, val rhs: Expr) : Expr()
    data class Greater(val lhs: Expr, val rhs: Expr) : Expr()

    data class Subscript(val arr: Expr, val ind: Expr) : Expr()

}