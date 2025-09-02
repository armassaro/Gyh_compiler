package compiladores.ap1_parcial;

public class AP1_parcial {
    public static void main(String[] args) {
        LexicalAnalyzer a = new LexicalAnalyzer("programa.gyh");
        a.tokenize();
    }
}
// teste