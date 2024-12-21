import java.util.*;

class TopDownParser {
    private final Map<String, List<String>> rules = new HashMap<>();
    private List<String> tokens; // Tokenized input
    private int currentIndex;   // Current token index

    public TopDownParser(List<String> grammarRules) {
        for (String rule : grammarRules) {
            String[] parts = rule.split("->");
            String leftHandSide = parts[0].trim();
            String[] rightHandSide = parts[1].split("\\|");
            rules.put(leftHandSide, Arrays.asList(rightHandSide));
        }
    }

    // Start parsing with tokenized input
    public boolean parse(String input) {
        // Tokenize input
        tokens = tokenize(input);
        currentIndex = 0;

        // Start with the start symbol 'E'
        boolean result = match("E");

        // Check if all tokens were consumed
        return result && currentIndex == tokens.size();
    }

    private boolean match(String nonTerminal) {
        // If non-terminal is not defined, reject
        if (!rules.containsKey(nonTerminal)) {
            return false;
        }

        // Try each production for the non-terminal
        for (String production : rules.get(nonTerminal)) {
            int savedIndex = currentIndex; // Save current index for backtracking
            boolean matched = true;

            // Match each symbol in the production
            for (String symbol : production.trim().split(" ")) {
                if (symbol.equals("ε")) {
                    continue; // Skip epsilon
                } else if (Character.isUpperCase(symbol.charAt(0))) { // Non-terminal
                    matched = match(symbol); // Recursive call
                } else { // Terminal
                    matched = matchTerminal(symbol);
                }

                if (!matched) {
                    break; // Stop matching this production if it fails
                }
            }

            if (matched) {
                return true; // Successfully matched production
            }

            // Backtrack if production fails
            currentIndex = savedIndex;
        }
        return false; // No production matched
    }

    private boolean matchTerminal(String terminal) {
        if (currentIndex < tokens.size() && tokens.get(currentIndex).equals(terminal)) {
            currentIndex++; // Consume token
            return true;
        }
        return false; // Terminal does not match
    }

    private List<String> tokenize(String input) {
        // Tokenize input (handles multi-character tokens like 'id')
        List<String> tokens = new ArrayList<>();
        String[] parts = input.split("\\s+"); // Split by spaces
        for (String part : parts) {
            if (part.matches("[a-zA-Z_][a-zA-Z0-9_]*")) { // Match identifiers
                tokens.add("id");
            } else {
                tokens.add(part); // Operators
            }
        }
        return tokens;
    }
}
