import java.util.List;

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
        PROGRAM();
        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("correcto");
            return  true;
        }else {
            System.out.println("Se encontraron errores");
            return false;
        }

    }

    // PROGRAM -> DECLARATION
    public void PROGRAM(){
        System.out.println("hola");
        DECLARATION();
    }

    // DECLARATION -> FUN_DECL DECLARATION | VAR_DECL DECLARATION | STATEMENT DECLARATION | Ɛ
    public void DECLARATION(){

        if(preanalisis.tipo == TipoToken.FUN)
        {
            FUN_DECL();
            DECLARATION();
        }

        else if (preanalisis.tipo == TipoToken.VAR)
        {
            VAR_DECL();
            DECLARATION();
        }

        else if (preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL ||
                preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE ||
                preanalisis.tipo == TipoToken.LEFT_BRACE)
        {
            STATEMENT();
            DECLARATION();
        }
    }

    //FUN_DECL -> fun FUNCTION
    public void FUN_DECL(){
        if(hayErrores)
            return;

        if (preanalisis.tipo == TipoToken.FUN)
        {
            match(TipoToken.FUN);
            FUNCTION();
        }
        else
        {
            hayErrores = true;
            System.out.println("Se esperaba 'FUN'");
        }

    }

    // VAR_DECL -> var id VAR_INIT ;
    public void VAR_DECL() {
        if (hayErrores)
            return;

        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        VAR_INIT();
        match(TipoToken.SEMICOLON);

    }

    // VAR_INIT -> = EXPRESSION | Ɛ
    public void VAR_INIT(){
        if (hayErrores)
            return;

        if (preanalisis.tipo == TipoToken.EQUAL)
        {
            match(TipoToken.EQUAL);
            EXPRESSION();
        }

    }

    // STATEMENT -> EXPR_STMT | FOR_STMT | IF_STMT | PRINT_STMT | RETURN_STMT | WHILE_STMT | BLOCK
    public void STATEMENT(){
        if (hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN || preanalisis.tipo == TipoToken.TRUE ||
                preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL ||
                preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING ||
                preanalisis.tipo == TipoToken.MINUS)
        {
            EXPR_STMT();
        }
        else if(preanalisis.tipo == TipoToken.FOR)
        {
            FOR_STMT();
        }
        else if (preanalisis.tipo == TipoToken.IF)
        {
           IF_STMT();
        }
        else if(preanalisis.tipo == TipoToken.PRINT)
        {
           PRINT_STMT();
        }
        else if(preanalisis.tipo == TipoToken.RETURN)
        {
           RETURN_STMT();
        }
        else if(preanalisis.tipo == TipoToken.WHILE)
        {
           WHILE_STMT();
        }
        else if(preanalisis.tipo == TipoToken.LEFT_BRACE)
        {
           BLOCK();
        }
        else
        {
            hayErrores = true;
            System.out.println("Error");
        }
    }

    // EXPR_STMT -> EXPRESSION ;
    public void EXPR_STMT(){
        if (hayErrores)
            return;

        EXPRESSION();
        match(TipoToken.SEMICOLON);

    }

    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    public void FOR_STMT(){
        if (hayErrores)
            return;

        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        FOR_STMT_1();
        FOR_STMT_2();
        FOR_STMT_3();
        match(TipoToken.RIGHT_PAREN);
        STATEMENT();

    }

    // FOR_STMT_1 -> VAR_DECL | EXPR_STMT | ;
    public void FOR_STMT_1(){
        if (hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN)
        {
            VAR_DECL();
        }
        else if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS)
        {
            EXPR_STMT();
        }
        else if (preanalisis.tipo == TipoToken.SEMICOLON)
        {
            match(TipoToken.SEMICOLON);
        }
        else
        {
            hayErrores = true;
            System.out.println("Se esperaba ';' ");
        }
    }

    // FOR_STMT_2 -> EXPRESSION ; | ;
    public void FOR_STMT_2(){
        if (hayErrores)
            return;

        if (preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            EXPRESSION();
            match(TipoToken.SEMICOLON);
        }
        else if(preanalisis.tipo == TipoToken.SEMICOLON)
        {
            match(TipoToken.SEMICOLON);
        }
        else
        {
            hayErrores = true;
            System.out.println("error");
        }
    }

    // FOR_STMT_3 -> EXPRESSION | Ɛ
    public void FOR_STMT_3(){
        if (hayErrores)
            return;
        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS||
                preanalisis.tipo == TipoToken.BANG)
        {
            EXPRESSION();
        }

    }

    // IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    public void IF_STMT(){
        if(hayErrores)
            return;

        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        EXPRESSION();
        STATEMENT();
        ELSE_STATEMENT();
    }

    // ELSE_STATEMENT -> else STATEMENT | Ɛ
    public void ELSE_STATEMENT(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.ELSE)
        {
            match(TipoToken.ELSE);
            STATEMENT();
        }
    }

    // PRINT_STMT -> print EXPRESSION ;
    public void PRINT_STMT(){
        if(hayErrores)
            return;

        match(TipoToken.PRINT);
        EXPRESSION();
        match(TipoToken.SEMICOLON);


    }

    // RETURN_STMT -> return RETURN_EXP_OPC ;
    public void RETURN_STMT(){
        if(hayErrores)
            return;

        match(TipoToken.RETURN);
        RETURN_EXP_OPC();
        match(TipoToken.SEMICOLON);
    }

    // RETURN_EXP_OPC -> EXPRESSION | Ɛ
    public void RETURN_EXP_OPC(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            EXPRESSION();
        }
    }

    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    public void WHILE_STMT(){
        if(hayErrores)
            return;

        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        EXPRESSION();
        match(TipoToken.RIGHT_PAREN);
        STATEMENT();

    }

    // BLOCK -> { DECLARATION }
    public void BLOCK(){
        if(hayErrores)
            return;

        match(TipoToken.LEFT_BRACE);
        DECLARATION();
        match(TipoToken.RIGHT_BRACE);
        //error??
    }

    // EXPRESSION -> ASSIGNMENT
    public void EXPRESSION(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            ASSIGNMENT();
        }
        else
        {
            hayErrores = true;
            System.out.println("errror");
        }
    }

    // ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    public void ASSIGNMENT(){
        if(hayErrores)
            return;
        LOGIC_OR();
        ASSIGNMENT_OPC();
    }

    // ASSIGNMENT_OPC -> = EXPRESSION | Ɛ
    public void ASSIGNMENT_OPC(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            EXPRESSION();
        }
    }

    // LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    public void LOGIC_OR(){
        if(hayErrores)
            return;

        LOGIC_AND();
        LOGIC_OR_2();
    }

    // LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2 | Ɛ
    public void LOGIC_OR_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.OR)
        {
            match(TipoToken.OR);
            LOGIC_AND();
            LOGIC_OR_2();
        }
        else
        {
            hayErrores = true;
            System.out.println("error");
        }
    }

    // LOGIC_AND -> EQUALITY LOGIC_AND_2
    public void LOGIC_AND(){
        if(hayErrores)
            return;

        EQUALITY();
        LOGIC_AND_2();
    }

    // LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2 | Ɛ
    public void LOGIC_AND_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.AND)
        {
            match(TipoToken.AND);
            EQUALITY();
            LOGIC_AND_2();
        }
        else
        {
            hayErrores = true;
            System.out.println("error");
        }
    }

    // EQUALITY -> COMPARISON EQUALITY_2
    public void EQUALITY(){
        if(hayErrores)
            return;

        COMPARISON();
        EQUALITY_2();
    }

    // EQUALITY_2 -> != COMPARISON EQUALITY_2 | == COMPARISON EQUALITY_2 | Ɛ
    public void EQUALITY_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG_EQUAL)
        {
            match(TipoToken.BANG_EQUAL);
            COMPARISON();
            EQUALITY_2();
        }
        else if (preanalisis.tipo == TipoToken.EQUAL_EQUAL)
        {
            match(TipoToken.EQUAL_EQUAL);
            COMPARISON();
            EQUALITY_2();
        }
    }

    // COMPARISON -> TERM COMPARISON_2
    public void COMPARISON(){
        if(hayErrores)
            return;

        TERM();
        COMPARISON_2();
    }

    // COMPARISON_2 -> > TERM COMPARISON_2 | >= TERM COMPARISON_2 | < TERM COMPARISON_2 | <= TERM COMPARISON_2 | Ɛ
    public void COMPARISON_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.GREATER)
        {
            match(TipoToken.GREATER);
            TERM();
            COMPARISON_2();
        }
        else if(preanalisis.tipo == TipoToken.GREATER_EQUAL)
        {
            match(TipoToken.GREATER_EQUAL);
            TERM();
            COMPARISON_2();
        }
        else if(preanalisis.tipo == TipoToken.LESS)
        {
            match(TipoToken.LESS);
            TERM();
            COMPARISON_2();
        }
        else if(preanalisis.tipo == TipoToken.LESS_EQUAL)
        {
            match(TipoToken.LESS_EQUAL);
            TERM();
            COMPARISON_2();
        }

    }

    // TERM -> FACTOR TERM_2
    public void TERM(){
        if(hayErrores)
            return;

        FACTOR();
        TERM_2();
    }

    // TERM_2 -> - FACTOR TERM_2 | + FACTOR TERM_2 | Ɛ
    public void TERM_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.MINUS)
        {
            match(TipoToken.MINUS);
            FACTOR();
            TERM_2();
        }
        else if(preanalisis.tipo == TipoToken.PLUS)
        {
            match(TipoToken.PLUS);
            FACTOR();
            TERM_2();
        }

    }

    // FACTOR -> UNARY FACTOR_2
    public void FACTOR(){
        if(hayErrores)
            return;

        UNARY();
        FACTOR_2();
    }

    // FACTOR_2 -> / UNARY FACTOR_2 | * UNARY FACTOR_2 | Ɛ
    public void FACTOR_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.SLASH)
        {
            match(TipoToken.SLASH);
            UNARY();
            FACTOR_2();
        }
        else if(preanalisis.tipo == TipoToken.STAR)
        {
            match(TipoToken.STAR);
            UNARY();
            FACTOR_2();
        }

    }

    // UNARY -> ! UNARY | - UNARY | CALL
    public void UNARY(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG)
        {
            match(TipoToken.BANG);
            UNARY();
        }
        else if (preanalisis.tipo == TipoToken.MINUS)
        {
            match(TipoToken.MINUS);
            UNARY();
        }
        else if (preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING)
        {
            CALL();
        }
        else
        {
            hayErrores = true;
            System.out.println("errores");
        }
    }

    // CALL -> PRIMARY CALL_2
    public void CALL(){
        if(hayErrores)
            return;

        PRIMARY();
        CALL_2();
    }

    // CALL_2 -> ( ARGUMENTS_OPC ) CALL_2 | Ɛ
    public void CALL_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.LEFT_PAREN)
        {
            match(TipoToken.LEFT_PAREN);
            ASSIGNMENT_OPC();
            match(TipoToken.RIGHT_PAREN);
            CALL_2();
        }

    }

    // PRIMARY -> true | false | null | number | string | id | ( EXPRESSION )
    public void PRIMARY(){
        if(hayErrores)
            return;

        if (preanalisis.tipo == TipoToken.TRUE)
        {
            match(TipoToken.TRUE);
        }
        else if(preanalisis.tipo == TipoToken.FALSE)
        {
            match(TipoToken.FALSE);
        }
        else if(preanalisis.tipo == TipoToken.NULL)
        {
            match(TipoToken.NULL);
        }
        else if(preanalisis.tipo == TipoToken.NUMBER)
        {
            match(TipoToken.NUMBER);
        }
        else if(preanalisis.tipo == TipoToken.STRING)
        {
            match(TipoToken.STRING);
        }
        else if(preanalisis.tipo == TipoToken.IDENTIFIER)
        {
            match(TipoToken.IDENTIFIER);
        }
        else if(preanalisis.tipo == TipoToken.LEFT_PAREN)
        {
            match(TipoToken.LEFT_PAREN);
            EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
        }
        else
        {
            hayErrores = true;
            System.out.println("errores");
        }

    }

    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    public void FUNCTION(){
        if(hayErrores)
            return;

        match(TipoToken.IDENTIFIER);
        match(TipoToken.LEFT_PAREN);
        PARAMETERS_OPC();
        match(TipoToken.RIGHT_PAREN);
        BLOCK();

    }

    // FUNCTIONS -> FUN_DECL FUNCTIONS | Ɛ
    public void FUNCTIONS(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN)
        {
            FUN_DECL();
            FUNCTIONS();
        }
    }

    // PARAMETERS_OPC -> PARAMETERS | Ɛ
    public void PARAMETERS_OPC(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER)
        {
            PARAMETERS();
        }
    }

    // PARAMETERS -> id PARAMETERS_2
    public void PARAMETERS(){
        if(hayErrores)
            return;

        match(TipoToken.IDENTIFIER);
        PARAMETERS_2();
    }

    // PARAMETERS_2 -> , id PARAMETERS_2 | Ɛ
    public void PARAMETERS_2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.COMMA)
        {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            PARAMETERS_2();
        }

    }

    // ARGUMENTS_OPC -> EXPRESSION ARGUMENTS | Ɛ
    public void ARGUMENTS_OPC(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.BANG)
        {
            EXPRESSION();
            ARGUMENTS();
        }

    }

    // ARGUMENTS -> , EXPRESSION ARGUMENTS | Ɛ
    public void ARGUMENTS(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.COMMA)
        {
            match(TipoToken.COMMA);
            EXPRESSION();
            ARGUMENTS();
        }

    }


    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error encontrado");
        }

    }


}
