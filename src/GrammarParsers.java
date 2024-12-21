import java.util.*;

public class GrammarParsers {

    public GrammarParsers() {

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Welcome to Grammar Parsers!");
        System.out.println("1. Top-Down Parser\n2. Bottom-Up Parser");


        int choice = scanner.nextInt();
        scanner.nextLine();


        System.out.println("Enter grammar rules (e.g., S->aSb|ε). End with an empty line:");
        List<String> grammarRules = new ArrayList<>();

        while (true) {
            String rule = scanner.nextLine().trim();
            if (rule.isEmpty()) break;


            grammarRules.add(rule.replaceAll("\\s*->\\s*", "->").replaceAll("\\s*\\|\\s*", "|"));
        }


        System.out.println("Enter input string:");
        String inputString = scanner.nextLine().trim();


        if (grammarRules.isEmpty()) {
            System.out.println("Error: Grammar rules cannot be empty!");
            return;
        }
        if (inputString.isEmpty()) {
            System.out.println("Error: Input string cannot be empty!");
            return;
        }


        List<String> tokens = tokenize(inputString);


        boolean parsingResult = false;


        if (choice == 1) {
            TopDownParser topDownParser = new TopDownParser(grammarRules);
            parsingResult = topDownParser.parse(String.join(" ", tokens));
        } else if (choice == 2) {
            BottomUpParser bottomUpParser = new BottomUpParser();
            parsingResult = bottomUpParser.parse(String.join(" ", tokens));
        } else {
            System.out.println("Invalid choice! Please select 1 (Top-Down) or 2 (Bottom-Up).");
            return;
        }


        System.out.println("Parsing result: " + (parsingResult ? "Accepted" : "Rejected"));
    }


    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();


        StringTokenizer tokenizer = new StringTokenizer(input, "+*()", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();

            if (token.isEmpty()) {
                continue;
            }


            if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add("id");
            } else {
                tokens.add(token);
            }
        }
        return tokens;
    }
}
