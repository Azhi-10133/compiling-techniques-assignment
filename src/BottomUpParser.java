import java.util.*;

class BottomUpParser {
    private final Map<String, String> rules = new LinkedHashMap<>(); // Grammar rules
    private final Stack<String> stack = new Stack<>();              // Stack for parsing
    private final Set<String> terminals = new HashSet<>();          // Terminals

    // Constructor accepts custom grammar rules
    public BottomUpParser(List<String> grammarRules) {
        // Parse the grammar rules provided in the UI
        for (String rule : grammarRules) {
            String[] parts = rule.split("->"); // Split rule into LHS and RHS
            String leftHandSide = parts[0].trim(); // LHS (non-terminal)
            String[] rightHandSide = parts[1].split("\\|"); // RHS (productions)

            for (String production : rightHandSide) {
                rules.put(production.trim(), leftHandSide); // Map production to non-terminal
                extractTerminals(production.trim()); // Identify terminals
            }
        }
        terminals.add("$"); // End marker for parsing
    }

    // Parse the input string
    public boolean parse(String input) {
        List<String> tokens = tokenize(input);
        tokens.add("$"); // Add end marker to input tokens

        int index = 0;
        while (index < tokens.size()) {
            // Shift the next token onto the stack
            stack.push(tokens.get(index));
            index++;

            // Reduce while possible
            while (reduce()) {}
        }

        // Successful parsing if stack has only start symbol and end marker
        return stack.size() == 2 && stack.peek().equals(rules.get("S")) && stack.get(0).equals("$");
    }

    // Try to reduce the stack using grammar rules
    private boolean reduce() {
        String currentStack = String.join("", stack); // Convert stack to string for matching

        for (String key : rules.keySet()) {
            if (currentStack.endsWith(key)) {
                // Perform reduction
                for (int i = 0; i < key.length(); i++) {
                    stack.pop(); // Remove matched symbols
                }
                stack.push(rules.get(key)); // Push reduced non-terminal
                return true; // Successful reduction
            }
        }
        return false; // No reduction possible
    }

    // Tokenize the input string
    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        String[] parts = input.split("\\s+"); // Split by spaces

        for (String part : parts) {
            if (terminals.contains(part)) { // Valid terminal
                tokens.add(part);
            } else if (part.matches("[a-zA-Z_][a-zA-Z0-9_]*")) { // Treat as identifier
                tokens.add("id");
            } else {
                throw new IllegalArgumentException("Invalid token: " + part); // Invalid input
            }
        }
        return tokens;
    }

    // Extract terminals from grammar rules
    private void extractTerminals(String production) {
        for (String symbol : production.split("")) {
            if (!Character.isUpperCase(symbol.charAt(0)) && !symbol.equals("ε")) { // Lowercase = terminal
                terminals.add(symbol);
            }
        }
    }
}
