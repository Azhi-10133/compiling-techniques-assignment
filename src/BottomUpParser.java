import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BottomUpParser {
    private final List<Token> tokens;

    public BottomUpParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean parse() {
        Stack<String> stack = new Stack<>();
        int pos = 0;

        while (pos < tokens.size()) {
            Token currentToken = tokens.get(pos);
            if (currentToken.getType() == TokenType.EOF) {
                pos++;
                continue;
            }

            stack.push(currentToken.toString());
            pos++;

            // Attempt reductions after each shift
            while (reduce(stack)) { }
        }

        // Final reductions
        while (reduce(stack)) { }

        return stack.size() == 1 && stack.peek().equals("E");
    }

    // Define grammar reduction rules
    private static final List<String[]> REDUCTIONS = Arrays.asList(
            new String[]{"E", "+", "T"},
            new String[]{"T", "*", "F"},
            new String[]{"(", "E", ")"},
            new String[]{"id"},
            new String[]{"F"},
            new String[]{"T"}
    );

    private boolean reduce(Stack<String> stack) {
        for (String[] rule : REDUCTIONS) {
            if (canApplyReduction(stack, rule)) {
                applyReduction(stack, rule);
                return true;
            }
        }
        return false;
    }

    private boolean canApplyReduction(Stack<String> stack, String[] rule) {
        if (stack.size() < rule.length) return false;
        for (int i = 0; i < rule.length; i++) {
            if (!stack.get(stack.size() - rule.length + i).equals(rule[i])) {
                return false;
            }
        }
        return true;
    }

    private void applyReduction(Stack<String> stack, String[] rule) {
        // Pop the RHS of the production
        for (int i = 0; i < rule.length; i++) {
            stack.pop();
        }

        // Determine the LHS non-terminal to push based on the rule
        switch (rule.length) {
            case 3:
                if (rule[0].equals("E") && rule[1].equals("+") && rule[2].equals("T")) {
                    stack.push("E");
                } else if (rule[0].equals("T") && rule[1].equals("*") && rule[2].equals("F")) {
                    stack.push("T");
                } else if (rule[0].equals("(") && rule[1].equals("E") && rule[2].equals(")")) {
                    stack.push("F");
                }
                break;
            case 1:
                if (rule[0].equals("id")) {
                    stack.push("F");
                } else if (rule[0].equals("F")) {
                    stack.push("T");
                } else if (rule[0].equals("T")) {
                    stack.push("E");
                }
                break;
            default:
                // No action for other rules
                break;
        }
    }
}
