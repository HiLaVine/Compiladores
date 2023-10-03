import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Identificadores, palabras reservadas y comentarios: Emmanuel
* Numero y cadenas: Reny
* Operadores: Angel*/

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        int estado = 0;       // Estado actual de la máquina de estados.
        String lexema = "";   // Acumulador para construir los lexemas.
        char c;               // Carácter actual.

        // Bucle para recorrer cada carácter en el código fuente.
        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            // Máquina de estados para el análisis léxico.
            switch (estado){
                // Estado inicial
                case 0:
                    if(Character.isLetter(c)){
                        estado = 13;
                        lexema += c;
                    }

                    else if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;

                        /*while(Character.isDigit(c)){
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }
                        Token t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        lexema = "";
                        estado = 0;
                        tokens.add(t);
                        */

                    }
                    else if(c == '/'){ //Estado donde revisa si tenemos un slash o un comentario.
                        estado = 26;
                    }
                    break;

                case 13:
                    if(Character.isLetterOrDigit(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else{
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            Token t = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t);
                        }
                        else{
                            Token t = new Token(tt, lexema);
                            tokens.add(t);
                        }

                        estado = 0;
                        lexema = "";
                        i--;

                    }
                    break;

                case 15:
                    if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                    else if(c == '.'){

                    }
                    else if(c == 'E'){

                    }
                    else{
                        Token t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;

                case 26:
                    if (c == '/') { //Si recibe otro '/' va al estado de comentario de una sola linea
                        estado = 30;
                    }

                    else if (c == '*'){//Si recibe un '*' va al estado de comentario multilinea
                        estado = 27;
                    }

                    else { // Si no recibe ninguno de los anteriores lo dejamos como slash.
                        Token t = new Token(TipoToken.SLASH, lexema);
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;

                case 27: //comentario multilinea
                    if (c == '*'){ //si recibe otro '*' vamos al estado 28
                        estado = 28;
                    }
                    else { // Si no nos quedamos en este estado hasta que reciba '*'
                        estado = 27;
                    }

                    break;

                case 28: //Comentario multilinea
                    if (c == '/'){ //Si despues de recibir '*' recibe un '/'. se cierra el comentario y vamos al estado de aceptacion 29;
                        estado = 29;
                    }
                    else { //Si no recibe '/' vuelve al estado 27.
                        estado = 27;
                    }

                    break;

                case 29: //Estado de aceptacion comentario multilinea
                    estado = 0; //No genera token y vamos a el estado 0 para seguir analizando.
                break;

                case 30: // Comentario de una sola linea
                    if(c=='\n') //Si recibe un salto de linea ya no
                    {
                        estado = 31;
                    }
                    else //Si no recibe el segundo '\n' se queda en el estado 30 hasta que lo reciba.
                    {
                        estado = 30;
                    }

                    break;

                case 31: //Estado de aceptacion del comentario de una linea.
                    estado = 0;
                break;
            }


        }


        return tokens;
    }
}
