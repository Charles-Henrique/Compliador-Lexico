package compilador;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Charles e Jakeline
 */
public class AnalisadorLexico {

    private static AnalisadorLexico instancia;

    //Arrays armazenam os números da Linguagem
    private final String[] num = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final ArrayList<String> numeros = new ArrayList<String>(Arrays.asList(num));

    //Arrays que armazenam as letras da Linguagem
    private final String[] alpha = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private final ArrayList<String> letras = new ArrayList<>(Arrays.asList(alpha));

    //Arrays que armazenam os operadores / sinais da Linguagem
    private final String[] operadores = {",", ";", "(", ")", "{", "}", "[", "]", "+",
        "-", "*", "/", "=", "<", ">", "!", "&", "|"};
    private final ArrayList<String> sinais = new ArrayList<>(Arrays.asList(operadores));

    //Array que armazenam as palavras reservadas da Linguagem
    private final String[] palavras = {"int", "void", "float", "char", "return",
        "if", "while"};
    private final ArrayList<String> palavrasReservadas
            = new ArrayList<>(Arrays.asList(palavras));

    //Array que armazena os tokens da analise
    private ArrayList<String> tokens;
    private String token;

    //Retorna caso a analise tenha sido bem sucedida
    private boolean sucesso;

    //Array de Strings que armazena os simbolos da analise
    private String[] simbolos;

    //Se a analise encontrar erros, armazenará o simbolo invalido
    private String simboloInvalido;

    private AnalisadorLexico() {
        this.tokens = new ArrayList<>();
        this.sucesso = false;
        this.token = "";
    }

    public static AnalisadorLexico getInstance() {
        if (instancia == null) {
            instancia = new AnalisadorLexico();
        }
        return instancia;
    }

    // Adiciona o Token encontrado ao array de tokens, junto com seu tipo
    //É a impressão do Token e de seu tipo
    public void adicionarToken(String tipo) {
        tokens.add(token + " - " + tipo);
        limparToken();
        sucesso = true;
    }

    // Recebe o que for digitado e inicia a analise
    public boolean analisar(String texto) {
        texto = texto.replaceAll("\n", "");
        simbolos = texto.split(""); // Divide o texto digitado (Objeto String) em um array de Strings

        for (int i = 0; i < simbolos.length; i++) {

            if (isLetra(simbolos[i])) {
                token += simbolos[i];
                analisarPalavra(i);
            } else if (isSinal(simbolos[i])) { // == 

                if (simbolos[i].equals("&") || simbolos[i].equals("|")) {
                    if (isOperadorDuplo(i)) {
                        token += simbolos[i] + simbolos[i + 1]; // ex && - || 
                        adicionarToken("Operador");
                        i++;
                    } else {
                        simboloInvalido = simbolos[i];
                        return false;
                    }
                } // Analisa o ultimo simbolo do array
                else if ((i == simbolos.length - 1)) {
                    token += simbolos[i];
                    adicionarToken("Operador");
                } //Verifica se é um operador duplo
                else if ((simbolos[i].equals("<") || simbolos[i].equals(">")
                        || simbolos[i].equals("=")) && simbolos[i + 1].equals("=")) {
                    token += simbolos[i] + simbolos[i + 1]; //um unico simbolo ex '=='
                    adicionarToken("Operador");
                    i++;
                } else {
                    token += simbolos[i]; // ex '='
                    adicionarToken("Operador");
                }
            } //verifica se é numerico
            else if (isNumero(simbolos[i])) {
                analisarNumeros(i);
            } //verifica se é espaço em branco
            else {
                if (isEspaco(simbolos[i])) {
                    limparToken();
                    sucesso = true;
                } else { //verifica se é simbolo invalido
                    limparToken();
                    simboloInvalido = simbolos[i];
                    return false;
                }
            }
        }

        return sucesso;
    }

    public boolean isOperadorDuplo(int index) {

        if ((index == simbolos.length - 1)) {
            token += simbolos[index];
            return false;
        } else if (((simbolos[index].equals("&")) && (simbolos[index + 1].equals("&")))
                || (simbolos[index].equals("|") && (simbolos[index + 1]).equals("|"))) {
            return true;
        } else {
            return (simbolos[index].equals("<") || simbolos[index].equals(">")
                    || simbolos[index].equals("=")) && (simbolos[index + 1].equals("="));
        }
    }

    public void analisarNumeros(int index) {
        token += simbolos[index];

        //verifica se o token não esta vazio e se não comeca com uma letra
        if (!(token.isEmpty()) && isLetra(String.valueOf(token.charAt(0)))) {
            analisarPalavra(index); // analisa se é palavra reservada
        } else if (index == simbolos.length - 1) {
            adicionarToken("Num");
        } else if ((isEspaco(simbolos[index + 1])) //se contem espaco no proximo indice
                || (isSinal(simbolos[index + 1])) || isLetra(simbolos[index + 1])) { //se contem sinal ou letra no proximo indice
            adicionarToken("Num");

        }

    }

    public String getSimboloInvalido() {
        return simboloInvalido;
    }

    public ArrayList<String> getTokens() {
        return this.tokens;
    }

    public void analisarPalavra(int index) {

        //compara se o indice atual é uma letra ou numero
        if ((isLetra(simbolos[index])) || (isNumero(simbolos[index]))) {
            // se for palavra reservada -> classifica como "Palavra reservada"
            if (isPalavraReservada(token)) {

                if (index == simbolos.length - 1) {
                    adicionarToken("Palavra Reservada");
                } else if ((isEspaco(simbolos[index + 1]))
                        || (isSinal(simbolos[index + 1]))) {
                    adicionarToken("Palavra Reservada");
                }
                // SE for o ultimo indice da analise, imprime "ID"
                
                //senao -> classifica como ID
            } else if (index == simbolos.length - 1) {
                adicionarToken("ID");
            } else if ((isEspaco(simbolos[index + 1])) || (isSinal(simbolos[index + 1]))) {
                adicionarToken("ID");
            }
        }

    }//fim do método

    public boolean isEspaco(String valor) {
        return (valor.equals(" ")) || (valor.equals(""));
    }

    public void limparToken() {
        this.token = "";
    }

    public void limparListaTokens() {
        tokens.clear();
    }

    public boolean isPalavraReservada(String valor) {
        return (palavrasReservadas.contains(valor));
    }

    public boolean isSinal(String valor) {
        return (sinais.contains(valor));
    }

    public boolean isLetra(String valor) {
        return (letras.contains(valor));
    }

    public boolean isNumero(String valor) {
        return (numeros.contains(valor));
    }
}
