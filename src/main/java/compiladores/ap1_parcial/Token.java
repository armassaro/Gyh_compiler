package compiladores.ap1_parcial;

public class Token {
    private TokenType type;
    private char content;
    
    public Token(TokenType type, char content) { 
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

    public char getContent() {
        return content;
    }

    public void setContent(char content) {
        this.content = content;
    }
}
