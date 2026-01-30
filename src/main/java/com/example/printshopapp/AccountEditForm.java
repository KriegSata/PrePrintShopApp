package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccountEditForm extends JFrame {
    private final SystemManager systemManager;
    private final User currentUser;
    private JTextField nameField, emailField, phoneField, studentIdField, courseField, sectionField, usernameField;
    private boolean isEditing = false;

    public AccountEditForm(SystemManager systemManager) {
        this.systemManager = systemManager;
        this.currentUser = systemManager.getCurrentUser();

        setTitle("Account Edit");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setupUI();
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;

        // Title
        JLabel titleLabel = new JLabel("Account Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Create fields and assign to instance variables
        nameField = createTextField(currentUser.getName());
        createFieldRow(mainPanel, "Name:", nameField, 1);
        emailField = createTextField(currentUser.getEmail());
        createFieldRow(mainPanel, "Email:", emailField, 2);
        phoneField = createTextField(currentUser.getContactNumber());
        createFieldRow(mainPanel, "Phone:", phoneField, 3);
        studentIdField = createTextField(currentUser.getStudentId());
        createFieldRow(mainPanel, "Student ID:", studentIdField, 4);
        courseField = createTextField(currentUser.getCourse());
        createFieldRow(mainPanel, "Course:", courseField, 5);
        sectionField = createTextField(currentUser.getSection());
        createFieldRow(mainPanel, "Section:", sectionField, 6);
        usernameField = createTextField(currentUser.getUsername());
        createFieldRow(mainPanel, "Username:", usernameField, 7);

        // Password change button
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> openPasswordEditForm());
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(changePasswordButton, gbc);

        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            new CustomerOptionsWindow(systemManager).setVisible(true);
            dispose();
        });
        gbc.gridy = 9;
        mainPanel.add(backButton, gbc);

        add(mainPanel);
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setBackground(Color.LIGHT_GRAY);
        return field;
    }

    private void createFieldRow(JPanel panel, String labelText, JTextField field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;

        // Label
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        panel.add(label, gbc);

        // TextField
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(field, gbc);

        // Edit button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> toggleEdit(field, editButton));
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        panel.add(editButton, gbc);
    }

    private void toggleEdit(JTextField field, JButton button) {
        if (!isEditing) {
            field.setEditable(true);
            field.setBackground(Color.WHITE);
            button.setText("Save");
            isEditing = true;
        } else {
            field.setEditable(false);
            field.setBackground(Color.LIGHT_GRAY);
            button.setText("Edit");
            isEditing = false;
            updateUserField(field);
        }
    }

    private void updateUserField(JTextField field) {
        String newValue = field.getText().trim();
        if (newValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Field cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the corresponding field in the user object
        if (field == nameField) currentUser.setName(newValue);
        else if (field == emailField) currentUser.setEmail(newValue);
        else if (field == phoneField) currentUser.setContactNumber(newValue);
        else if (field == studentIdField) currentUser.setStudentId(newValue);
        else if (field == courseField) currentUser.setCourse(newValue);
        else if (field == sectionField) currentUser.setSection(newValue);
        else if (field == usernameField) currentUser.setUsername(newValue);

        // Update the user in the system
        systemManager.updateUser(currentUser);
    }

    private void openPasswordEditForm() {
        new PasswordEditForm(systemManager).setVisible(true);
    }
}
