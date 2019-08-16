/*
Autores: Camille Jesús e Reinildo Souza
Componente Curricular: EXA869 - MI Processadores de Linguagem de Programação (P03)
Data: 18/08/2019
*/
package model;

import token.Token;

import java.util.ArrayList;


public class AnalisadorSintatico {
    private ArrayList<String> PrimeiroDefInicio = new ArrayList<>();
    private ArrayList<String> PrimeiroDefGlobal = new ArrayList<>();
    private ArrayList<String> PrimeiroDefConstante = new ArrayList<>();
    private ArrayList<String> PrimeiroDefPrincipal = new ArrayList<>();
    private ArrayList<String> PrimeiroDefGlobal2 = new ArrayList<>();
    private ArrayList<String> PrimeiroDefMetodo = new ArrayList<>();
    private ArrayList<String> PrimeiroConstante = new ArrayList<>();
    private ArrayList<String> PrimeiroListaConst = new ArrayList<>();
    private ArrayList<String> PrimeiroTipoId = new ArrayList<>();
    private ArrayList<Token> listaTokens = new ArrayList<>();
    private String token;
    private int tokenAtual = 0, tokenAnterior = 0, numeroArquivo = 0;
    private ArrayList<ArrayList<Token>> listasTokens = new ArrayList<>();

    public void setListaTokens(ArrayList<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public void setListasTokens(ArrayList<ArrayList<Token>> listaListasTokens) {
        this.listasTokens = listaListasTokens;
    }

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

    public void mainSintatico() {

        for (int i = 0; i < listasTokens.size(); i++) {
            numeroArquivo = i + 1;
            setListaTokens(listasTokens.get(i));
            procedimentosGramatica();
            limparEstruturas();
        }
    }

    public void procedimentosGramatica() {
        proximoToken();
        Inicio();

        if (token.equals("$")) {
            System.out.println("Sucesso na análise sintática do " + numeroArquivo + "º arquivo!");
        } else {
            System.out.println("Erro na análise sintática do " + numeroArquivo + "º arquivo!");
        }
    }

    public void proximoToken() {
        token = (listaTokens.get(tokenAtual)).getLexema();
        System.out.println(tokenAtual);
        System.out.println(token);
        tokenAnterior = tokenAtual;
        tokenAtual++;
    }

    public void Inicio() {

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

    public void DefGlobal() {

        if (PrimeiroDefConstante.contains(token)) {
            DefConstante();
            DefPrincipal();
            DefGlobal2();
        }
    }

    public void DefGlobal2() {

        if (PrimeiroDefMetodo.contains(token)) {
            DefMetodo();
            DefGlobal2();
        }
    }

    public void DefConstante() {

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

    public void DefPrincipal() {

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

    public void DefMetodo() {

        if (token.equals("metodo")) {
            proximoToken();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
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

    public void ListaConst() {

        if (PrimeiroConstante.contains(token)) {
            Constante();

            if (token.equals(";")) {
                proximoToken();
                ListaConst2();
            }
        }
    }

    public void ListaConst2() {

        if (PrimeiroListaConst.contains(token)) {
            ListaConst();
        }
    }

    public void Constante() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();
            AtribuicaoConst();
            ListaAtribuicaoConst();
        }
    }

    public void ListaAtribuicaoConst() {

        if (token.equals(",")) {
            proximoToken();
            AtribuicaoConst();
        }
    }

    public void AtribuicaoConst() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            proximoToken();

            if (token.equals("=")) {
                proximoToken();
                Valor();
            }
        }
    }

    public void Tipo() {

        if ((token.equals("vazio"))) {
            proximoToken();
        } else {
            TipoId();
        }
    }

    public void TipoId() {

        if ((token.equals("inteiro")) || (token.equals("real")) || (token.equals("texto")) || (token.equals("boleano"))) {
            proximoToken();
        }
    }

    public void Valor() {

        if ((listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            proximoToken();
        } else if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            proximoToken();
        }
    }


    public void ListaParam() {
        TipoId();
        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            proximoToken();
            ListaParam2();
        }
    }

    //CORRIGIR
    public void ListaParam2() {

        if (token.equals(",")) {
            proximoToken();
            TipoId();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
                ListaParam2();
            }
        }
    }



    public void Declaracao() {
        DefVariavel();
    }

    public void DefVariavel() {

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

    public void limparEstruturas() {
        listaTokens.clear();
        tokenAnterior = 0;
        tokenAtual = 0;
    }
}