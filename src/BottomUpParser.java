import java.util.*;

class BottomUpParser {
    private final Map<String, String> rules = new LinkedHashMap<>();
    private final Stack<String> stack = new Stack<>();
    private final Set<String> terminals = new HashSet<>();


    public BottomUpParser(List<String> grammarRules) {

        for (String rule : grammarRules) {
            String[] parts = rule.split("->");
            String leftHandSide = parts[0].trim();
            String[] rightHandSide = parts[1].split("\\|");

            for (String production : rightHandSide) {
                rules.put(production.trim(), leftHandSide);
                extractTerminals(production.trim());
            }
        }
        terminals.add("$");
    }


    public boolean parse(String input) {
        List<String> tokens = tokenize(input);
        tokens.add("$");

        int index = 0;
        while (index < tokens.size()) {

            stack.push(tokens.get(index));
            index++;


            while (reduce()) {}
        }


        return stack.size() == 2 && stack.peek().equals(rules.get("S")) && stack.get(0).equals("$");
    }


    private boolean reduce() {
        String currentStack = String.join("", stack);

        for (String key : rules.keySet()) {
            if (currentStack.endsWith(key)) {

                for (int i = 0; i < key.length(); i++) {
                    stack.pop();
                }
                stack.push(rules.get(key));
                return true;
            }
        }
        return false;
    }


    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        String[] parts = input.split("\\s+");

        for (String part : parts) {
            if (terminals.contains(part)) {
                tokens.add(part);
            } else if (part.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add("id");
            } else {
                throw new IllegalArgumentException("Invalid token: " + part);
            }
        }
        return tokens;
    }


    private void extractTerminals(String production) {
        for (String symbol : production.split("")) {
            if (!Character.isUpperCase(symbol.charAt(0)) && !symbol.equals("ε")) {
                terminals.add(symbol);
            }
        }
    }
}
