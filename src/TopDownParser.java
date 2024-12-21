import java.util.List;

public class TopDownParser {
    private final List<Token> tokens;
    private int pos = 0;

    public TopDownParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean parse() {
        try {
            E();
            expect(TokenType.EOF);
            return true;
        } catch (ParseException e) {
            throw new RuntimeException("Top-Down Parsing Error: " + e.getMessage());
        }
    }

    private void E() throws ParseException {
        T();
        EPrime();
    }

    private void EPrime() throws ParseException {
        if (match(TokenType.PLUS)) {
            T();
            EPrime();
        }
    }

    private void T() throws ParseException {
        F();
        TPrime();
    }

    private void TPrime() throws ParseException {
        if (match(TokenType.MULT)) {
            F();
            TPrime();
        }
    }

    private void F() throws ParseException {
        if (match(TokenType.LPAREN)) {
            E();
            expect(TokenType.RPAREN);
        } else if (match(TokenType.ID)) {

        } else {
            throw new ParseException("Expected 'id' or '(', found: " + peek());
        }
    }

    private boolean match(TokenType type) {
        if (pos < tokens.size() && tokens.get(pos).getType() == type) {
            pos++;
            return true;
        }
        return false;
    }

    private void expect(TokenType type) throws ParseException {
        if (!match(type)) {
            throw new ParseException("Expected " + type + ", found: " + (pos < tokens.size() ? tokens.get(pos) : "EOF"));
        }
    }

    private Token peek() {
        return pos < tokens.size() ? tokens.get(pos) : new Token(TokenType.EOF, "$");
    }
}
