package chamadas;

import compilador.AnalisadorLexico;
import java.util.ArrayList;


public class Funcao {
    
    private final AnalisadorLexico lexico = AnalisadorLexico.getInstance();
    
    public boolean analisarLexicamente(String texto){
        return lexico.analisar(texto);
    }
    
    public ArrayList<String> getTokens(){
        return lexico.getTokens();
    }
    
    public void limparListaTokens(){
        lexico.limparListaTokens();
    }
    
    public String getSimboloInvalido(){
        return lexico.getSimboloInvalido();
    }
}
