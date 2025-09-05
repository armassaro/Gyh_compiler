package compiladores.ap1_parcial;

import compiladores.ap1_parcial.LexicalAnalyzer.Automaton.State;

public enum StatesEnum {
    // Estados de operadores relacionais e aritméticos
    q_0(new State("q_0")), 
    q_1(new State("q_1", TokenType.OpRelMaior)),            // >
    q_3(new State("q_3", TokenType.OpRelMenor)),            // <
    q_76(new State("q_76", TokenType.OpRelMaiorIgual)),     // >=
    q_2(new State("q_2", TokenType.OpRelMenorIgual)),       // <=
    q_73(new State("q_73", TokenType.OpRelDif)),            // !=
    q_4(new State("q_4")),                                  // =
    q_74(new State("q_74", TokenType.OpRelIgual)),          // ==
    q_75(new State("q_75", TokenType.Atrib)),               // :=
    q_5(new State("q_5", TokenType.DoisPontos)),            // :
    q_6(new State("q_6")),                                  // !
    q_54(new State("q_54", TokenType.AbrePar)),             // (
    q_53(new State("q_53", TokenType.FechaPar)),            // )
    q_47(new State("q_47", TokenType.OpAritMult)),          // *
    q_48(new State("q_48", TokenType.OpAritDiv)),           // /
    q_49(new State("q_49", TokenType.OpAritSoma)),          // +
    q_50(new State("q_50", TokenType.OpAritSub)),           // -
    q_78(new State("q_78", TokenType.DelimAbre)),           // [
    q_79(new State("q_79", TokenType.DelimFecha)),          // ]
    
    q_7(new State("q_7")),                                  // D
    q_8(new State("q_8")),                                  // E
    q_9(new State("q_9")),                                  // C
    q_55(new State("q_55")),                                // L
    q_56(new State("q_56")),                                // A
    q_57(new State("q_57")),                                // R
    q_58(new State("q_58")),                                // A
    q_59(new State("q_59", TokenType.PCDec)),               // R
    
    q_51(new State("q_51")),                                // O
    q_52(new State("q_52", TokenType.OpBoolOu)),            // U
    
    q_31(new State("q_31")),                                // S
    q_32(new State("q_32", TokenType.PCSe)),                // E
    q_33(new State("q_33")),                                // N
    q_34(new State("q_34")),                                // A
    q_35(new State("q_35", TokenType.PCSenao)),             // O
    
    q_10(new State("q_10")),                                // P
    q_11(new State("q_11")),                                // R
    q_12(new State("q_12")),                                // O
    q_13(new State("q_13")),                                // G
    q_60(new State("q_60")),                                // R
    q_61(new State("q_61")),                                // A
    q_62(new State("q_62")),                                // M
    q_63(new State("q_63", TokenType.PCProg)),              // A
    
    q_14(new State("q_14")),                                // I
    q_15(new State("q_15")),                                // N
    q_43(new State("q_43")),                                // I
    q_68(new State("q_68")),                                // C
    q_69(new State("q_69")),                                // I
    q_70(new State("q_70", TokenType.PCIni)),               // O
    
    q_16(new State("q_16")),                                // T
    q_64(new State("q_64")),                                // E
    q_65(new State("q_65")),                                // G
    q_66(new State("q_66")),                                // E
    q_67(new State("q_67", TokenType.PCInt)),               // R
    
    q_24(new State("q_24")),                                // M
    q_25(new State("q_25")),                                // P
    q_26(new State("q_26")),                                // R
    q_27(new State("q_27")),                                // I
    q_28(new State("q_28")),                                // M
    q_29(new State("q_29")),                                // I
    q_30(new State("q_30", TokenType.PCImprimir)),          // R
    
    q_17(new State("q_17")),                                // L
    q_18(new State("q_18")),                                // E
    q_19(new State("q_19", TokenType.PCLer)),               // R
    
    q_20(new State("q_20")),                                // R
    q_21(new State("q_21")),                                // E
    q_22(new State("q_22")),                                // A
    q_23(new State("q_23", TokenType.PCReal)),              // L
    
    q_36(new State("q_36", TokenType.OpBoolE)),             // E
    q_37(new State("q_37")),                                // N
    q_38(new State("q_38")),                                // T
    q_39(new State("q_39")),                                // A
    q_40(new State("q_40", TokenType.PCEntao)),             // O
    
    q_41(new State("q_41")),                                // Q
    q_42(new State("q_42")),                                // T
    q_77(new State("q_77", TokenType.PCEntao)),             // O
    
    q_44(new State("q_44")),                                // F
    q_45(new State("q_45")),                                // I
    q_46(new State("q_46")),                                // N
    q_71(new State("q_71")),                                // A
    q_72(new State("q_72", TokenType.PCFim)),               // L

    q_80(new State("q_80", TokenType.Var));                 // Variável
    
    private final State state;
    
    StatesEnum(State state) { 
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
