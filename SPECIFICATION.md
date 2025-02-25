(Implicit) types: Num, List, Char
Operations done on wrong types (append to not list, sub/mul/div on lists, etc) result in a halt
Lists are mutable and heterogeneous 

String is List[Char]
Bool is Num (false = 0, true = 1)

Grammar:
```
Program ::= read <Var>, ..., <Var>; '\n' <Basic Block>+
Basic Block ::= <Label> : <Assignment>* <Jump>; '\n'

Var ::= [a-zA-Z_][a-zA-Z0-9_]*
Label ::= [a-zA-Z0-9_]+
Assignment ::= <Var> := <Expr>
Jump ::= goto <Label>
       | if <Expr> goto <Label> else <Label>
       | return <Expr>

Constant ::= NumLiteral
           | StringLiteral
           | CharLiteral
           | Var

NumLiteral ::= 0
             | [1-9][0-9]*
             | -[1-9][0-9]*

CharLiteral ::= \'.\'
StringLiteral ::= \".*\"

Expr ::= <Constant>
       | \( <Expr> \)
       | <Expr>[<Expr>]
       | <Expr> +/-/*/\/ <Expr>
       | <Expr> </=/> <Expr>
       | \[ \]
       | \[ <Expr> (, <Expr>)* \] 
```
