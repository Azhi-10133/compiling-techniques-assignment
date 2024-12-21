import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Parser extends JFrame {

    enum TokenType {
        ID, PLUS, MULT, LPAREN, RPAREN, EOF
    }


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


    static class Tokenizer {
        private final String input;
        private int pos = 0;
        private final int length;

        Tokenizer(String input) {

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
                    case '$':
                        tokens.add(new Token(TokenType.EOF, "$"));
                        pos++;
                        break;
                    default:
                        if (Character.isLetter(current)) {
                            StringBuilder sb = new StringBuilder();
                            while (pos < length && Character.isLetter(input.charAt(pos))) {
                                sb.append(input.charAt(pos));
                                pos++;
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

        }

        private void F() throws ParseException {
            if (match(TokenType.LPAREN)) {
                E();
                expect(TokenType.RPAREN);
            } else if (match(TokenType.ID)) {

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


        static class ParseException extends Exception {
            ParseException(String message) {
                super(message);
            }
        }
    }


    static class BottomUpParser {
        private final List<Token> tokens;

        BottomUpParser(List<Token> tokens) {
            this.tokens = tokens;
        }

        public boolean parse() {
            Stack<String> stack = new Stack<>();
            int pos = 0;

            while (pos < tokens.size()) {
                Token currentToken = tokens.get(pos);
                if (currentToken.type == TokenType.EOF) {

                    pos++;
                    continue;
                }
                stack.push(currentToken.toString());
                pos++;

                boolean reduced;
                do {
                    reduced = reduce(stack);
                } while (reduced);
            }


            while (reduce(stack)) {

            }

            return stack.size() == 1 && stack.peek().equals("E");
        }

        private boolean reduce(Stack<String> stack) {

            List<String[]> reductions = Arrays.asList(
                    new String[]{"E", "+", "T"},
                    new String[]{"T", "*", "F"},
                    new String[]{"(", "E", ")"},
                    new String[]{"id"},
                    new String[]{"F"},
                    new String[]{"T"}
            );

            for (String[] rule : reductions) {
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
            for (int i = 0; i < rule.length; i++) {
                stack.pop();
            }

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
                    break;
            }
        }
    }


    private JTextField inputField;
    private JButton parseButton, clearButton;
    private JTextArea outputArea;
    private JScrollPane scrollPane;

    public Parser() {
        setTitle("Arithmetic Expression Parser");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setLocationRelativeTo(null);
    }

    private void initUI() {

        setLayout(new BorderLayout());

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

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

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

        outputArea.setText("");

        Tokenizer tokenizer = new Tokenizer(input);
        List<Token> tokens;
        try {
            tokens = tokenizer.tokenize();
            outputArea.append("Tokens: " + tokens + "\n\n");
        } catch (RuntimeException ex) {
            outputArea.append("Tokenizer Error: " + ex.getMessage() + "\n");
            return;
        }

        TopDownParser topDownParser = new TopDownParser(tokens);
        boolean topDownResult;
        try {
            topDownResult = topDownParser.parse();
            outputArea.append("Top-Down Parsing Result: " + (topDownResult ? "Accepted" : "Rejected") + "\n");
        } catch (RuntimeException ex) {
            outputArea.append(ex.getMessage() + "\n");
            topDownResult = false;
        }

        BottomUpParser bottomUpParser = new BottomUpParser(tokens);
        boolean bottomUpResult;
        try {
            bottomUpResult = bottomUpParser.parse();
            outputArea.append("Bottom-Up Parsing Result: " + (bottomUpResult ? "Accepted" : "Rejected") + "\n");
        } catch (RuntimeException ex) {
            outputArea.append("Bottom-Up Parsing Error: " + ex.getMessage() + "\n");
            bottomUpResult = false;
        }

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
