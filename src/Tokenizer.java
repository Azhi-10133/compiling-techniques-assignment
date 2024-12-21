import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private final String input;
    private int pos = 0;
    private final int length;

    public Tokenizer(String input) {
        // Remove whitespace and append EOF symbol
        this.input = input.replaceAll("\\s+", "") + "$";
        this.length = this.input.length();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < length) {
            char current = input.charAt(pos);
            switch (current) {
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+"));
                    pos++;
                    break;
                case '*':
                    tokens.add(new Token(TokenType.MULT, "*"));
                    pos++;
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "("));
                    pos++;
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                    pos++;
                    break;
                case '$':
                    tokens.add(new Token(TokenType.EOF, "$"));
                    pos++;
                    break;
                default:
                    if (Character.isLetter(current)) {
                        StringBuilder sb = new StringBuilder();
                        while (pos < length && Character.isLetter(input.charAt(pos))) {
                            sb.append(input.charAt(pos++));
                        }
                        tokens.add(new Token(TokenType.ID, sb.toString()));
                    } else {
                        throw new RuntimeException("Unknown character: '" + current + "' at position " + pos);
                    }
            }
        }
        return tokens;
    }
}
