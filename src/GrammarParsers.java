import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GrammarParsers extends JFrame {

    private JTextArea grammarInputArea;
    private JTextField inputStringField;
    private JButton parseButton;
    private JComboBox<String> parserTypeComboBox;
    private JLabel resultLabel;

    public GrammarParsers() {

        setTitle("Grammar Parsers");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 1, 5, 5));


        JLabel grammarLabel = new JLabel("Enter Grammar Rules (e.g., E->E+T|T):");
        grammarInputArea = new JTextArea(5, 30);
        grammarInputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));


        JLabel parserTypeLabel = new JLabel("Select Parser Type:");
        parserTypeComboBox = new JComboBox<>(new String[]{"Top-Down Parser", "Bottom-Up Parser"});


        JLabel inputStringLabel = new JLabel("Enter Input String:");
        inputStringField = new JTextField(20);


        parseButton = new JButton("Parse");


        resultLabel = new JLabel("Result: ");


        inputPanel.add(grammarLabel);
        inputPanel.add(new JScrollPane(grammarInputArea));
        inputPanel.add(parserTypeLabel);
        inputPanel.add(parserTypeComboBox);
        inputPanel.add(inputStringLabel);
        inputPanel.add(inputStringField);


        add(inputPanel, BorderLayout.CENTER);
        add(parseButton, BorderLayout.SOUTH);
        add(resultLabel, BorderLayout.NORTH);


        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseInput();
            }
        });
    }


    private void parseInput() {

        String grammarText = grammarInputArea.getText().trim();
        String inputString = inputStringField.getText().trim();
        int parserChoice = parserTypeComboBox.getSelectedIndex() + 1;


        if (grammarText.isEmpty()) {
            resultLabel.setText("Result: Error - Grammar rules cannot be empty!");
            return;
        }
        if (inputString.isEmpty()) {
            resultLabel.setText("Result: Error - Input string cannot be empty!");
            return;
        }


        List<String> grammarRules = new ArrayList<>();
        for (String line : grammarText.split("\n")) {
            grammarRules.add(line.replaceAll("\\s*->\\s*", "->").replaceAll("\\s*\\|\\s*", "|"));
        }

        boolean parsingResult = false;

        try {
            if (parserChoice == 1) {

                TopDownParser topDownParser = new TopDownParser(grammarRules);
                parsingResult = topDownParser.parse(inputString);
            } else if (parserChoice == 2) {

                BottomUpParser bottomUpParser = new BottomUpParser(grammarRules);
                parsingResult = bottomUpParser.parse(inputString);
            }


            resultLabel.setText("Result: " + (parsingResult ? "Accepted" : "Rejected"));
        } catch (Exception ex) {

            resultLabel.setText("Error: " + ex.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GrammarParsers gui = new GrammarParsers();
            gui.setVisible(true);
        });
    }
}
