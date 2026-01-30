package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;

public class PasswordEditForm extends JFrame {
    private final SystemManager systemManager;
    private final User currentUser;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;

    public PasswordEditForm(SystemManager systemManager) {
        this.systemManager = systemManager;
        this.currentUser = systemManager.getCurrentUser();

        setTitle("Change Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
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

        // Title
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Current Password
        JLabel currentLabel = new JLabel("Current Password:");
        currentLabel.setForeground(new Color(44, 62, 80));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(currentLabel, gbc);

        currentPasswordField = new JPasswordField();
        gbc.gridy = 2;
        mainPanel.add(currentPasswordField, gbc);

        // New Password
        JLabel newLabel = new JLabel("New Password:");
        newLabel.setForeground(new Color(44, 62, 80));
        gbc.gridy = 3;
        mainPanel.add(newLabel, gbc);

        newPasswordField = new JPasswordField();
        gbc.gridy = 4;
        mainPanel.add(newPasswordField, gbc);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm New Password:");
        confirmLabel.setForeground(new Color(44, 62, 80));
        gbc.gridy = 5;
        mainPanel.add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField();
        gbc.gridy = 6;
        mainPanel.add(confirmPasswordField, gbc);

        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        mainPanel.add(errorLabel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> updatePassword());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 8;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void updatePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Verify current password
        if (!currentUser.getPassword().equals(currentPassword)) {
            errorLabel.setText("Current password is incorrect");
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("New passwords do not match");
            return;
        }

        // Update password
        currentUser.setPassword(newPassword);
        systemManager.updateUser(currentUser);

        JOptionPane.showMessageDialog(this,
            "Password updated successfully",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
