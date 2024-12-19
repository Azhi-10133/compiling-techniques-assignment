import java.util.*;

public class Lexer {
    private String input;
    private int pos;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }
            switch (c) {
                case '+':
                    tokens.add(new Token("+", "+"));
                    pos++;
                    break;
                case '*':
                    tokens.add(new Token("*", "*"));
                    pos++;
                    break;
                case '(':
                    tokens.add(new Token("(", "("));
                    pos++;
                    break;
                case ')':
                    tokens.add(new Token(")", ")"));
                    pos++;
                    break;
                default:
                    if (Character.isLetter(c)) {
                        StringBuilder sb = new StringBuilder();
                        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
                            sb.append(input.charAt(pos));
                            pos++;
                        }
                        tokens.add(new Token("id", sb.toString()));
                    } else {
                        throw new RuntimeException("Unexpected character: " + c);
                    }
            }
        }
        tokens.add(new Token("EOF", "EOF"));
        return tokens;
    }
}
