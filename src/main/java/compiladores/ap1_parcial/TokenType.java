package compiladores.ap1_parcial;

public enum TokenType {
    // Palavras reservadas
    PCDec("DECLARAR"), PCProg("PROGRAMA"), PCInt("INTEGER"), PCReal("REAL"), PCLer("LER"), PCImprimir("IMPRIMIR"), PCSe("SE"), PCEntao("ENTAO"), PCSenao("SENAO"), PCEnqto("ENQTO"), PCIni("INICIO"), PCFim("FINAL"),
    // Operadores aritméticos
    OpAritMult("*"), OpAritDiv("/"), OpAritSoma("+"), OpAritSub("-"),
    // Operadores relacionais
    OpRelMenor("<"), OpRelMenorIgual("<="), OpRelMaior(">"), OpRelMaiorIgual(">="), OpRelIgual("=="), OpRelDif("!="),
    // Operadores lógicos
    OpBoolE("E"), OpBoolOu("OU"),
    // Símbolos delimitadores
    DelimAbre("["), DelimFecha("]"),
    // Símbolo de atribuição
    Atrib(":="),
    // Símbolos de parênteses
    AbrePar("("), FechaPar(")"),
    // Declaração de variável
    Var(""),
    // Declaração de número inteiro
    NumInt(""),
    // Declaração de número decimal
    NumReal(""),
    // Declaração de string
    Cadeia(""),
    // End of file
    EoF("EoF"),
    // :
    DoisPontos(":");
    
    private String content; 
    
    TokenType(String content) { 
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
