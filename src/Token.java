public class Token {
    private String type;   // e.g. "id", "+", "*", "(", ")", "EOF"
    private String lexeme;

    public Token(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public String getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String toString() {
        return "[" + type + ", " + lexeme + "]";
    }
}
