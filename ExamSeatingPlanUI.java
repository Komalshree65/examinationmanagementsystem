import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class ExamSeatingPlanUI extends JFrame {
    private JTextField inputField;
    private JTextField numRowsField;
    private JTextField numColsField;
    private JTextArea outputArea;

    public ExamSeatingPlanUI() {
        setTitle("Exam Seating Plan Generator");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(new JLabel("Input File:"));
        inputField = new JTextField();
        inputPanel.add(inputField);
        inputPanel.add(new JLabel("Number of Rows:"));
        numRowsField = new JTextField();
        inputPanel.add(numRowsField);
        inputPanel.add(new JLabel("Number of Columns:"));
        numColsField = new JTextField();
        inputPanel.add(numColsField);

        // Output panel
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        outputPanel.add(new JLabel("Seating Plan:"), BorderLayout.NORTH);
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton generateButton = new JButton("Generate Seating Plan");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputFile = inputField.getText();
                int numRows = getIntInput(numRowsField);
                int numCols = getIntInput(numColsField);
                List<ExamStudent> students = readInputFile(inputFile);
                if (students == null || numRows <= 0 || numCols <= 0) {
                    outputArea.setText("Invalid input.");
                    return;
                }
                Collections.shuffle(students);
                String[][] seatingPlan = generateSeatingPlan(students, numRows, numCols);
                boolean hasConsecutive = hasConsecutiveStudents(seatingPlan);
                if (hasConsecutive) {
                    outputArea.setText("Unable to generate seating plan with no consecutive students of the same course.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < numRows; i++) {
                        for (int j = 0; j < numCols; j++) {
                            sb.append(seatingPlan[i][j]).append("\t");
                        }
                        sb.append("\n");
                    }
                    outputArea.setText(sb.toString());
                }
            }
        });
        buttonPanel.add(generateButton);

        // Add panels to frame
        add(inputPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String[][] generateSeatingPlan(List<ExamStudent> students, int numRows, int numCols) {
        String[][] seatingPlan = new String[numRows][numCols];
        int index = 0;
        // Assign students to seats in row-major order
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (index < students.size()) {
                    ExamStudent student = students.get(index);
                    seatingPlan[i][j] = student.getRegistrationNumber() + " (" + student.getCourseCode() + ")";
                    index++;
                } else {
                    seatingPlan[i][j] = "";
                }
            }
        }
        return seatingPlan;
    }

    private boolean hasConsecutiveStudents(String[][] seatingPlan) {
        int numRows = seatingPlan.length;
        int numCols = seatingPlan[0].length;
        // Check for consecutive students in rows
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols - 1; j++) {
                String curr = seatingPlan[i][j];
                String next = seatingPlan[i][j+1];
                if (!curr.equals("") && curr.equals(next)) {
                    return true;
                }
            }
        }
        // Check for consecutive students in columns
        for (int i = 0; i < numRows - 1; i++) {
            for (int j = 0; j < numCols; j++) {
                String curr = seatingPlan[i][j];
                String next = seatingPlan[i+1][j];
                if (!curr.equals("") && curr.equals(next)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<ExamStudent> readInputFile(String inputFile) {
        List<ExamStudent> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    return null;
                }
                students.add(new ExamStudent(parts[0], parts[1]));
            }
        } catch (IOException e) {
            return null;
        }
        return students;
    }

    private int getIntInput(JTextField field) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public class ExamStudent {
        private String registrationNumber;
        private String courseCode;

        public ExamStudent(String registrationNumber, String courseCode) {
            this.registrationNumber = registrationNumber;
            this.courseCode = courseCode;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public String getCourseCode() {
            return courseCode;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExamSeatingPlanUI ui = new ExamSeatingPlanUI();
                ui.setVisible(true);
            }
        });
    }
}