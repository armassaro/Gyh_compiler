package compiladores.ap1_parcial;

import java.io.IOException;
import java.util.HashMap;
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
    
    public class Automaton { 
        private State actualState;
        private State firstState;
        private final Map<String, State> transitionsTable;
        
        public class State {
            private String label;
            private Map<String, State> transitionsMap;
            private boolean isFinalState;
            private TokenType tokenType;
            
            // Construtor para o caso do estado não ser final, definindo o TokenType como null
            public State(String label, Map<String, State> transitionsMap) {
                this.label = label;
                this.transitionsMap = transitionsMap;
                this.isFinalState = false;
                this.tokenType = null;
            }
            
            // Construtor para o caso do estado ser final, ou seja, ele deve retornar um TokenType
            public State(String label, Map<String, State> transitionsMap, boolean isFinalState, TokenType tokenType) {
                this.label = label;
                this.transitionsMap = transitionsMap;
                this.isFinalState = isFinalState;
                this.tokenType = tokenType;
            }
            
            // Construtor dedicado para o HashMap inicial contendo os símbolos
            public State(String label) { 
                this.label = label;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public Map<String, State> getTransitionsMap() {
                return transitionsMap;
            }

            public void setTransitionsMap(HashMap<String, State> transitionsMap) {
                this.transitionsMap = transitionsMap;
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
        }
        
        public Automaton() {
            // Declaração do primeiro estado
            firstState = new State("q0", new HashMap<>());
            actualState = firstState;
            
            transitionsTable = Map.ofEntries(
                // Transições a aprtir do estado q0 
                Map.entry("[", new State("q_DelimAbre", null, true, TokenType.DelimAbre)),
                Map.entry("]", new State("q_DelimFecha", null, true, TokenType.DelimFecha)),
                Map.entry("(", new State("q_AbrePar", null, true, TokenType.AbrePar)),
                Map.entry(")", new State("q_FecharPar", null, true, TokenType.FechaPar))
            );
            
            firstState.getTransitionsMap().putAll(transitionsTable);
            
            // Criar mapa interno para q0
//            HashMap<String, StatesEnum> q0Map = new HashMap<>();
//
//            // Delimitadores
//            q0Map.put("[", StatesEnum.q_DelimAbre);
//            q0Map.put("]", StatesEnum.q_DelimFecha);
//
//            // Operadores aritméticos
//            q0Map.put("*", StatesEnum.q_OpAritMult);
//            q0Map.put("/", StatesEnum.q_OpAritDiv);
//            q0Map.put("+", StatesEnum.q_OpAritSoma);
//            q0Map.put("-", StatesEnum.q_OpAritSub);

            // Operadores relacionais
//            q0Map.put("<", new State("q_OpRelMenor", new HashMap<>().putAll(Map.ofEntries()
//                    true, TokenType.OpRelMenor));
//            q0Map.put("<=", TokenType.OpRelMenorIgual);
//            q0Map.put(">", TokenType.OpRelMaior);
//            q0Map.put(">=", TokenType.OpRelMaiorIgual);
//            q0Map.put("==", TokenType.OpRelIgual);
//            q0Map.put("!=", TokenType.OpRelDif);

            // Atribuição
//            HashMap<String, Object> q1Map = new HashMap<>();
//            q1Map.put("=", TokenType.Atrib);
//
//            HashMap<String, Object> colonMap = new HashMap<>();
//            colonMap.put("q1", q1Map);
//
//            q0Map.put(":", colonMap);

            // Adicionar q0 ao mapa principal
//            actualState.getTransitionsMap().putAll(q0Map);
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

        public Map<String, State> getTransitionsTable() {
            return transitionsTable;
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
        Automaton.State actualState = automaton.getActualState();
        
        for(int a = 0; a < bufferLength; a++) {
            char ch = doubleBuffer[currentBuffer][a];
            if(ch == '\0') break;
            // Se for um espaço, ignora. Senão vai tentar ler um espaço no autômato
            if(ch == ' ') continue;
            
            try { 
                // Avança para o próximo estado do autômato
                Automaton.State targetState = actualState.getTransitionsMap().get(String.valueOf(ch));
                automaton.setActualState(targetState);
                System.out.println(targetState.getTransitionsMap());
    //            if(targetState == null) { 
    //                System.out.println("Houve um erro de análise léxica no código!");
    //                return;
    //            }

                // Se o estado atual não possui mais transições e é um estado final, adiciona para a lista de tokens
                if(targetState.getTransitionsMap() == null && targetState.isFinalState()) { 
                    // Adiciona o novo token para a lista de tokens
                    tokenList.add(new Token(targetState.getTokenType(), ch));
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