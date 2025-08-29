package compiladores.ap1_parcial;

public class Token {
    private TokenType type;
    private String content;
    
    public Token(TokenType type, String content) { 
        this.type = type;
        this.content = content;
    }
    
    public String toString() { 
        return "<" + this.type + "," + this.content + ">";
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
