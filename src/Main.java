import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Main extends JFrame {
    private final JTextField inputField = new JTextField(30);
    private final JButton parseButton = new JButton("Parse");
    private final JButton clearButton = new JButton("Clear");
    private final JTextArea outputArea = new JTextArea();
    private final JScrollPane scrollPane = new JScrollPane(outputArea);

    public Main() {
        setTitle("Arithmetic Expression Parser");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        
        JPanel topPanel = new JPanel(new FlowLayout());

        topPanel.add(new JLabel("Enter Expression:"));
        topPanel.add(inputField);
        topPanel.add(parseButton);
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);

        
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(scrollPane, BorderLayout.CENTER);

        
        parseButton.addActionListener(this::parseInput);
        clearButton.addActionListener(e -> {
            inputField.setText("");
            outputArea.setText("");
        });
    }

    private void parseInput(ActionEvent e) {
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
            Main parserUI = new Main();
            parserUI.setVisible(true);
        });
    }
}
