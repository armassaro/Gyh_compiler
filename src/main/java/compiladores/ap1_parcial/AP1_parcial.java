package compiladores.ap1_parcial;

public class AP1_parcial {
    public static void main(String[] args) {
        AnalisadorLexico a = new AnalisadorLexico("programa.gyh");
        a.tokenize();
    }
}
