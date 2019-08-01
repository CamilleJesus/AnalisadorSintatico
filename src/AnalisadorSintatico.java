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
    private static int numeroToken = 0;

    public AnalisadorSintatico() {
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
        lerArquivos();
    }

    public static void lerArquivos() {
        File arquivos[], diretorio = new File("teste/");
        arquivos = diretorio.listFiles();
        int numeroArquivo = 0;

        for (int i = 0; i < arquivos.length; i++){

            if (arquivos[i].toString().contains("saida")) {
                System.out.println(arquivos[i]);

                try {
                    numeroArquivo += 1;
                    System.out.println("\nIniciando a análise sintática do " + numeroArquivo + "º arquivo de entrada.");
                    lerArquivo(arquivos[i]);
                    System.out.println("\nFinalizando a análise sintática do " + numeroArquivo + "º arquivo de entrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void lerArquivo(File nomeArquivo) throws IOException {
        String linha;
        Scanner in = new Scanner(System.in);
        BufferedReader buffer = new BufferedReader(new FileReader(nomeArquivo));

        //Lê todas as linhas do arquivo até o final:
        while ((linha = buffer.readLine()) != null) {

            if (linha.contains("<")) {
                String[] linhaToken = linha.replace("<", "").replace(">", "").replace(" ", "").split(",");
                //System.out.println(Arrays.toString(linhaToken));
                listaTokens.add(linhaToken);
            }
        }
        buffer.close();

        proximoToken();
        Inicio();

        if (token.equals("$")) {
            System.out.println("Sucesso!");
        } else {
            System.out.println("Erro!");
        }
    }

    public static void proximoToken() {
        System.out.println(numeroToken);
        token = (listaTokens.get(numeroToken))[1];
        numeroToken++;
        System.out.println(token);
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
        }
    }

    public static void DefGlobal() {
        //System.out.println(token);
        //System.out.println(numeroToken);

        DefConstante();

        if (PrimeiroDefConstante.contains(token)) {
            //DefPrincipal();
            //DefGlobal2();
        }
    }

    public static void DefGlobal2() {

        if (PrimeiroDefMetodo.contains(token)) {
            DefConstante();
            DefPrincipal();
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

    }

    public static void DefMetodo() {

    }

    public static void ListaConst() {

        //Constante();

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

        if (listaTokens.get(numeroToken)[0].equals("IDENTIFICADOR")) {
            proximoToken();

            if (token.equals("=")) {
                Valor();
            }
        }
    }

    public static void TipoId() {

        if ((token.equals("inteiro")) || (token.equals("real")) || (token.equals("texto")) || (token.equals("boleano"))) {
            proximoToken();
        }
    }

    public static void Valor() {

        if ((listaTokens.get(numeroToken)[0].equals("NUMERO")) || (listaTokens.get(numeroToken)[0].equals("CADEIA_CARACTERES"))) {
            proximoToken();
        } else if (listaTokens.get(numeroToken)[0].equals("IDENTIFICADOR")) {
            proximoToken();
        }
    }
}
