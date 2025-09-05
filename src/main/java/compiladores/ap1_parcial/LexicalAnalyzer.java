package compiladores.ap1_parcial;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LexicalAnalyzer {
    private CodeFileReader fileReader;
    private List<Token> tokenList;
    private final int READING_BUFFER_SIZE = 16;
    private char[][] doubleBuffer;
    private int currentBuffer = 0;
    private Automaton automaton;
    private boolean isReadingComment = false;
    
    public class Automaton { 
        private State actualState;
        private State firstState;
        private final Map<State, Map<String, State>> transitionsMap;
        
        public static class State {
            private String label;
            private boolean isFinalState;
            private final TokenType tokenType;
            
            // Construtor para o caso do estado não ser final, definindo o TokenType como null
            public State(String label) {
                this.label = label;
                this.isFinalState = false;
                this.tokenType = null;
            }
            
            // Construtor para o caso do estado ser final, ou seja, ele deve retornar um TokenType
            public State(String label, TokenType tokenType) {
                this.label = label;
                this.isFinalState = true;
                this.tokenType = tokenType;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public boolean isFinalState() {
                return isFinalState;
            }

            public TokenType getTokenType() {
                return tokenType;
            }

            public boolean isIsFinalState() {
                return isFinalState;
            }

            public void setIsFinalState(boolean isFinalState) {
                this.isFinalState = isFinalState;
            }

            public String toString() {
                return "label: " + label + ", isFinalState: " + isFinalState + ", tokenType: " + tokenType;
            }   
        }
        
        public Automaton() {
            // Declaração do primeiro estado
            actualState = StatesEnum.q_0.getState();
            firstState = StatesEnum.q_0.getState();
            
            transitionsMap = Map.ofEntries(
                // q_0
                Map.entry(firstState, Map.ofEntries(
                    Map.entry("(", StatesEnum.q_54.getState()),
                    Map.entry(")", StatesEnum.q_53.getState()),
                    Map.entry(">", StatesEnum.q_1.getState()),
                    Map.entry("<", StatesEnum.q_3.getState()),
                    Map.entry("!", StatesEnum.q_6.getState()),
                    Map.entry("=", StatesEnum.q_4.getState()),
                    Map.entry(":", StatesEnum.q_5.getState()),
                    Map.entry("*", StatesEnum.q_47.getState()),
                    Map.entry("/", StatesEnum.q_48.getState()),
                    Map.entry("+", StatesEnum.q_49.getState()),
                    Map.entry("-", StatesEnum.q_50.getState()),
                    Map.entry("[", StatesEnum.q_78.getState()),
                    Map.entry("]", StatesEnum.q_79.getState()),
                    // Letras
                    Map.entry("O", StatesEnum.q_51.getState()),
                    Map.entry("S", StatesEnum.q_31.getState()),
                    Map.entry("D", StatesEnum.q_7.getState()),
                    Map.entry("P", StatesEnum.q_10.getState()),
                    Map.entry("I", StatesEnum.q_14.getState()),
                    Map.entry("L", StatesEnum.q_17.getState()),
                    Map.entry("R", StatesEnum.q_20.getState()),
                    Map.entry("E", StatesEnum.q_36.getState()),
                    Map.entry("F", StatesEnum.q_44.getState())
                )),
                Map.entry(StatesEnum.q_1.getState(), Map.of("=", StatesEnum.q_76.getState())),  // >=
                Map.entry(StatesEnum.q_3.getState(), Map.of("=", StatesEnum.q_2.getState())),   // <=
                Map.entry(StatesEnum.q_6.getState(), Map.of("=", StatesEnum.q_73.getState())),  // !=
                Map.entry(StatesEnum.q_4.getState(), Map.of("=", StatesEnum.q_74.getState())),  // ==
                Map.entry(StatesEnum.q_5.getState(), Map.of("=", StatesEnum.q_75.getState())),  // :=
                    
                Map.entry(StatesEnum.q_51.getState(), Map.of("U", StatesEnum.q_52.getState())), // OU
                Map.entry(StatesEnum.q_31.getState(), Map.of("E", StatesEnum.q_32.getState())), // SE
                Map.entry(StatesEnum.q_32.getState(), Map.of("N", StatesEnum.q_33.getState())), // SEN
                Map.entry(StatesEnum.q_33.getState(), Map.of("A", StatesEnum.q_34.getState())), // SENA
                Map.entry(StatesEnum.q_34.getState(), Map.of("O", StatesEnum.q_35.getState())), // SENAO
                Map.entry(StatesEnum.q_7.getState(), Map.of("E", StatesEnum.q_8.getState())),   // DE
                Map.entry(StatesEnum.q_8.getState(), Map.of("C", StatesEnum.q_9.getState())),   // DEC
                Map.entry(StatesEnum.q_9.getState(), Map.of("L", StatesEnum.q_55.getState())),  // DECL
                Map.entry(StatesEnum.q_55.getState(), Map.of("A", StatesEnum.q_56.getState())), // DECLA
                Map.entry(StatesEnum.q_56.getState(), Map.of("R", StatesEnum.q_57.getState())), // DECLAR
                Map.entry(StatesEnum.q_57.getState(), Map.of("A", StatesEnum.q_58.getState())), // DECLARA
                Map.entry(StatesEnum.q_58.getState(), Map.of("R", StatesEnum.q_59.getState())),  // DECLARAR
                Map.entry(StatesEnum.q_10.getState(), Map.of("R", StatesEnum.q_11.getState())), // PR
                Map.entry(StatesEnum.q_11.getState(), Map.of("O", StatesEnum.q_12.getState())), // PRO
                Map.entry(StatesEnum.q_12.getState(), Map.of("G", StatesEnum.q_13.getState())), // PROG
                Map.entry(StatesEnum.q_13.getState(), Map.of("R", StatesEnum.q_60.getState())), // PROGR
                Map.entry(StatesEnum.q_60.getState(), Map.of("A", StatesEnum.q_61.getState())), // PROGRA
                Map.entry(StatesEnum.q_61.getState(), Map.of("M", StatesEnum.q_62.getState())), // PROGRAM
                Map.entry(StatesEnum.q_62.getState(), Map.of("A", StatesEnum.q_63.getState())), // PROGRAMA
                Map.entry(StatesEnum.q_14.getState(), Map.ofEntries(
                    Map.entry("N", StatesEnum.q_15.getState()),                                 // IN
                    Map.entry("M", StatesEnum.q_24.getState())                                  // IM
                )),
                Map.entry(StatesEnum.q_15.getState(), Map.ofEntries(
                    Map.entry("I", StatesEnum.q_43.getState()),                                 // INI
                    Map.entry("T", StatesEnum.q_16.getState())                                  // INT
                )),
                Map.entry(StatesEnum.q_43.getState(), Map.of("C", StatesEnum.q_68.getState())), // INIC
                Map.entry(StatesEnum.q_68.getState(), Map.of("I", StatesEnum.q_69.getState())), // INICI
                Map.entry(StatesEnum.q_69.getState(), Map.of("O", StatesEnum.q_70.getState())), // INICIO
                Map.entry(StatesEnum.q_16.getState(), Map.of("E", StatesEnum.q_64.getState())), // INTE
                Map.entry(StatesEnum.q_64.getState(), Map.of("G", StatesEnum.q_65.getState())), // INTEG
                Map.entry(StatesEnum.q_65.getState(), Map.of("E", StatesEnum.q_66.getState())), // INTEGE
                Map.entry(StatesEnum.q_66.getState(), Map.of("R", StatesEnum.q_67.getState())), // INTEGER
                Map.entry(StatesEnum.q_24.getState(), Map.of("P", StatesEnum.q_25.getState())), // IMP
                Map.entry(StatesEnum.q_25.getState(), Map.of("R", StatesEnum.q_26.getState())), // IMPR
                Map.entry(StatesEnum.q_26.getState(), Map.of("I", StatesEnum.q_27.getState())), // IMPRI
                Map.entry(StatesEnum.q_27.getState(), Map.of("M", StatesEnum.q_28.getState())), // IMPRIM
                Map.entry(StatesEnum.q_28.getState(), Map.of("I", StatesEnum.q_29.getState())), // IMPRIMI
                Map.entry(StatesEnum.q_29.getState(), Map.of("R", StatesEnum.q_30.getState())), // IMPRIMIR
                Map.entry(StatesEnum.q_17.getState(), Map.of("E", StatesEnum.q_18.getState())), // LE
                Map.entry(StatesEnum.q_18.getState(), Map.of("R", StatesEnum.q_19.getState())), // LER
                Map.entry(StatesEnum.q_20.getState(), Map.of("E", StatesEnum.q_21.getState())), // RE
                Map.entry(StatesEnum.q_21.getState(), Map.of("A", StatesEnum.q_22.getState())), // REA
                Map.entry(StatesEnum.q_22.getState(), Map.of("L", StatesEnum.q_23.getState())), // REAL
                Map.entry(StatesEnum.q_36.getState(), Map.of("N", StatesEnum.q_37.getState())), // EN
                Map.entry(StatesEnum.q_37.getState(), Map.ofEntries(
                    Map.entry("T", StatesEnum.q_38.getState()),                                 // ENT
                    Map.entry("Q", StatesEnum.q_41.getState())                                  // ENQ
                )),
                Map.entry(StatesEnum.q_38.getState(), Map.of("A", StatesEnum.q_39.getState())), // ENTA
                Map.entry(StatesEnum.q_39.getState(), Map.of("O", StatesEnum.q_40.getState())), // ENTAO
                Map.entry(StatesEnum.q_41.getState(), Map.of("T", StatesEnum.q_42.getState())), // ENQT
                Map.entry(StatesEnum.q_42.getState(), Map.of("O", StatesEnum.q_77.getState())), // ENQTO
                Map.entry(StatesEnum.q_44.getState(), Map.of("I", StatesEnum.q_45.getState())), // FI
                Map.entry(StatesEnum.q_45.getState(), Map.of("N", StatesEnum.q_46.getState())), // FIN
                Map.entry(StatesEnum.q_46.getState(), Map.of("A", StatesEnum.q_71.getState())), // FINA
                Map.entry(StatesEnum.q_71.getState(), Map.of("L", StatesEnum.q_72.getState()))  // FINAL
            );
        }
                
        public State getActualState() {
            return actualState;
        }

        public void setActualState(State actualState) {
            this.actualState = actualState;
        }

        public State getFirstState() {
            return firstState;
        }

        public Map<State, Map<String, State>> getTransitionsMap() {
            return transitionsMap;
        }
    }
    
    public LexicalAnalyzer(String fileName) { 
        fileReader = new CodeFileReader(fileName);
        this.doubleBuffer = new char[2][READING_BUFFER_SIZE/2];
        this.tokenList = new LinkedList<>();
        this.automaton = new Automaton();
    }
    
    private boolean reloadBuffer(int bufferIndex) {
        try {
            int bytesRead = fileReader.readToBuffer(doubleBuffer[bufferIndex], READING_BUFFER_SIZE/2); 
            if (bytesRead == -1) {
                fileReader.setEoFReached(true);
                return false;
            }
            fileReader.updateLastCharIndex(bytesRead);
            return true;
        }
        catch(IOException e) { 
            System.out.println("Houve um erro na leitura do arquivo ao tentar recarregar o buffer!");
            fileReader.setEoFReached(true);
            return false;
        }
    }
    
    private void swapBufferContent() { 
        currentBuffer = (currentBuffer == 0) ? 1 : 0;
    }
    
    // Possui toda a lógica de leitura de buffer e navegação pelos estados do autômato
    private void readBufferContent() {
        int bufferLength = doubleBuffer[currentBuffer].length;
        Map<Automaton.State, Map<String, Automaton.State>> transitionsMap = automaton.getTransitionsMap();
        Automaton.State targetState = null;
        Character ch;
        StringBuilder readContent = new StringBuilder();

        for(int a = 0; a < bufferLength; a++) {
            ch = doubleBuffer[currentBuffer][a];
            // Ativa modo de comentário ao encontrar '#'
            if (ch == '#') {
                isReadingComment = true;
            }
            // Ignora caracteres enquanto estiver em modo de comentário
            if (isReadingComment) {
                if (ch == '\n') {
                    isReadingComment = false;
                }
                continue;
            }

            if(ch == '\0' || ch == '\n') continue;
            
            // Se for um espaço, ignora. Senão vai tentar ler um espaço no autômato
            if (ch == ' ') {
                if (automaton.getActualState().isFinalState() && readContent.length() > 0) {
                    tokenList.add(new Token(automaton.getActualState().getTokenType(), readContent.toString()));
                    readContent = new StringBuilder();
                }
                automaton.setActualState(automaton.getFirstState());
                continue;
            }
            
            try { 
                // Avança para o próximo estado do autômato
                targetState = transitionsMap.get(automaton.getActualState()).get(String.valueOf(ch));
                automaton.setActualState(targetState);
                // Concatena o conteúdo lido para readContent
                readContent.append(ch);

                if(targetState != null) { 
                    System.out.println("Lido: " + ch + " | Estado atual: " + automaton.getActualState());
                }
                
                // Se o estado atual não possui mais transições e é um estado final, adiciona para a lista de tokens
                if(transitionsMap.get(targetState) == null && targetState.isFinalState()) { 
                    // Adiciona o novo token para a lista de tokens
                    tokenList.add(new Token(targetState.getTokenType(), targetState.getTokenType().getContent()));
                    // Reinicia o automato
                    automaton.setActualState(automaton.getFirstState());
                }
            }
            catch(NullPointerException e) { 
                automaton.setActualState(automaton.getFirstState());
            }
        }
    }
    
    public void tokenize() { 
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        Future<Boolean> future = null;

        try {
            if (!reloadBuffer(currentBuffer)) {
                return;
            }
            
            while(!fileReader.isEoFReached()) { 
                int nextBuffer = (currentBuffer == 0) ? 1 : 0;
                future = threadPool.submit(() -> reloadBuffer(nextBuffer));
                
                readBufferContent();
                
                if (future != null) {
                    try {
                        boolean success = future.get();
                        if (!success) {
                            break;
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        break;
                    }
                }
                
                swapBufferContent();
            }
        } finally {
            threadPool.shutdown();
        }

        System.out.println(tokenList.toString());
    }
}