import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Parser extends JFrame {
    // Define token types
    enum TokenType {
        ID, PLUS, MULT, LPAREN, RPAREN, EOF
    }

    // Token class
    static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // Tokenizer class
    static class Tokenizer {
        private final String input;
        private int pos = 0;
        private final int length;

        Tokenizer(String input) {
            // Remove whitespace and append EOF symbol
            this.input = input.replaceAll("\\s+", "") + "$";
            this.length = this.input.length();
        }

        List<Token> tokenize() {
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
                    case 'i':
                        if (pos + 1 < length && input.charAt(pos + 1) == 'd') {
                            tokens.add(new Token(TokenType.ID, "id"));
                            pos += 2;
                        } else {
                            throw new RuntimeException("Invalid token at position " + pos);
                        }
                        break;
                    case '$':
                        tokens.add(new Token(TokenType.EOF, "$"));
                        pos++;
                        break;
                    default:
                        throw new RuntimeException("Unknown character: " + current + " at position " + pos);
                }
            }
            return tokens;
        }
    }

    // Grammar class (for reference, not directly used in parsers)
    static class Grammar {
        String[] rules = {
                "E -> E + T",
                "E -> T",
                "T -> T * F",
                "T -> F",
                "F -> ( E )",
                "F -> id"
        };
    }

    // Top-Down Parser (Recursive Descent)
    static class TopDownParser {
        private final List<Token> tokens;
        private int pos = 0;

        TopDownParser(List<Token> tokens) {
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
            // epsilon production
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
            // epsilon production
        }

        private void F() throws ParseException {
            if (match(TokenType.LPAREN)) {
                E();
                expect(TokenType.RPAREN);
            } else if (match(TokenType.ID)) {
                // matched 'id'
            } else {
                throw new ParseException("Expected 'id' or '(', found: " + peek());
            }
        }

        private boolean match(TokenType type) {
            if (pos < tokens.size() && tokens.get(pos).type == type) {
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
            if (pos < tokens.size()) {
                return tokens.get(pos);
            }
            return new Token(TokenType.EOF, "$");
        }

        // Custom exception for parse errors
        static class ParseException extends Exception {
            ParseException(String message) {
                super(message);
            }
        }
    }

    // Bottom-Up Parser (Shift-Reduce)
    static class BottomUpParser {
        private final List<Token> tokens;

        BottomUpParser(List<Token> tokens) {
            this.tokens = tokens;
        }

        public boolean parse() {
            Stack<String> stack = new Stack<>();
            int pos = 0;

            while (pos < tokens.size()) {
                stack.push(tokens.get(pos).toString());
                pos++;
                // Attempt to reduce after each shift
                boolean reduced;
                do {
                    reduced = reduce(stack);
                } while (reduced);
            }

            // After processing all tokens, try to reduce to the start symbol
            while (reduce(stack)) {
                // Keep reducing until no more reductions are possible
            }

            return stack.size() == 1 && stack.peek().equals("E");
        }

        private boolean reduce(Stack<String> stack) {
            // Define the possible reductions in order of priority
            List<String[]> reductions = Arrays.asList(
                    new String[]{"E", "+", "T"}, // E -> E + T
                    new String[]{"T", "*", "F"}, // T -> T * F
                    new String[]{"(", "E", ")"}, // F -> ( E )
                    new String[]{"id"},           // F -> id
                    new String[]{"F"},            // T -> F
                    new String[]{"T"}            // E -> T
                    // Removed new String[]{"E"} // Accepting E, to prevent redundant reductions
            );

            for (String[] rule : reductions) {
                if (canApplyReduction(stack, rule)) {
                    applyReduction(stack, rule);
                    return true; // A reduction was applied
                }
            }
            return false; // No reduction applied
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
            // Remove the symbols to be reduced
            for (int i = 0; i < rule.length; i++) {
                stack.pop();
            }

            // Determine the non-terminal to push based on the rule
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
                    // No action
                    break;
            }
        }
    }

    // GUI Components
    private JTextField inputField;
    private JButton parseButton, clearButton;
    private JTextArea outputArea;
    private JScrollPane scrollPane;

    public Parser() {
        setTitle("Arithmetic Expression Parser");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setLocationRelativeTo(null); // Center the window
    }

    private void initUI() {
        // Set layout manager
        setLayout(new BorderLayout());

        // Top panel for input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel inputLabel = new JLabel("Enter Expression:");
        inputField = new JTextField(30);

        parseButton = new JButton("Parse");
        clearButton = new JButton("Clear");

        topPanel.add(inputLabel);
        topPanel.add(inputField);
        topPanel.add(parseButton);
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);

        // Center panel for output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseInput();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputField.setText("");
                outputArea.setText("");
            }
        });
    }

    private void parseInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an expression to parse.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        outputArea.setText(""); // Clear previous output

        // Tokenization
        Tokenizer tokenizer = new Tokenizer(input);
        List<Token> tokens;
        try {
            tokens = tokenizer.tokenize();
            outputArea.append("Tokens: " + tokens + "\n\n");
        } catch (RuntimeException ex) {
            outputArea.append("Tokenizer Error: " + ex.getMessage() + "\n");
            return;
        }

        // Top-Down Parsing
        TopDownParser topDownParser = new TopDownParser(tokens);
        boolean topDownResult;
        try {
            topDownResult = topDownParser.parse();
            outputArea.append("Top-Down Parsing Result: " + (topDownResult ? "Accepted" : "Rejected") + "\n");
        } catch (RuntimeException ex) {
            outputArea.append(ex.getMessage() + "\n");
            topDownResult = false;
        }

        // Bottom-Up Parsing
        BottomUpParser bottomUpParser = new BottomUpParser(tokens);
        boolean bottomUpResult;
        try {
            bottomUpResult = bottomUpParser.parse();
            outputArea.append("Bottom-Up Parsing Result: " + (bottomUpResult ? "Accepted" : "Rejected") + "\n");
        } catch (RuntimeException ex) {
            outputArea.append("Bottom-Up Parsing Error: " + ex.getMessage() + "\n");
            bottomUpResult = false;
        }

        // Final Result
        if (topDownResult && bottomUpResult) {
            outputArea.append("\nOverall Result: Accepted by both parsers.");
        } else {
            outputArea.append("\nOverall Result: Rejected by one or both parsers.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Parser parserUI = new Parser();
            parserUI.setVisible(true);
        });
    }
}
