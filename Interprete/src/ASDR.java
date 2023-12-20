import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*Dudas:
* suma(x,y) error, al parecer falta la coma
* pedir que suba las pruebas para el parser
*
*
* Cuando hay Epsilon, se retorna null?
* Cuando hay error igual se retorna null?
* Cómo saber qupe tipo regresa/retorna cada funcion?
* Por que se llama el ExprCallFunction?
* */
public class ASDR implements Parser{

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse() {

        try{
            PROGRAM();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }


        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("correcto");
            return  true;
        }else {
            System.out.println("Se encontraron errores");
            return false;
        }

    }

    // PROGRAM -> DECLARATION
    public void PROGRAM() throws Exception {
        List<Statement> statements = new ArrayList<>();
        DECLARATION(statements);
    }

    // DECLARATION -> FUN_DECL DECLARATION | VAR_DECL DECLARATION | STATEMENT DECLARATION | Ɛ
    public void DECLARATION(List<Statement> statements) throws Exception {

        if(preanalisis.tipo == TipoToken.FUN)
        {
            Statement stmt = FUN_DECL();
            statements.add(stmt);
            DECLARATION(statements);
        }

        else if (preanalisis.tipo == TipoToken.VAR)
        {
            Statement stmt = VAR_DECL();
            statements.add(stmt);
            DECLARATION(statements);
        }

        else if (preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL ||
                preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE ||
                preanalisis.tipo == TipoToken.LEFT_BRACE)
        {
            Statement stmt = STATEMENT();
            statements.add(stmt);
            DECLARATION(statements);
        }
    }

    //FUN_DECL -> fun FUNCTION
    public StmtFunction FUN_DECL() throws Exception {
        match(TipoToken.FUN);
        return FUNCTION();
    }

    // VAR_DECL -> var id VAR_INIT ;
    public StmtVar VAR_DECL() throws Exception {

        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        Token t = previous();
        Expression expr = VAR_INIT();
        match(TipoToken.SEMICOLON);
        return new StmtVar(t,expr);
    }

    // VAR_INIT -> = EXPRESSION | Ɛ
    private Expression VAR_INIT() throws Exception {

        if (preanalisis.tipo == TipoToken.EQUAL)
        {
            match(TipoToken.EQUAL);
            return EXPRESSION();
        }

        return null;
    }

    // STATEMENT -> EXPR_STMT | FOR_STMT | IF_STMT | PRINT_STMT | RETURN_STMT | WHILE_STMT | BLOCK
    public Statement STATEMENT() throws Exception {
        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN || preanalisis.tipo == TipoToken.TRUE ||
                preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL ||
                preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING ||
                preanalisis.tipo == TipoToken.MINUS)
        {
            return EXPR_STMT();
        }
        else if(preanalisis.tipo == TipoToken.FOR)
        {
            return FOR_STMT();
        }
        else if (preanalisis.tipo == TipoToken.IF)
        {
           return IF_STMT();
        }
        else if(preanalisis.tipo == TipoToken.PRINT)
        {
           return PRINT_STMT();
        }
        else if(preanalisis.tipo == TipoToken.RETURN)
        {
           return RETURN_STMT();
        }
        else if(preanalisis.tipo == TipoToken.WHILE)
        {
           return WHILE_STMT();
        }
        else if(preanalisis.tipo == TipoToken.LEFT_BRACE)
        {
           return BLOCK();
        }
        else
        {
            hayErrores = true;
            throw new RuntimeException("Error");
        }
    }

    // EXPR_STMT -> EXPRESSION ;
    public StmtExpression EXPR_STMT() throws Exception {

        Expression expr = EXPRESSION();
        match(TipoToken.SEMICOLON);

        return new StmtExpression(expr);
    }

    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    public Statement FOR_STMT() throws Exception {

        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        Statement init = FOR_STMT_1();
        Expression cond = FOR_STMT_2();
        Expression inc = FOR_STMT_3();
        match(TipoToken.RIGHT_PAREN);
        Statement body = STATEMENT();

        if(inc != null){
            body = new StmtBlock(Arrays.asList(body, new StmtExpression(inc)));
        }

        if(cond == null){
            cond = new ExprLiteral(true);
        }
        body = new StmtLoop(cond, body);

        if(init != null){
            body = new StmtBlock(Arrays.asList(init, body));
        }

        return body;
    }

    // FOR_STMT_1 -> VAR_DECL | EXPR_STMT | ;
    public Statement FOR_STMT_1() throws Exception {

        if(preanalisis.tipo == TipoToken.VAR)
        {
            return VAR_DECL();
        }
        else if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS)
        {
            return EXPR_STMT();
        }
        else if (preanalisis.tipo == TipoToken.SEMICOLON)
        {
            match(TipoToken.SEMICOLON);
            return null;
        }
        else
        {
            hayErrores = true;
            throw new RuntimeException("Se esperaba ';' ");
            //System.out.println("Se esperaba ';' ");
        }
    }

    // FOR_STMT_2 -> EXPRESSION ; | ;
    private Expression FOR_STMT_2() throws Exception {

        if (preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            Expression expr = EXPRESSION();
            match(TipoToken.SEMICOLON);
            return expr;
        }
        else if(preanalisis.tipo == TipoToken.SEMICOLON)
        {
            match(TipoToken.SEMICOLON);
            return null;
        }
        else
        {
            hayErrores = true;
            throw new RuntimeException("Error");
            //System.out.println("error");
        }
    }

    // FOR_STMT_3 -> EXPRESSION | Ɛ
    public Expression FOR_STMT_3() throws Exception {
        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS||
                preanalisis.tipo == TipoToken.BANG)
        {
            return EXPRESSION();
        }
        return null;
    }

    // IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    public StmtIf IF_STMT() throws Exception {

        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression cond= EXPRESSION();
        match(TipoToken.RIGHT_PAREN);
        Statement thenBranch = STATEMENT();
        Statement elseBranch = ELSE_STATEMENT();
        return new StmtIf(cond,thenBranch,elseBranch);
    }

    // ELSE_STATEMENT -> else STATEMENT | Ɛ
    public Statement ELSE_STATEMENT() throws Exception {

        if(preanalisis.tipo == TipoToken.ELSE)
        {
            match(TipoToken.ELSE);
            return STATEMENT();
        }
        return null;
    }

    // PRINT_STMT -> print EXPRESSION ;
    public StmtPrint PRINT_STMT() throws Exception {
        match(TipoToken.PRINT);
        Expression expr = EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtPrint(expr);
    }

    // RETURN_STMT -> return RETURN_EXP_OPC ;
    public StmtReturn RETURN_STMT() throws Exception {
        match(TipoToken.RETURN);
        Expression expr = RETURN_EXP_OPC();
        match(TipoToken.SEMICOLON);
        return new StmtReturn(expr);
    }

    // RETURN_EXP_OPC -> EXPRESSION | Ɛ
    private Expression RETURN_EXP_OPC() throws Exception {

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            return EXPRESSION();
        }
        return null;
    }

    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    public StmtLoop WHILE_STMT() throws Exception {
        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        Expression cond = EXPRESSION();
        match(TipoToken.RIGHT_PAREN);
        Statement body = STATEMENT();
        return new StmtLoop(cond,body);
    }

    // BLOCK -> { DECLARATION }
    public StmtBlock BLOCK() throws Exception {

        match(TipoToken.LEFT_BRACE);

        List<Statement> statements = new ArrayList<>();
        DECLARATION(statements);
        match(TipoToken.RIGHT_BRACE);

        return new StmtBlock(statements);
    }

    //--------------------------------EXPRESSIONS-------------------------------------------

    // EXPRESSION -> ASSIGNMENT
    private Expression EXPRESSION() throws Exception {

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            return ASSIGNMENT();
        }
        else
        {
            hayErrores = true;
            throw new RuntimeException("Error");
            //System.out.println("error");
        }
    }

    // ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    private Expression ASSIGNMENT() throws Exception {
        Expression expr = LOGIC_OR();
        return ASSIGNMENT_OPC(expr);
    }

    // ASSIGNMENT_OPC -> = EXPRESSION | Ɛ
    private Expression ASSIGNMENT_OPC(Expression expr) throws Exception {

        if(preanalisis.tipo == TipoToken.EQUAL)
        {
            if(expr instanceof ExprVariable){
                match(TipoToken.EQUAL);
                Token t = ((ExprVariable)expr).name; //ExprVariable es la que es una variable cualquiera como NumIterac
                Expression value = EXPRESSION();
                return new ExprAssign(t,value);
            }
            else{
                throw new Exception("No es una expresión variable");//esto es lo que puedo verificar hasta la siguiente etapa de análisis semántico, pero pues lo hago de una vez
            }
        }
        return expr;
    }

    // LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    private Expression LOGIC_OR() throws Exception {
        Expression expr = LOGIC_AND();
        return LOGIC_OR_2(expr);
    }

    // LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2 | Ɛ
    private Expression LOGIC_OR_2(Expression expr) throws Exception {
        if(preanalisis.tipo == TipoToken.OR)
        {
            match(TipoToken.OR);
            Token operador = previous();
            Expression right = LOGIC_AND();
            LOGIC_OR_2(new ExprLogical(expr,operador,right));
        }
        return expr;
    }

    // LOGIC_AND -> EQUALITY LOGIC_AND_2
    private Expression LOGIC_AND() throws Exception {
        Expression expr = EQUALITY();
        return LOGIC_AND_2(expr);
    }

    // LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2 | Ɛ
    private Expression LOGIC_AND_2(Expression expr) throws Exception {
        if(preanalisis.tipo == TipoToken.AND)
        {
            match(TipoToken.AND);
            Token operador = previous();
            Expression right = EQUALITY();
            LOGIC_AND_2(new ExprLogical(expr,operador,right));
        }
        return expr;
    }

    // EQUALITY -> COMPARISON EQUALITY_2
    //regresa una exprBinary pero con más elementos, pero ahora de comparación relacionados con el comparador igual
    private Expression EQUALITY() throws Exception {
        Expression expr = COMPARISON();
        return EQUALITY_2(expr);
    }

    // EQUALITY_2 -> != COMPARISON EQUALITY_2 | == COMPARISON EQUALITY_2 | Ɛ
    //agrega términos de comparación pero con asignación a las expresiones binarias que vienen
    private Expression EQUALITY_2(Expression expr) throws Exception {
        if(preanalisis.tipo == TipoToken.BANG_EQUAL)
        {
            match(TipoToken.BANG_EQUAL);
            Token operator  = previous();
            Expression right = COMPARISON();
            EQUALITY_2(new ExprBinary(expr,operator,right));
        }
        else if (preanalisis.tipo == TipoToken.EQUAL_EQUAL)
        {
            match(TipoToken.EQUAL_EQUAL);
            Token operator  = previous();
            Expression right = COMPARISON();
            EQUALITY_2(new ExprBinary(expr,operator,right));
        }
        return expr;
    }

    // COMPARISON -> TERM COMPARISON_2
    //regresa una exprBinary con más términos, pero de comparaación
    private Expression COMPARISON() throws Exception {
        Expression expr=TERM();
        return COMPARISON_2(expr);
    }

    // COMPARISON_2 -> > TERM COMPARISON_2 | >= TERM COMPARISON_2 | < TERM COMPARISON_2 | <= TERM COMPARISON_2 | Ɛ
    //agrega terminos pero de comparación a las expresiones binarias que ya se vienen concatenando (si es que lo hacen)
    private Expression COMPARISON_2(Expression expr) throws Exception {
        if(preanalisis.tipo == TipoToken.GREATER)
        {
            match(TipoToken.GREATER);
            Token operator = previous();
            Expression right = TERM();
            COMPARISON_2(new ExprBinary(expr,operator,right));
        }
        else if(preanalisis.tipo == TipoToken.GREATER_EQUAL)
        {
            match(TipoToken.GREATER_EQUAL);
            Token operator = previous();
            Expression right = TERM();
            COMPARISON_2(new ExprBinary(expr,operator,right));
        }
        else if(preanalisis.tipo == TipoToken.LESS)
        {
            match(TipoToken.LESS);
            Token operator = previous();
            Expression right = TERM();
            COMPARISON_2(new ExprBinary(expr,operator,right));
        }
        else if(preanalisis.tipo == TipoToken.LESS_EQUAL)
        {
            match(TipoToken.LESS_EQUAL);
            Token operator = previous();
            Expression right = TERM();
            COMPARISON_2(new ExprBinary(expr,operator,right));
        }
        return expr;
    }

    // TERM -> FACTOR TERM_2
    // regresa una exprBinary con más términos, pero ahora de suma y resta (o no añade nada y solo regresa la exprBinary que le llegó)
    private Expression TERM() throws Exception {
        Expression expr= FACTOR();
        return TERM_2(expr);
    }

    // TERM_2 -> - FACTOR TERM_2 | + FACTOR TERM_2 | Ɛ
    //esta es la parte que añade los terminos dependiendo si encuentra el - o el + (o no añade si no encuentra estos operadores)
    private Expression TERM_2(Expression expr) throws Exception {
        if(preanalisis.tipo == TipoToken.MINUS)
        {
            match(TipoToken.MINUS);
            Token operator = previous();
            Expression right = FACTOR();
            TERM_2(new ExprBinary(expr,operator,right));
        }
        else if(preanalisis.tipo == TipoToken.PLUS)
        {
            match(TipoToken.PLUS);
            Token operator = previous();
            Expression right = FACTOR();
            TERM_2(new ExprBinary(expr,operator,right));
        }
        return expr;
    }

    // FACTOR -> UNARY FACTOR_2
    //regresa ExpBinary, ExprUnary, ExpCallFunc o Expression simples
    private Expression FACTOR() throws Exception {
        Expression expr=UNARY();
        return FACTOR_2(expr);
    }

    // FACTOR_2 -> / UNARY FACTOR_2 | * UNARY FACTOR_2 | Ɛ
    //este es el que construye mi expresión binaria, además, puede agregar tantos factores como se necesite (incluye división y multiplicación)
    private Expression FACTOR_2(Expression expr) throws Exception {
        if(preanalisis.tipo == TipoToken.SLASH)
        {
            match(TipoToken.SLASH);
            Token operator=previous();
            Expression right=UNARY();
            Expression binExp=new ExprBinary(expr,operator,right);
            FACTOR_2(binExp);
        }
        else if(preanalisis.tipo == TipoToken.STAR)
        {
            match(TipoToken.STAR);
            Token operator=previous();
            Expression right=UNARY();
            Expression binExp=new ExprBinary(expr,operator,right);
            FACTOR_2(binExp);
        }

        return expr;


    }

    // UNARY -> ! UNARY | - UNARY | CALL
    //regresa ExprUnary, ExpCallFunc o Expression simples
    private Expression UNARY() throws Exception {

        if(preanalisis.tipo == TipoToken.BANG)
        {
            match(TipoToken.BANG);
            Token operador=previous();
            Expression expr=UNARY();
            return new ExprUnary(operador,expr);
        }
        else if (preanalisis.tipo == TipoToken.MINUS)
        {
            match(TipoToken.MINUS);
            Token operador = previous();
            Expression expr=UNARY();
            return new ExprUnary(operador,expr);
        }
        else if (preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING)
        {
            return CALL();
        }
        else
        {
            hayErrores = true;
            //System.out.println("errores");
            throw new Exception("errores");
        }
    }

    // CALL -> PRIMARY CALL_2
    //regresa un ExprCallFunc o Expression simple
    private Expression CALL() throws Exception {
        Expression expr=PRIMARY();
        return CALL_2(expr);//Aquí es como si le estuviera concatenando los paréntesis y argumentos a mi expression (que, si es semanticamente correcto, sera un identificador que corresponderá al nombre de la función a llamar)
    }

    // CALL_2 -> ( ARGUMENTS_OPC ) | Ɛ
    //regresa los argumentos de la llamada a funcion (si los hay), puede ser que no sea llamada a función, en cuyo caso esto no hace nada y regresa lo que se le pasó
    private Expression CALL_2(Expression expr) throws Exception {

        if(preanalisis.tipo == TipoToken.LEFT_PAREN)
        {
            match(TipoToken.LEFT_PAREN);
            List<Expression> lstArguments=ARGUMENTS_OPC();
            match(TipoToken.RIGHT_PAREN);
            ExprCallFunction ecf=new ExprCallFunction(expr,lstArguments);
            return ecf;
        }
        else
            return expr;//retorno la expresion sin hacerle nada

    }

    // PRIMARY -> true | false | null | number | string | id | ( EXPRESSION )
    //regresa Expression (simple/primitiva, es decir,literal, de agrupación o variable )
    private Expression PRIMARY() throws Exception {

        if (preanalisis.tipo == TipoToken.TRUE)
        {
            match(TipoToken.TRUE);
            return new ExprLiteral(true);
        }
        else if(preanalisis.tipo == TipoToken.FALSE)
        {
            match(TipoToken.FALSE);
            return new ExprLiteral(false);
        }
        else if(preanalisis.tipo == TipoToken.NULL)
        {
            match(TipoToken.NULL);
            return new ExprLiteral(null);
        }
        else if(preanalisis.tipo == TipoToken.NUMBER)
        {
            match(TipoToken.NUMBER);
            Token numero=previous();
            return new ExprLiteral(numero.literal);
        }
        else if(preanalisis.tipo == TipoToken.STRING)
        {
            match(TipoToken.STRING);
            Token cadena=previous();
            return new ExprLiteral(cadena.literal);//el cadena.literal es para obtener el valor numérico, si se ocupara .lexema (que es el valor del token), se obtendría el número, pero en cadena
        }
        else if(preanalisis.tipo == TipoToken.IDENTIFIER)
        {
            match(TipoToken.IDENTIFIER);
            Token id = previous();
            return new ExprVariable(id); //NO es id.lexema porque esta es una exprVariable y esta clase recibe un token, no un value (valor conreto, es deicr, literal), como la clase exprLiteral
        }
        else if(preanalisis.tipo == TipoToken.LEFT_PAREN) {
            match(TipoToken.LEFT_PAREN);
            Expression expr = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            return new ExprGrouping(expr);//Es una expresión de agrupación, y esta clase recibe una expression, por eso es que solo se le pasa expr (lo que va entre los paréntesis)
        }
        else
        {
            hayErrores = true;
            //System.out.println("errores");
            throw new Exception("error en Primary");
        }

    }

    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    public StmtFunction FUNCTION() throws Exception {

        match(TipoToken.IDENTIFIER);
        Token id = previous();
        match(TipoToken.LEFT_PAREN);
        List<Token> parametros = PARAMETERS_OPC();
        match(TipoToken.RIGHT_PAREN);
        StmtBlock body = BLOCK();

        return new StmtFunction(id, parametros, body);

    }

    // FUNCTIONS -> FUN_DECL FUNCTIONS | Ɛ
    public void FUNCTIONS() throws Exception {

        if(preanalisis.tipo == TipoToken.FUN)
        {
            FUN_DECL();
            FUNCTIONS();
        }
    }

    // PARAMETERS_OPC -> PARAMETERS | Ɛ
    public List<Token> PARAMETERS_OPC(){

        List<Token> parametros = new ArrayList<>();

        if(preanalisis.tipo == TipoToken.IDENTIFIER)
        {
            PARAMETERS(parametros);
        }
        return parametros;
    }

    // PARAMETERS -> id PARAMETERS_2
    public void PARAMETERS(List<Token> parametros){

        match(TipoToken.IDENTIFIER);
        Token t = previous();
        parametros.add(t);
        PARAMETERS_2(parametros);
    }

    // PARAMETERS_2 -> , id PARAMETERS_2 | Ɛ
    public void PARAMETERS_2(List<Token> parametros){

        if(preanalisis.tipo == TipoToken.COMMA)
        {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token t = previous();
            parametros.add(t);
            PARAMETERS_2(parametros);
        }

    }

    // ARGUMENTS_OPC -> EXPRESSION ARGUMENTS | Ɛ
    //regresa una lista con todos los argumentos de la llamada a función, generalmente estos serán Expressions simples
    private List<Expression> ARGUMENTS_OPC() throws Exception {

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            List<Expression> lstExpr= new ArrayList<>();
            lstExpr.add(EXPRESSION());
            ARGUMENTS(lstExpr);
            return lstExpr;
        }
        else
            return new ArrayList<>();
    }

    // ARGUMENTS -> , EXPRESSION ARGUMENTS | Ɛ
    //basicamente esto es para agregar más y más argumentos a la lista
    private List<Expression> ARGUMENTS(List<Expression> lstExpr) throws Exception {

        if(preanalisis.tipo == TipoToken.COMMA)
        {
            match(TipoToken.COMMA);
            lstExpr.add(EXPRESSION());
            ARGUMENTS(lstExpr);
        }

        return lstExpr;
    }


    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;

            throw new RuntimeException("Error encontrad");
            //System.out.println("Error encontrado");
        }

    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }


}
