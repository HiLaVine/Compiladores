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
        }
        return false;
    }

    // PROGRAM -> DECLARATION
    public void PROGRAM(){
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
            //FUNCTION();
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

        if (preanalisis.tipo == TipoToken.VAR)
        {
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            VAR_INIT();
            match(TipoToken.SEMICOLON);
        }
        else
        {
            hayErrores = true;
            System.out.println("Se esperaba 'VAR' ");
        }
    }

    // VAR_INIT -> = EXPRESSION | Ɛ
    public void VAR_INIT(){
        if (hayErrores)
            return;

        if (preanalisis.tipo == TipoToken.EQUAL)
        {
            match(TipoToken.EQUAL);
            //EXPRESSION();
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
           // IF_STMT();
        }
        else if(preanalisis.tipo == TipoToken.PRINT)
        {
           // PRINT_STMT();
        }
        else if(preanalisis.tipo == TipoToken.RETURN)
        {
           // RETURN_STMT();
        }
        else if(preanalisis.tipo == TipoToken.WHILE)
        {
           // WHILE_STMT();
        }
        else if(preanalisis.tipo == TipoToken.LEFT_BRACE)
        {
           // BLOCK();
        }

        //SE LLAMA ERROR??
    }

    // EXPR_STMT -> EXPRESSION ;
    public void EXPR_STMT(){
        if (hayErrores)
            return;

       // EXPRESSION();
        if (preanalisis.tipo == TipoToken.SEMICOLON)
            match(TipoToken.SEMICOLON);
        else
        {
            hayErrores = true;
            System.out.println("Se esperaba ';' "); //Correcto??
        }
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
        //Como se manejan los errores
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
            System.out.println("Se esperaba ';' "); //Correcto??
        }
    }

    // FOR_STMT_2 -> EXPRESSION ; | ;
    public void FOR_STMT_2(){
        if (hayErrores)
            return;

        if (preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.MINUS)
        {
            //EXPRESSION();
            match(TipoToken.SEMICOLON);
        }
        else if(preanalisis.tipo == TipoToken.SEMICOLON)
        {
            match(TipoToken.SEMICOLON);
        }
    }

    public void FOR_STMT_3(){
        if (hayErrores)
            return;
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
