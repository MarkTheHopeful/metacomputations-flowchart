data class Read(val variables: List<Expr.Var>)

data class Assignment(val variable: Expr.Var, val expr: Expr)

data class Label(val label: String, var resolvedLine: Int = -1)

sealed class Jump {
    data class Goto(val label: Label) : Jump()

    data class If(val condition: Expr, val labelTrue: Label, val labelFalse: Label) : Jump()

    data class Return(val expr: Expr) : Jump()
}

data class BasicBlock(val label: Label, val assignments: List<Assignment>, val jump: Jump)

data class Program(val read: Read, val steps: List<BasicBlock>)