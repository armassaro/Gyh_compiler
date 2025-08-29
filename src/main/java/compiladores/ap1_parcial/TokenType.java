package compiladores.ap1_parcial;

public enum TokenType {
    // Palavras reservadas
    PCDec, PCProg, PCInt, PCReal, PCLer, PCImprimir, PCSe, PCEntao, PCSenao, PCEnqto, PCIni, PCFim,
    // Operadores aritméticos
    OpAritMult, OpAritDiv, OpAritSoma, OpAritSub,
    // Operadores relacionais
    OpRelMenor, OpRelMenorIgual, OpRelMaior, OpRelMaiorIgual, OpRelIgual, OpRelDif,
    // Operadores lógicos
    OpBoolE, OpBoolOu,
    // Símbolos delimitadores
    DelimAbre, DelimFecha,
    // Símbolo de atribuição
    Atrib,
    // Símbolos de parênteses
    AbrePar, FechaPar,
    // Declaração de variável
    Var,
    // Declaração de número inteiro
    NumInt,
    // Declaração de número decimal
    NumReal,
    // Declaração de string
    Cadeia,
    // End of file
    EoF,
    // :
    DoisPontos
}
