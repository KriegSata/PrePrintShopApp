package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;

public class CustomerRegistrationForm extends JFrame {
    private final SystemManager systemManager; // Reference to SystemManager
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField schoolIdField;
    private JTextField courseField; // New field for course
    private JTextField sectionField; // New field for section
    private JTextField usernameField;
    private JPasswordField passwordField;

    public CustomerRegistrationForm(SystemManager systemManager) {
        this.systemManager = systemManager;
        setTitle("Customer Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false); // Lock window resizing

        setupComponents();
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");
    }

    private void handleSubmit() {
        try {
            // Get and trim all input values
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String schoolId = schoolIdField.getText().trim();
            String course = courseField.getText().trim();
            String section = sectionField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Validate all required fields
            StringBuilder errors = new StringBuilder();
            if (name.isEmpty()) errors.append("Name is required\n");
            if (email.isEmpty()) errors.append("Email is required\n");
            if (phone.isEmpty()) errors.append("Phone number is required\n");
            if (schoolId.isEmpty()) errors.append("School ID is required\n");
            if (course.isEmpty()) errors.append("Course is required\n");
            if (section.isEmpty()) errors.append("Section is required\n");
            if (username.isEmpty()) errors.append("Username is required\n");
            if (password.isEmpty()) errors.append("Password is required\n");

            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this,
                    errors.toString(),
                    "Missing Required Fields",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email format
            if (!KaelLib.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return;
            }

            // Validate phone number format
            if (!KaelLib.isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid 10-digit phone number",
                    "Invalid Phone Number",
                    JOptionPane.ERROR_MESSAGE);
                phoneField.requestFocus();
                return;
            }

            // Validate password strength (minimum 6 characters)
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long",
                    "Invalid Password",
                    JOptionPane.ERROR_MESSAGE);
                passwordField.requestFocus();
                return;
            }

            // Try to register the user
            try {
                systemManager.registerUser(name, schoolId, email, phone, course, section, username, password, "customer");
                JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nPlease wait for admin approval before logging in.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

                // Return to login page
                goBack();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                    "Registration failed: " + e.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "An unexpected error occurred: " + e.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Customer Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Add form fields with improved styling
        addFormField(mainPanel, "Name:", nameField = createStyledTextField(), gbc, 1);
        addFormField(mainPanel, "Email:", emailField = createStyledTextField(), gbc, 2);
        addFormField(mainPanel, "Phone:", phoneField = createStyledTextField(), gbc, 3);
        addFormField(mainPanel, "School ID:", schoolIdField = createStyledTextField(), gbc, 4);
        addFormField(mainPanel, "Course:", courseField = createStyledTextField(), gbc, 5);
        addFormField(mainPanel, "Section:", sectionField = createStyledTextField(), gbc, 6);
        addFormField(mainPanel, "Username:", usernameField = createStyledTextField(), gbc, 7);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        addFormField(mainPanel, "Password:", passwordField, gbc, 8);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton submitButton = new JButton("Submit");
        submitButton.setForeground(Color.BLACK);
        submitButton.setBackground(new Color(0, 120, 212));
        submitButton.addActionListener(e -> handleSubmit());

        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(240, 240, 240));
        backButton.addActionListener(e -> goBack());

        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        // Add mainPanel to a scroll pane in case the form gets too long
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    private void styleTextField(JComponent field) {
        field.setPreferredSize(new Dimension(250, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 212)),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
            ));
        }
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void goBack() {
        this.dispose();
        new LoginPage(systemManager).setVisible(true);
    }
}
