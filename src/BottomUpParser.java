import java.util.*;

class BottomUpParser {
    private final Map<String, String> rules = new LinkedHashMap<>();
    private final Stack<String> stack = new Stack<>();
    private final Set<String> terminals = new HashSet<>(Arrays.asList("+", "*", "id", "$"));


    public BottomUpParser() {
        rules.put("E+T", "E");
        rules.put("T*F", "T");
        rules.put("T", "E");
        rules.put("id", "F");
        rules.put("F", "T");
        rules.put("E", "S");
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


        return stack.size() == 2 && stack.peek().equals("S") && stack.get(0).equals("$");
    }


    private boolean reduce() {
        String currentStack = String.join("", stack);


        for (String key : rules.keySet()) {
            if (currentStack.endsWith(key)) {
                String[] symbols = key.split("");
                for (int i = 0; i < symbols.length; i++) {
                    if (stack.isEmpty()) {
                        return false;
                    }
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
            if (part.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add("id");
            } else if (terminals.contains(part)) {
                tokens.add(part);
            } else {
                throw new IllegalArgumentException("Invalid token: " + part);
            }
        }
        return tokens;
    }
}
