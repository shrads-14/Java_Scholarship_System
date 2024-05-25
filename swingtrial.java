package mydatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class swingtrial {

    private JFrame frame;
    private JTextField txtManagementSystem;
    private JTextField clickhere;
    private JTextField nametext;
    private JTextField rolltext;
    private JTextField classtext;
    private JLabel namelabel;
    private JLabel rolllabel;
    private JLabel classlabel;
    private JButton submitButton;
    private JTextField displaymessage; // Changed to JTextField

    private boolean scholarshipGiven = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    swingtrial window = new swingtrial();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public swingtrial() {
        initialize();
    }

    private void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!");
            System.exit(1); 
        }

        frame = new JFrame();
        frame.getContentPane().setBackground(new Color(230, 253, 255));
        frame.setBounds(0, 177, 1544, 876);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        String imagePath = "C:\\Users\\shrad\\Downloads\\image-removebg-preview (31).png";
        ImageIcon logoIcon = new ImageIcon(imagePath);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBackground(new Color(232, 253, 255));
        logoLabel.setBounds(10, -23, 473, 511);
        frame.getContentPane().add(logoLabel);

        txtManagementSystem = new JTextField();
        txtManagementSystem.setBounds(192, 42, 1328, 73);
        txtManagementSystem.setBackground(new Color(255, 132, 193));
        txtManagementSystem.setFont(new Font("Trebuchet MS", Font.BOLD, 35));
        txtManagementSystem.setHorizontalAlignment(SwingConstants.CENTER);
        txtManagementSystem.setText("MANAGEMENT SYSTEM");
        frame.getContentPane().add(txtManagementSystem);
        txtManagementSystem.setColumns(10);

        clickhere = new JTextField();
        clickhere.setBounds(720, 177, 482, 30);
        clickhere.setForeground(new Color(0, 0, 0));
        clickhere.setBackground(new Color(230, 253, 255));
        clickhere.setFont(new Font("Tahoma", Font.PLAIN, 15));
        clickhere.setText("<- Click here to enter your details and check for scholarship availment");
        clickhere.setEditable(false);
        frame.getContentPane().add(clickhere);
        clickhere.setBorder(null);
        clickhere.setColumns(10);

        namelabel = new JLabel("Student Name");
        namelabel.setBounds(585, 331, 200, 25);
        namelabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
        namelabel.setVisible(false);
        frame.getContentPane().add(namelabel);

        nametext = new JTextField();
        nametext.setBounds(795, 331, 200, 30);
        nametext.setFont(new Font("Tahoma", Font.PLAIN, 20));
        nametext.setVisible(false);
        frame.getContentPane().add(nametext);

        rolllabel = new JLabel("Roll Number");
        rolllabel.setBounds(585, 381, 200, 25);
        rolllabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
        frame.getContentPane().add(rolllabel);

        rolltext = new JTextField();
        rolltext.setBounds(795, 381, 200, 30);
        rolltext.setFont(new Font("Tahoma", Font.PLAIN, 20));
        frame.getContentPane().add(rolltext);

        classlabel = new JLabel("Class");
        classlabel.setBounds(585, 431, 200, 25);
        classlabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
        classlabel.setVisible(false);
        frame.getContentPane().add(classlabel);
        rolllabel.setVisible(false);
        rolltext.setVisible(false);

        classtext = new JTextField();
        classtext.setBounds(795, 431, 200, 30);
        classtext.setFont(new Font("Tahoma", Font.PLAIN, 20));
        classtext.setVisible(false);
        frame.getContentPane().add(classtext);

        JButton scholarshipButton = new JButton("SCHOLARSHIP");
        scholarshipButton.setBackground(new Color(176, 224, 230));
        scholarshipButton.setBounds(376, 156, 292, 63);
        scholarshipButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
        scholarshipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show student details entry fields
                namelabel.setVisible(true);
                nametext.setVisible(true);
                rolllabel.setVisible(true);
                rolltext.setVisible(true);
                classlabel.setVisible(true);
                classtext.setVisible(true);
                submitButton.setVisible(true);

                // Fetch student details based on roll number
                String roll = rolltext.getText().trim();
                if (!roll.isEmpty()) {
                    fetchStudentDetails(roll);
                }
            }
        });

        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(176, 224, 230));
        submitButton.setBounds(682, 556, 140, 40);
        submitButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
        submitButton.setVisible(false);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nametext.getText();
                    String roll = rolltext.getText();
                    String stuclass = classtext.getText();

                    // Check for insufficient details
                    if (name.isEmpty() || roll.isEmpty() || stuclass.isEmpty()) {
                        displayMessage("Insufficient details! Please enter all fields.");
                        displaymessage.setVisible(true);
                        return; // Exit early if details are insufficient
                    }

                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdetails", "root", "Shraddha-1401");

                    // Fetch student's marks from the students table
                    int marks = fetchStudentMarks(roll);

                    if (marks == -1) {
                        displayMessage("Student not found!"); // Student details not found in the database
                    } else if (marks < 75) {
                        displayMessage("Sorry, you are not eligible for a scholarship (Marks < 75).");
                    } else {
                        // Continue with scholarship application process
                        // Check if scholarship already given to this student
                        if (scholarshipGiven) {
                            displayMessage("Scholarship already awarded for this student.");
                        } else {
                            // Check if the student's name already exists in scholarship application
                            boolean isAlreadyApplied = checkExistingApplication(name, connection);

                            if (isAlreadyApplied) {
                                displayMessage("Application already accessed. Scholarship given.");
                            } else {
                                // Insert student details into scholarship application table
                                String insertQuery = "INSERT INTO scholarshipapplication (studentname, roll_number, class) VALUES (?, ?, ?)";
                                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                                insertStatement.setString(1, name);
                                insertStatement.setString(2, roll);
                                insertStatement.setString(3, stuclass);
                                insertStatement.executeUpdate();

                                int scholarshipAmount = determineScholarshipAmount(marks);

                                // Fetch student's fees from the students table
                                String fetchQuery = "SELECT fees FROM students WHERE rollnumber = ?";
                                PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
                                fetchStatement.setString(1, roll);
                                ResultSet resultSet = fetchStatement.executeQuery();

                                if (resultSet.next()) {
                                    int fees = resultSet.getInt("fees");
                                    int updatedFees = fees - scholarshipAmount;

                                    // Update database with new fees
                                    String updateQuery = "UPDATE students SET fees = ? WHERE rollnumber = ?";
                                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                                    updateStatement.setInt(1, updatedFees);
                                    updateStatement.setString(2, roll);
                                    updateStatement.executeUpdate();

                                    displayMessage("CONGRATULATIONS! Scholarship applied successfully, New fees: Rs." + updatedFees);
                                    scholarshipGiven = true; // Mark scholarship as awarded
                                } else {
                                    displayMessage("Student not found!");
                                }

                                resultSet.close();
                                fetchStatement.close();
                                insertStatement.close();
                            }
                        }
                    }
                    connection.close();

                    // Make displaymessage visible after processing
                    displaymessage.setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    displayMessage("Error processing request: " + ex.getMessage());
                    // Make displaymessage visible to show error message
                    displaymessage.setVisible(true);
                }
            }
        });

        frame.getContentPane().add(submitButton);
        frame.getContentPane().add(scholarshipButton);

        displaymessage = new JTextField();
        displaymessage.setFont(new Font("Tahoma", Font.PLAIN, 35));
        displaymessage.setHorizontalAlignment(SwingConstants.CENTER);
        displaymessage.setBackground(new Color(250, 250, 210));
        displaymessage.setBounds(178, 662, 1232, 80);
        displaymessage.setEditable(false); // Make the text field non-editable
        displaymessage.setVisible(false); // Initially set to invisible
        frame.getContentPane().add(displaymessage);
        displaymessage.setColumns(10);
    }

    private void fetchStudentDetails(String roll) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdetails", "root", "Shraddha-1401");
            String fetchQuery = "SELECT studentname, class FROM students WHERE rollnumber = ?";
            PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
            fetchStatement.setString(1, roll);
            ResultSet resultSet = fetchStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("studentname");
                String stuclass = resultSet.getString("class");
                nametext.setText(name);
                classtext.setText(stuclass);
            } else {
                displayMessage("Record not found!");
            }

            resultSet.close();
            fetchStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            displayMessage("Error fetching student details: " + ex.getMessage());
            displaymessage.setVisible(true); // Make displaymessage visible to show error message
        }
    }

    private int fetchStudentMarks(String roll) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdetails", "root", "Shraddha-1401");
            String fetchMarksQuery = "SELECT marks FROM students WHERE rollnumber = ?";
            PreparedStatement marksStatement = connection.prepareStatement(fetchMarksQuery);
            marksStatement.setString(1, roll);
            ResultSet marksResult = marksStatement.executeQuery();

            int marks = -1; // Default value indicating student not found
            if (marksResult.next()) {
                marks = marksResult.getInt("marks");
            }

            marksResult.close();
            marksStatement.close();
            connection.close();

            return marks;
        } catch (SQLException ex) {
            ex.printStackTrace();
            displayMessage("Error fetching student marks: " + ex.getMessage());
            displaymessage.setVisible(true); // Make displaymessage visible to show error message
            return -1; // Return -1 for error case
        }
    }

    private int determineScholarshipAmount(int marks) {
        if (marks >= 95) {
            return 30000;
        } else if (marks >= 90) {
            return 25000;
        } else if (marks >= 85) {
            return 20000;
        } else if (marks >= 80) {
            return 15000;
        } else if (marks >= 75) {
            return 10000;
        }
        return 0;
    }

    private boolean checkExistingApplication(String name, Connection connection) throws SQLException {
        String checkQuery = "SELECT * FROM scholarshipapplication WHERE studentname = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
        checkStatement.setString(1, name);
        ResultSet resultSet = checkStatement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        checkStatement.close();
        return exists;
    }

    private void displayMessage(String message) {
        displaymessage.setText(message);
    }
}