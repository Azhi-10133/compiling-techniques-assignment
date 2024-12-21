import java.util.*;

class TopDownParser {
    private final Map<String, List<String>> rules = new HashMap<>();
    private List<String> tokens;
    private int currentIndex;

    public TopDownParser(List<String> grammarRules) {
        for (String rule : grammarRules) {
            String[] parts = rule.split("->");
            String leftHandSide = parts[0].trim();
            String[] rightHandSide = parts[1].split("\\|");
            rules.put(leftHandSide, Arrays.asList(rightHandSide));
        }
    }


    public boolean parse(String input) {

        tokens = tokenize(input);
        currentIndex = 0;


        boolean result = match("E");


        return result && currentIndex == tokens.size();
    }

    private boolean match(String nonTerminal) {

        if (!rules.containsKey(nonTerminal)) {
            return false;
        }


        for (String production : rules.get(nonTerminal)) {
            int savedIndex = currentIndex;
            boolean matched = true;


            for (String symbol : production.trim().split(" ")) {
                if (symbol.equals("ε")) {
                    continue;
                } else if (Character.isUpperCase(symbol.charAt(0))) {
                    matched = match(symbol);
                } else {
                    matched = matchTerminal(symbol);
                }

                if (!matched) {
                    break;
                }
            }

            if (matched) {
                return true;
            }


            currentIndex = savedIndex;
        }
        return false;
    }

    private boolean matchTerminal(String terminal) {
        if (currentIndex < tokens.size() && tokens.get(currentIndex).equals(terminal)) {
            currentIndex++;
            return true;
        }
        return false;
    }

    private List<String> tokenize(String input) {

        List<String> tokens = new ArrayList<>();
        String[] parts = input.split("\\s+");
        for (String part : parts) {
            if (part.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add("id");
            } else {
                tokens.add(part);
            }
        }
        return tokens;
    }
}
