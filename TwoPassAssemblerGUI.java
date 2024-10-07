import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TwoPassAssemblerGUI extends JFrame {

    private JTextField inputFileField, optabFileField;
    private JTextArea intermediateArea, symtabArea, outputArea;
    private JButton assembleBtn, quitBtn;

    public TwoPassAssemblerGUI() {
        setTitle("Two-Pass Assembler");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        Font font = new Font("Arial", Font.PLAIN, 16);
        Color bgColor = new Color(245, 245, 245);
        Color buttonColor = new Color(255, 215, 0); // Light golden color

        // Input File Section
        JLabel inputLabel = new JLabel("Input File:");
        inputLabel.setFont(font);
        inputLabel.setBounds(400, 50, 120, 30);
        panel.add(inputLabel);

        inputFileField = new JTextField(30);
        inputFileField.setBounds(520, 50, 600, 30);
        panel.add(inputFileField);

        JButton browseInputBtn = new JButton("Browse");
        browseInputBtn.setFont(font);
        browseInputBtn.setBounds(1130, 50, 120, 30);
        browseInputBtn.addActionListener(e -> browseFile(inputFileField));
        panel.add(browseInputBtn);

        // Opcode Table File Section
        JLabel optabLabel = new JLabel("Opcode Table File:");
        optabLabel.setFont(font);
        optabLabel.setBounds(400, 100, 150, 30);
        panel.add(optabLabel);

        optabFileField = new JTextField(30);
        optabFileField.setBounds(550, 100, 600, 30);
        panel.add(optabFileField);

        JButton browseOptabBtn = new JButton("Browse");
        browseOptabBtn.setFont(font);
        browseOptabBtn.setBounds(1160, 100, 120, 30);
        browseOptabBtn.addActionListener(e -> browseFile(optabFileField));
        panel.add(browseOptabBtn);

        // Assemble Button
        assembleBtn = new JButton("Assemble");
        assembleBtn.setFont(new Font("Arial", Font.BOLD, 16));
        assembleBtn.setBackground(buttonColor);
        assembleBtn.setForeground(Color.BLACK);
        assembleBtn.setBounds(750, 150, 300, 40);
        assembleBtn.addActionListener(e -> runAssembler());
        panel.add(assembleBtn);

        // Intermediate File Output Section
        JLabel intermediateLabel = new JLabel("Intermediate File:");
        intermediateLabel.setFont(font);
        intermediateLabel.setBounds(50, 220, 250, 30);
        panel.add(intermediateLabel);

        intermediateArea = new JTextArea(8, 70);
        intermediateArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane intermediateScroll = new JScrollPane(intermediateArea);
        intermediateScroll.setBounds(50, 260, 900, 400);
        panel.add(intermediateScroll);

        // Symtab File Output Section
        JLabel symtabLabel = new JLabel("Symtab File:");
        symtabLabel.setFont(font);
        symtabLabel.setBounds(970, 220, 250, 30);
        panel.add(symtabLabel);

        symtabArea = new JTextArea(8, 70);
        symtabArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane symtabScroll = new JScrollPane(symtabArea);
        symtabScroll.setBounds(970, 260, 900, 400);
        panel.add(symtabScroll);

        // Object Code Output Section
        JLabel outputLabel = new JLabel("Object Code:");
        outputLabel.setFont(font);
        outputLabel.setBounds(50, 680, 250, 30);
        panel.add(outputLabel);

        outputArea = new JTextArea(7, 70);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBounds(50, 720, 1820, 200);
        panel.add(outputScroll);

        // Quit Button
        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        quitBtn.setBackground(Color.RED);
        quitBtn.setForeground(Color.WHITE);
        quitBtn.setBounds(1750, 950, 120, 40);
        quitBtn.addActionListener(e -> System.exit(0));
        panel.add(quitBtn);

        panel.setBackground(bgColor);
        return panel;
    }

    private void browseFile(JTextField field) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            field.setText(file.getPath());
        }
    }

    private void runAssembler() {
        String inputFile = inputFileField.getText();
        String optabFile = optabFileField.getText();

        if (inputFile.isEmpty() || optabFile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide both the input file and opcode table file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        assembleBtn.setEnabled(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                TwoPassAssembler assembler = new TwoPassAssembler(inputFile, optabFile);
                try {
                    assembler.loadOptab();
                    assembler.passOne();
                    assembler.passTwo();

                    displayFileContent("intermediate.txt", intermediateArea);
                    displayFileContent("symtab.txt", symtabArea);
                    displayFileContent("output.txt", outputArea);

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(TwoPassAssemblerGUI.this, "Error running the assembler: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            @Override
            protected void done() {
                assembleBtn.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void displayFileContent(String filename, JTextArea textArea) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        textArea.setText("");
        String line;
        while ((line = reader.readLine()) != null) {
            textArea.append(line + "\n");
        }
        reader.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TwoPassAssemblerGUI::new);
    }
}
