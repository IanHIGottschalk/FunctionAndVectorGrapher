import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Function & Vector Grapher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 750);
            frame.setLocationRelativeTo(null);

            GraphPanel graphPanel = new GraphPanel();

            // Unified input field
            JTextField inputField = new JTextField();
            inputField.setEditable(false);
            inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));

            // Buttons
            JButton setFunctionBtn = new JButton("Set Function");
            JButton setVectorBtn = new JButton("Set Vector");

            setFunctionBtn.addActionListener(e -> {
                graphPanel.setFunction(inputField.getText());
                graphPanel.repaint();
            });

            setVectorBtn.addActionListener(e -> {
                graphPanel.setVector(inputField.getText());
                graphPanel.repaint();
            });

            // Keyboard panel
            JPanel keyboardPanel = new JPanel(new GridLayout(5, 6, 5, 5));
            String[] keys = {
                    "x", "(", ")", "^", "sqrt", "CLR",
                    "7", "8", "9", "/", "sin", "cos",
                    "4", "5", "6", "*", "tan", "log",
                    "1", "2", "3", "-", ".", "+",
                    "0", ",", " ", " ", "←", " "
            };

            for (String key : keys) {
                JButton btn = new JButton(key.trim());
                if (key.isBlank()) {
                    btn.setEnabled(false);
                } else {
                    btn.addActionListener(e -> {
                        if (key.equals("CLR")) {
                            inputField.setText("");
                        } else if (key.equals("←")) {
                            String text = inputField.getText();
                            if (!text.isEmpty()) {
                                inputField.setText(text.substring(0, text.length() - 1));
                            }
                        } else {
                            inputField.setText(inputField.getText() + key);
                        }
                    });
                }
                keyboardPanel.add(btn);
            }

            // Layout
            frame.setLayout(new BorderLayout());
            frame.add(inputField, BorderLayout.NORTH);
            frame.add(graphPanel, BorderLayout.CENTER);

            JPanel southPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            buttonPanel.add(setFunctionBtn);
            buttonPanel.add(setVectorBtn);

            southPanel.add(keyboardPanel, BorderLayout.CENTER);
            southPanel.add(buttonPanel, BorderLayout.SOUTH);

            frame.add(southPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}
