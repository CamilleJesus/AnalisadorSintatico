/*
Autores: Camille Jesús e Reinildo Souza
Componente Curricular: EXA869 - MI Processadores de Linguagem de Programação (P03)
Data: 18/08/2019
*/
package model;

import token.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class AnalisadorSintatico {
    private ArrayList<String>  PrimeiroDefConstante, PrimeiroDefMetodo, PrimeiroTipoId, PrimeiroAtribuicao, PrimeiroDefVariavel;
    private ArrayList<String> PrimeiroDefSe, PrimeiroDefEnquanto, PrimeiroDefEscreva, PrimeiroDefLeia, PrimeiroDefResultado;
    private ArrayList<String> PrimeiroOpRelacionalIgual, PrimeiroOpRelacionalOutros, PrimeiroOpAritmeticoAd, PrimeiroOpAritmeticoMul;
    private ArrayList<String> PrimeiroOpUnario, PrimeiroOpPosfixo, listaErros, PrimeiroDefPrincipal;
    private ArrayList<Token> listaTokens;
    private String token;
    private int tokenAtual = 0, tokenAnterior = 0, numeroArquivo = 0;
    private long linhaErro = 0;
    private ArrayList<ArrayList<Token>> listasTokens;

    public void setListaTokens(ArrayList<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public void setListasTokens(ArrayList<ArrayList<Token>> listaListasTokens) {
        this.listasTokens = listaListasTokens;
    }

    public AnalisadorSintatico() {
        PrimeiroDefConstante = new ArrayList<>();
        PrimeiroDefPrincipal = new ArrayList<>();
        PrimeiroDefMetodo = new ArrayList<>();
        PrimeiroTipoId = new ArrayList<>();
        PrimeiroAtribuicao = new ArrayList<>();
        PrimeiroDefVariavel = new ArrayList<>();
        PrimeiroDefSe = new ArrayList<>();
        PrimeiroDefEnquanto = new ArrayList<>();
        PrimeiroDefEscreva = new ArrayList<>();
        PrimeiroDefLeia = new ArrayList<>();
        PrimeiroDefResultado = new ArrayList<>();
        PrimeiroOpRelacionalIgual = new ArrayList<>();
        PrimeiroOpRelacionalOutros = new ArrayList<>();
        PrimeiroOpAritmeticoAd = new ArrayList<>();
        PrimeiroOpAritmeticoMul = new ArrayList<>();
        PrimeiroOpUnario = new ArrayList<>();
        PrimeiroOpPosfixo = new ArrayList<>();
        listaErros = new ArrayList<>();
        listaTokens = new ArrayList<>();
        listasTokens = new ArrayList<>();

        PrimeiroDefConstante.add("constantes");

        PrimeiroDefPrincipal.add("metodo");

        PrimeiroDefMetodo.add("metodo");

        PrimeiroTipoId.add("inteiro");
        PrimeiroTipoId.add("real");
        PrimeiroTipoId.add("texto");
        PrimeiroTipoId.add("boleano");

        PrimeiroAtribuicao.add("++");
        PrimeiroAtribuicao.add("--");
        PrimeiroAtribuicao.add("!");
        PrimeiroAtribuicao.add("verdadeiro");
        PrimeiroAtribuicao.add("falso");
        PrimeiroAtribuicao.add("(");

        PrimeiroDefVariavel.add("variaveis");

        PrimeiroDefSe.add("se");

        PrimeiroDefEnquanto.add("enquanto");

        PrimeiroDefEscreva.add("escreva");

        PrimeiroDefLeia.add("leia");

        PrimeiroDefResultado.add("resultado");

        PrimeiroOpRelacionalIgual.add("==");
        PrimeiroOpRelacionalIgual.add("!=");

        PrimeiroOpRelacionalOutros.add("<");
        PrimeiroOpRelacionalOutros.add(">");
        PrimeiroOpRelacionalOutros.add("<=");
        PrimeiroOpRelacionalOutros.add(">=");

        PrimeiroOpAritmeticoAd.add("+");
        PrimeiroOpAritmeticoAd.add("-");

        PrimeiroOpAritmeticoMul.add("*");
        PrimeiroOpAritmeticoMul.add("/");

        PrimeiroOpUnario.add("++");
        PrimeiroOpUnario.add("--");
        PrimeiroOpUnario.add("!");

        PrimeiroOpPosfixo.add("++");
        PrimeiroOpPosfixo.add("--");
        PrimeiroOpPosfixo.add("[");
        PrimeiroOpPosfixo.add("(");
        PrimeiroOpPosfixo.add(".");
    }

    public void mainSintatico() {

        for (int i = 0; i < listasTokens.size(); i++) {

            try {
                numeroArquivo = i + 1;
                setListaTokens(listasTokens.get(i));
                procedimentosGramatica();
                escreverArquivo();
                limparEstruturas();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        Token t = listaTokens.get(tokenAtual);
        token = t.getLexema();
        linhaErro = t.getLinha();
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
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "programa"));
        }
    }

    public void DefGlobal() {

        if (PrimeiroDefConstante.contains(token)) {
            DefConstante();
            DefPrincipal();
            DefGlobal2();
        } else if (PrimeiroDefPrincipal.contains(token)) {
            DefPrincipal();
            DefGlobal2();
        } else {
            listaErros.add(mensagemErro(linhaErro, "bloco", "principal"));
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
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "constante"));
        }
    }

    public void DefPrincipal() {

        if (token.equals("metodo")) {
            proximoToken();

            if (token.equals("principal")) {
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
                                } else {
                                    listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                                }
                            } else {
                                listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
                            }
                        } else {
                            listaErros.add(mensagemErro(linhaErro, "simbolo", ":"));
                        }
                    } else {
                        listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                    }
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", "("));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "palavra", "principal"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "metodo"));
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
                                } else {
                                    listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                                }
                            } else {
                                listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
                            }
                        } else {
                            listaErros.add(mensagemErro(linhaErro, "simbolo", ":"));
                        }
                    } else {
                        listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                    }
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", "("));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "identificador", "método"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "metodo"));
        }
    }

    public void ListaConst() {

        if (PrimeiroTipoId.contains(token)) {
            Constante();

            if (token.equals(";")) {
                proximoToken();
                ListaConst2();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", ";"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "tipo", ""));
        }
    }

    public void ListaConst2() {

        if (PrimeiroTipoId.contains(token)) {
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
                ValorConst();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "="));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "identificador", "constante"));
        }
    }

    public void Tipo() {

        if ((token.equals("vazio"))) {
            proximoToken();
        } else if (PrimeiroTipoId.contains(token)) {
            TipoId();
        } else {
            listaErros.add(mensagemErro(linhaErro, "tipo", ""));
        }
    }

    public void TipoId() {

        if ((token.equals("inteiro")) || (token.equals("real")) || (token.equals("texto")) || (token.equals("boleano"))) {
            proximoToken();
        }
    }

    public void Valor() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            proximoToken();
        } else if (token.equals("(")) {
            proximoToken();

            if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
                Expressao();

                if (token.equals(")")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "tipo", "retorno"));
            }
        } else if((listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso"))) {
            ValorConst();
        } else {
            listaErros.add(mensagemErro(linhaErro, "valor", ""));
        }
    }

    public void ValorConst() {

        if ((listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso"))) {
            proximoToken();
        }
    }

    public void ListaParam() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
                ListaParam2();
            } else {
                listaErros.add(mensagemErro(linhaErro, "identificador", "parâmetro"));
            }
        }
    }

    public void ListaParam2() {

        if (token.equals(",")) {
            proximoToken();
            TipoId();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
                ListaParam2();
            } else {
                listaErros.add(mensagemErro(linhaErro, "identificador", "parâmetro"));
            }
        }
    }

    public void ListaArg() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Atribuicao();
            ListaArg2();
        } else {
            listaErros.add(mensagemErro(linhaErro, "valor", ""));
        }
    }

    public void ListaArg2() {

        if (token.equals(",")) {
            proximoToken();
            Atribuicao();
            ListaArg2();
        }
    }

    public void Declaracao() {

        if (PrimeiroDefVariavel.contains(token)) {
            DefVariavel();
            Declaracao();
        } else if (PrimeiroDefSe.contains(token)) {
            DefSe();
            Declaracao();
        } else if (PrimeiroDefEnquanto.contains(token)) {
            DefEnquanto();
            Declaracao();
        } else if (PrimeiroDefEscreva.contains(token)) {
            DefEscreva();
            Declaracao();
        } else if (PrimeiroDefLeia.contains(token)) {
            DefLeia();
            Declaracao();
        } else if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            DefExpressao();
            Declaracao();
        } else if (PrimeiroDefResultado.contains(token)) {
            DefResultado();
            Declaracao();
        }
    }

    public void DefVariavel() {

        if (token.equals("variaveis")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                ListaVar();

                if (token.equals("}")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "variaveis"));
        }
    }

    public void ListaVar() {

        if (PrimeiroTipoId.contains(token)) {
            DeclaracaoVar();
            ListaVar2();
        } else {
            listaErros.add(mensagemErro(linhaErro, " ", "Variável esperada não encontrada."));
        }
    }

    //*** Verificar se o Primeiro está correto
    public void ListaVar2() {

        if (PrimeiroTipoId.contains(token)) {
            DeclaracaoVar();
            ListaVar2();
        }
    }


    public void DeclaracaoVar() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();
            ListaDeclaracaoVar();

            if (token.equals(";")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", ";"));
            }
        }
    }

    public void ListaDeclaracaoVar() {
        AtribuicaoVar();
        ListaDeclaracaoVar2();
    }

    public void ListaDeclaracaoVar2() {

        if (token.equals(",")) {
            proximoToken();
            AtribuicaoVar();
            ListaDeclaracaoVar2();
        }
    }

    public void AtribuicaoVar() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            Declarador();
            AtribuicaoVar2();
        }
    }

    public void AtribuicaoVar2() {

        if (token.equals("=")) {
            proximoToken();
            Inicializacao();
        }
    }

    public void Inicializacao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Atribuicao();
        } else if (token.equals("{")) {
            proximoToken();
            ListaInicializacaoVar();
            Inicializacao2();
        } else {
            listaErros.add(mensagemErro(linhaErro, "valor", ""));
        }
    }

    public void Inicializacao2() {

        if (token.equals("}")) {
            proximoToken();
        } else if (token.equals(",")) {
            proximoToken();

            if (token.equals("}")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "simbolo", ", ou }"));
        }
    }

    public void ListaInicializacaoVar() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Inicializacao();
            ListaInicializacaoVar2();
        }
    }

    public void ListaInicializacaoVar2() {

        if (token.equals(",")) {
            proximoToken();
            Inicializacao();
            ListaInicializacaoVar2();
        }
    }

    public void Declarador() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            proximoToken();
            Declarador2();
        } else {
            listaErros.add(mensagemErro(linhaErro, "identificador", "variável"));
        }
    }

    public void Declarador2() {

        if (token.equals("[")) {
            proximoToken();
            Declarador3();
        }
    }

    public void Declarador3() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAtribuicao();

            if (token.equals("]")) {
                proximoToken();
                Declarador2();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "]"));
            }
        } else if(token.equals("]")) {
            proximoToken();
            Declarador2();
        } else {
            listaErros.add(mensagemErro(linhaErro, "valor", ""));
        }
    }

    public void DefSe() {

        if (token.equals("se")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ExprRelacionalIgual();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals("entao")) {
                        proximoToken();

                        if (token.equals("{")) {
                            proximoToken();
                            Declaracao();

                            if (token.equals("}")) {
                                proximoToken();
                                DefSenao();
                            } else {
                                listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                            }
                        } else {
                            listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
                        }
                    } else {
                        listaErros.add(mensagemErro(linhaErro, "palavra", "entao"));
                    }
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "se"));
        }
    }

    public void DefSenao() {

        if (token.equals("senao")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                Declaracao();

                if (token.equals("}")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
            }
        }
    }

    public void DefEnquanto() {

        if (token.equals("enquanto")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ExprRelacionalIgual();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals("{")) {
                        proximoToken();
                        Declaracao();

                        if (token.equals("}")) {
                            proximoToken();
                        } else {
                            listaErros.add(mensagemErro(linhaErro, "simbolo", "}"));
                        }
                    } else {
                        listaErros.add(mensagemErro(linhaErro, "simbolo", "{"));
                    }
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "enquanto"));
        }
    }

    public void DefEscreva() {

        if (token.equals("escreva")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ListaArg();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals(";")) {
                        proximoToken();
                    } else {
                        listaErros.add(mensagemErro(linhaErro, "simbolo", ";"));
                    }
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "escreva"));
        }
    }

    public void DefLeia() {

        if (token.equals("leia")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ListaArg();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals(";")) {
                        proximoToken();
                    } else {
                        listaErros.add(mensagemErro(linhaErro, "simbolo", ";"));
                    }
                } else {
                    listaErros.add(mensagemErro(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "leia"));
        }
    }

    public void DefResultado() {

        if (token.equals("resultado")) {
            proximoToken();
            Expressao();

            if (token.equals(";")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", ";"));
            }
        } else {
            listaErros.add(mensagemErro(linhaErro, "palavra", "resultado"));
        }
    }

    public void DefExpressao() {

        if (token.equals(";")) {
            proximoToken();
        } else if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Expressao();

            if (token.equals(";")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErro(linhaErro, "simbolo", ";"));
            }
        }
    }

    public void Expressao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Atribuicao();
            Expressao2();
        }
    }

    public void Expressao2() {

        if (token.equals(",")) {
            proximoToken();
            Atribuicao();
            Expressao2();
        }
    }

    public void Atribuicao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAtribuicao();
            Atribuicao2();
        }
    }

    public void Atribuicao2() {

        if (token.equals("=")) {
            proximoToken();
            ExprAtribuicao();
            Atribuicao2();
        }
    }

    public void ExprAtribuicao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprLogicaOu();
        }
    }

    public void ExprLogicaOu() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprLogicaE();
            ExprLogicaOu2();
        }
    }

    public void ExprLogicaOu2() {

        if (token.equals("||")) {
            proximoToken();
            ExprLogicaE();
            ExprLogicaOu2();
        }
    }

    public void ExprLogicaE() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprRelacionalIgual();
            ExprLogicaE2();
        }
    }

    public void ExprLogicaE2() {

        if (token.equals("&&")) {
            proximoToken();
            ExprRelacionalIgual();
            ExprLogicaE2();
        }
    }

    public void ExprRelacionalIgual() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprRelacionalOutras();
            ExprRelacionalIgual2();
        }
    }

    public void ExprRelacionalIgual2() {

        if (PrimeiroOpRelacionalIgual.contains(token)) {
            OpRelacionalIgual();
            ExprRelacionalOutras();
            ExprRelacionalIgual2();
        }
    }

    public void ExprRelacionalOutras() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAritmeticaAd();
            ExprRelacionalOutras2();
        }
    }

    public void ExprRelacionalOutras2() {

        if (PrimeiroOpRelacionalOutros.contains(token)) {
            OpRelacionalOutros();
            ExprAritmeticaAd();
            ExprRelacionalOutras2();
        }
    }

    public void ExprAritmeticaAd() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAritmeticaMult();
            ExprAritmeticaAd2();
        }
    }

    public void ExprAritmeticaAd2() {

        if (PrimeiroOpAritmeticoAd.contains(token)) {
            OpAritmeticoAd();
            ExprAritmeticaMult();
            ExprAritmeticaAd2();
        }
    }

    public void ExprAritmeticaMult() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprUnaria();
            ExprAritmeticaMult2();
        }
    }

    public void ExprAritmeticaMult2() {

        if (PrimeiroOpAritmeticoMul.contains(token)) {
            OpAritmeticoMul();
            ExprUnaria();
            ExprAritmeticaMult2();
        }
    }

    public void ExprUnaria() {

        if (PrimeiroOpUnario.contains(token)) {
            OpUnario();
            ExprUnaria();
        } else if ((listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso")) || (token.equals("("))) {
            ExprPosfixa();
        }
    }

    public void ExprPosfixa() {

        if ((listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso")) || (token.equals("("))) {
            Valor();
            ExprPosfixa2();
        }
    }

    public void ExprPosfixa2() {

        if (PrimeiroOpPosfixo.contains(token)) {
            OpPosfixo();
            ExprPosfixa2();
        }
    }

    public void OpRelacionalIgual() {

        if ((token.equals("==")) || (token.equals("!="))) {
            proximoToken();
        }
    }

    public void OpRelacionalOutros() {

        if ((token.equals("<")) || (token.equals(">")) || (token.equals("<=")) || (token.equals(">="))) {
            proximoToken();
        }
    }

    public void OpAritmeticoAd() {

        if ((token.equals("+")) || (token.equals("-"))) {
            proximoToken();
        }
    }

    public void OpAritmeticoMul() {

        if ((token.equals("*")) || (token.equals("/"))) {
            proximoToken();
        }
    }

    public void OpUnario() {

        if ((token.equals("++")) || (token.equals("--")) || (token.equals("!"))) {
            proximoToken();
        }
    }

    public void OpPosfixo() {

        if ((token.equals("++")) || (token.equals("--"))) {
            proximoToken();
        } else if (token.equals("[")) {
            proximoToken();
            Expressao();

            if (token.equals("]")) {
                proximoToken();
            }
        } else if (token.equals("(")) {
            proximoToken();
            OpPosfixo2();
        } else if (token.equals(".")) {
            proximoToken();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
            }
        }
    }

    public void OpPosfixo2() {

        if (token.equals(")")) {
            proximoToken();
        } else if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ListaArg();

            if(token.equals(")")) {
                proximoToken();
            }
        }
    }

    public static String mensagemErro (long linhaErro, String tipo, String valor) {

        if (tipo.equals("simbolo")) {
            return ("Erro sintático na linha " + linhaErro + ". Símbolo esperado não encontrado: " + valor + ".");
        } else if (tipo.equals("numero")) {
            return ("Erro sintático na linha " + linhaErro + ". Número esperado não encontrado: " + valor + ".");
        } else if (tipo.equals("palavra")) {
            return ("Erro sintático na linha " + linhaErro + ". Palavra reservada esperada não encontrada: " + valor + ".");
        } else if (tipo.equals("identificador")) {
            return ("Erro sintático na linha " + linhaErro + ". Identificador de " + valor + " esperado não encontrado.");
        } else if (tipo.equals("tipo")) {
            return ("Erro sintático na linha " + linhaErro + ". Tipo esperado não encontrado.");
        } else if (tipo.equals("valor")) {
            return ("Erro sintático na linha " + linhaErro + ". Valor esperado não encontrado.");
        } else if (tipo.equals("bloco")) {
            return ("Erro sintático na linha " + linhaErro + ". Bloco de comando " + valor + " esperado não encontrado.");
        } else if (tipo.equals(" ")) {
            return ("Erro sintático na linha " + linhaErro + "." + valor);
        }
        return "";
    }

    public void escreverArquivo() throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("teste/saidaSintatico" + numeroArquivo + ".txt"));

        if (listaErros.isEmpty()) {
            buffWrite.append("\nSucesso!");
        } else {

            for (int i = 0; i < listaErros.size(); i++) {
                buffWrite.append("\n" + listaErros.get(i));
            }
        }
        buffWrite.close();
        System.out.println("\nResultado da análise sintático no arquivo: saidaSintatico" + numeroArquivo + ".txt");
    }

    public void limparEstruturas() {
        listaTokens.clear();
        listaErros.clear();
        tokenAnterior = 0;
        tokenAtual = 0;
    }
}