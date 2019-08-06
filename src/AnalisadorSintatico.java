import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;


public class AnalisadorSintatico {
    private static ArrayList<String> PrimeiroDefInicio = new ArrayList<>();
    private static ArrayList<String> PrimeiroDefGlobal = new ArrayList<>();
    private static ArrayList<String> PrimeiroDefConstante = new ArrayList<>();
    private static ArrayList<String> PrimeiroDefPrincipal = new ArrayList<>();
    private static ArrayList<String> PrimeiroDefGlobal2 = new ArrayList<>();
    private static ArrayList<String> PrimeiroDefMetodo = new ArrayList<>();
    private static ArrayList<String> PrimeiroConstante = new ArrayList<>();
    private static ArrayList<String> PrimeiroListaConst = new ArrayList<>();
    private static ArrayList<String> PrimeiroTipoId = new ArrayList<>();
    private static ArrayList<String[]> listaTokens = new ArrayList<>();
    private static String token;
    private static int tokenAtual = 0, tokenAnterior = 0, numeroArquivo = 0;

    public static void regras() {
        PrimeiroDefInicio.add("programa");

        PrimeiroDefGlobal.add("constantes");

        PrimeiroDefConstante.add("constantes");

        PrimeiroDefPrincipal.add("principal");

        PrimeiroDefGlobal2.add("metodo");

        PrimeiroDefMetodo.add("metodo");

        PrimeiroConstante.add("inteiro");
        PrimeiroConstante.add("real");
        PrimeiroConstante.add("texto");
        PrimeiroConstante.add("boleano");

        PrimeiroListaConst.add("inteiro");
        PrimeiroListaConst.add("real");
        PrimeiroListaConst.add("texto");
        PrimeiroListaConst.add("boleano");

        PrimeiroTipoId.add("inteiro");
        PrimeiroTipoId.add("real");
        PrimeiroTipoId.add("texto");
        PrimeiroTipoId.add("boleano");
    }

    public static void main(String[] args) {
        System.out.println("\n -- ANALISADOR SINTÁTICO -- ");
        regras();
        lerArquivos();
    }

    public static void lerArquivos() {
        File arquivos[], diretorio = new File("teste/");
        arquivos = diretorio.listFiles();

        for (int i = 0; i < arquivos.length; i++){

            if (arquivos[i].toString().contains("saida")) {

                try {
                    numeroArquivo += 1;
                    lerArquivo(arquivos[i]);
                    limpaEstruturas();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void lerArquivo(File nomeArquivo) throws IOException {
        String linha;
        BufferedReader buffer = new BufferedReader(new FileReader(nomeArquivo));

        //Lê todas as linhas do arquivo até o final:
        while ((linha = buffer.readLine()) != null) {

            if (linha.contains("<")) {
                String[] linhaToken = linha.replace("<", "").replace(">", "").replace(" ", "").split(",");
                listaTokens.add(linhaToken);
            }
        }
        buffer.close();

        proximoToken();
        Inicio();

        if (token.equals("$")) {
            System.out.println("Sucesso na análise sintática do " + numeroArquivo + "º arquivo!");
        } else {
            System.out.println("Erro na análise sintática do " + numeroArquivo + "º arquivo!");
        }
    }

    public static void proximoToken() {
        token = (listaTokens.get(tokenAtual))[1];
        System.out.println(tokenAtual);
        System.out.println(token);
        tokenAnterior = tokenAtual;
        tokenAtual++;
    }

    public static void Inicio() {

        if (token.equals("programa")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                DefGlobal();

                if (token.equals("}")) {
                    proximoToken();
                }
            }
        } else {
            System.out.println("Erro no início do programa.");
        }
    }

    public static void DefGlobal() {

        if (PrimeiroDefConstante.contains(token)) {
            DefConstante();
            DefPrincipal();
            DefGlobal2();
        }
    }

    public static void DefGlobal2() {

        if (PrimeiroDefMetodo.contains(token)) {
            DefMetodo();
            DefGlobal2();
        }
    }

    public static void DefConstante() {

        if (token.equals("constantes")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                ListaConst();

                if (token.equals("}")) {
                    proximoToken();
                }
            }
        }
    }

    public static void DefPrincipal() {

        if (token.equals("metodo")) {
            proximoToken();

            if (token.equals("principal")) {
                proximoToken();

                if (token.equals("(")) {
                    proximoToken();

                    if (token.equals(")")) {
                        proximoToken();

                        if (token.equals(":")) {
                            proximoToken();
                            Tipo();

                            if (token.equals("{")) {
                                proximoToken();
                                Declaracao();

                                if (token.equals("}")) {
                                    proximoToken();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void DefMetodo() {

        if (token.equals("metodo")) {
            proximoToken();

            if (listaTokens.get(tokenAnterior)[0].equals("IDENTIFICADOR")) {
                proximoToken();

                if (token.equals("(")) {
                    proximoToken();

                    ListaParam();

                    if (token.equals(")")) {
                        proximoToken();

                        if (token.equals(":")) {
                            proximoToken();
                            Tipo();

                            if (token.equals("{")) {
                                proximoToken();
                                Declaracao();

                                if (token.equals("}")) {
                                    proximoToken();
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static void ListaConst() {

        if (PrimeiroConstante.contains(token)) {
            Constante();

            if (token.equals(";")) {
                proximoToken();
                ListaConst2();
            }
        }
    }

    public static void ListaConst2() {

        if (PrimeiroListaConst.contains(token)) {
            ListaConst();
        }
    }

    public static void Constante() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();
            AtribuicaoConst();
            ListaAtribuicaoConst();
        }
    }

    public static void ListaAtribuicaoConst() {

        if (token.equals(",")) {
            proximoToken();
            AtribuicaoConst();
        }
    }

    public static void AtribuicaoConst() {

        if (listaTokens.get(tokenAnterior)[0].equals("IDENTIFICADOR")) {
            proximoToken();

            if (token.equals("=")) {
                proximoToken();
                Valor();
            }
        }
    }

    public static void Tipo() {

        if ((token.equals("vazio"))) {
            proximoToken();
        } else {
            TipoId();
        }
    }

    public static void TipoId() {

        if ((token.equals("inteiro")) || (token.equals("real")) || (token.equals("texto")) || (token.equals("boleano"))) {
            proximoToken();
        }
    }

    public static void Valor() {

        if ((listaTokens.get(tokenAnterior)[0].equals("NUMERO")) || (listaTokens.get(tokenAnterior)[0].equals("CADEIA_CARACTERES"))) {
            proximoToken();
        } else if (listaTokens.get(tokenAnterior)[0].equals("IDENTIFICADOR")) {
            proximoToken();
        }
    }


    public static void ListaParam() {
        TipoId();
        if (listaTokens.get(tokenAnterior)[0].equals("IDENTIFICADOR")) {
            proximoToken();
            ListaParam2();
        }
    }

    //CORRIGIR
    public static void ListaParam2() {

        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS
        //CORRIGIR ESSE '*' ABAIXO, COLOQUEI ELE AÍ ENQUANTO RESOLVIA O PROBLEMA DAS VÍRGULAS NOS TOKENS

        if (token.equals("*")) {
            proximoToken();
            TipoId();

            if (listaTokens.get(tokenAnterior)[0].equals("IDENTIFICADOR")) {
                proximoToken();
                ListaParam2();
            }
        }
    }



    public static void Declaracao() {
        DefVariavel();
    }

    public static void DefVariavel() {

        if (token.equals("variaveis")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                //Lista...

                if (token.equals("}")) {
                    proximoToken();
                }
            }
        }
    }

    public static void limpaEstruturas() {
        listaTokens.clear();
        tokenAnterior = 0;
        tokenAtual = 0;
    }
}