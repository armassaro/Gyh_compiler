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

public class AnalisadorLexico {
    private CodeFileReader fileReader;
    private List<Token> tokenList;
    private final int READING_BUFFER_SIZE = 16;
    private char[][] doubleBuffer;
    private int currentBuffer = 0;
    private Automaton automaton;
    
    private class Automaton { 
        private State actualState;
        private final Map<String, State> statesMap;
        
        private class State {
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
                this.isFinalState = true;
                this.tokenType = tokenType;
            }
            
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
            actualState = new State("q0", new HashMap<>());
            statesMap = new HashMap<>();
            
            // Mapeia todos os estados num HashMap
            statesMap.putAll((HashMap<String, State>) Map.ofEntries(
                    Map.entry("q_DelimAbre", new State("q_DelimAbre")),
                    Map.entry("q_DelimFecha", new State("q_DelimFecha")),
                    Map.entry("q_OpAritMult", new State("q_OpAritMult")),
                    Map.entry("q_OpAritDiv", new State("q_OpAritDiv")),
                    Map.entry("q_OpAritSoma", new State("q_OpAritSoma")),
                    Map.entry("q_OpAritSub", new State("q_OpAritSub")),
                    Map.entry("q_OpRelMenor", new State("q_OpRelMenor")),
                    Map.entry("q_OpRelMaior", new State("q_OpRelMaior")),
                    Map.entry("q_OpRelIgual", new State("q_OpRelIgual")),
                    Map.entry("q_Exclamacao", new State("q_Exclamacao")),
                    
            ));
            
            
            
            // Criar mapa interno para q0
            HashMap<String, State> q0Map = new HashMap<>();

            // Delimitadores
            q0Map.put("[", new State("q_DelimAbre", null, true, TokenType.DelimAbre));
            q0Map.put("]", new State("q_DelimFecha", null, true, TokenType.DelimFecha));

            // Operadores aritméticos
//            q0Map.put("*", TokenType.OpAritMult);
//            q0Map.put("/", TokenType.OpAritDiv);
//            q0Map.put("+", TokenType.OpAritSoma);
//            q0Map.put("-", TokenType.OpAritSub);

            // Operadores relacionais
//            q0Map.put("<", new State("q_OpRelMenor", new HashMap<>().putAll(Map.ofEntries()
//                    true, TokenType.OpRelMenor));
//            q0Map.put("<=", TokenType.OpRelMenorIgual);
//            q0Map.put(">", TokenType.OpRelMaior);
//            q0Map.put(">=", TokenType.OpRelMaiorIgual);
//            q0Map.put("==", TokenType.OpRelIgual);
//            q0Map.put("!=", TokenType.OpRelDif);

            // Parênteses
            q0Map.put("(", TokenType.AbrePar);
            q0Map.put(")", TokenType.FechaPar);

            // Atribuição
//            HashMap<String, Object> q1Map = new HashMap<>();
//            q1Map.put("=", TokenType.Atrib);
//
//            HashMap<String, Object> colonMap = new HashMap<>();
//            colonMap.put("q1", q1Map);
//
//            q0Map.put(":", colonMap);

            // Adicionar q0 ao mapa principal
            actualState.getTransitionsMap().putAll(q0Map);
        }

        public State getActualState() {
            return actualState;
        }

        public void setActualState(State actualState) {
            this.actualState = actualState;
        }
    }
    
    public AnalisadorLexico(String fileName) { 
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
    
    private void readBufferContent() {
        int bufferLength = doubleBuffer[currentBuffer].length;
        
        for(int a = 0; a < bufferLength; a++) {
            char ch = doubleBuffer[currentBuffer][a];
            if (ch == '\0') break;
            Automaton.State targetState = automaton.getActualState().getTransitionsMap().get(String.valueOf(ch));
            
            if(targetState == null) { 
                System.out.println("Houve um erro de análise léxica no código!");
                return;
            }
            
            switch(ch) { 
                case '[' -> {
                    tokenList.add(new Token(TokenType.DelimAbre, ch));
                }
                case ']' -> {
                    tokenList.add(new Token(TokenType.DelimFecha, ch));
                }
                case (char) -1 -> {
                    fileReader.setEoFReached(true);
                    return;
                }
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