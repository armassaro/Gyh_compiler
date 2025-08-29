package compiladores.ap1_parcial;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        }
        
        public Automaton() {
            // Declaração do primeiro estado
            actualState = StatesEnum.q_0.getState();
            firstState = StatesEnum.q_0.getState();
            
            transitionsMap = Map.ofEntries(
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
                    Map.entry("]", StatesEnum.q_79.getState())
                )),
                Map.entry(StatesEnum.q_1.getState(), Map.of("=", StatesEnum.q_76.getState())),
                Map.entry(StatesEnum.q_3.getState(), Map.of("=", StatesEnum.q_2.getState())),
                Map.entry(StatesEnum.q_6.getState(), Map.of("=", StatesEnum.q_73.getState())),
                Map.entry(StatesEnum.q_4.getState(), Map.of("=", StatesEnum.q_74.getState())),
                Map.entry(StatesEnum.q_5.getState(), Map.of("=", StatesEnum.q_75.getState())),
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
        
        for(int a = 0; a < bufferLength; a++) {
            char ch = doubleBuffer[currentBuffer][a];
            if(ch == '\0') break;
            // Se for um espaço, ignora. Senão vai tentar ler um espaço no autômato
            if(ch == ' ') {
                // Reseta o autômato par ao estado inicial ao ler um espaço
                automaton.setActualState(automaton.getFirstState());
                continue;
            };
            
            try { 
                // Avança para o próximo estado do autômato
                Automaton.State targetState = transitionsMap.get(automaton.getActualState()).get(String.valueOf(ch));
                automaton.setActualState(targetState);

                // Se o estado atual não possui mais transições e é um estado final, adiciona para a lista de tokens
                if(transitionsMap.get(targetState) == null && targetState.isFinalState()) { 
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