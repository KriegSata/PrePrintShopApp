package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.example.printshopapp.CustomerRegistrationForm;
import com.example.printshopapp.GuestPreOrderForm;

public class LoginPage extends JFrame {
    private final SystemManager systemManager; // Reference to SystemManager
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage(SystemManager systemManager) {
        this.systemManager = systemManager;
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false); // Lock window resizing

        setupComponents();
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");
    }

    private void setupComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);  // Make panel transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.BLACK);  // Ensure label is visible
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password Field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.BLACK);  // Ensure label is visible
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setForeground(Color.BLACK);
        loginButton.addActionListener(new LoginActionListener());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // "Not From the School?" Button
        JButton notFromSchoolButton = new JButton("Not From the School?");
        notFromSchoolButton.setForeground(Color.BLACK);
        notFromSchoolButton.addActionListener(event -> openOrderFormDirectly());
        gbc.gridy = 3;
        panel.add(notFromSchoolButton, gbc);

        // "Create an Account" Button
        JButton createAccountButton = new JButton("Create an Account");
        createAccountButton.setForeground(Color.BLACK);
        createAccountButton.addActionListener(event -> openCustomerRegistrationForm());
        gbc.gridy = 4;
        panel.add(createAccountButton, gbc);

        add(panel);
    }

    private void openOrderFormDirectly() {
        // Ensure non-school users always go through GuestPreOrderForm
        new GuestPreOrderForm(systemManager, this).setVisible(true);
        dispose();
    }

    private void openCustomerRegistrationForm() {
        // Open the CustomerRegistrationForm
        new CustomerRegistrationForm(systemManager).setVisible(true);
        dispose();
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Validate login
            User user = systemManager.validateLogin(username, password);
            if (user != null) {
                systemManager.logAction(user.getName(), "Logged in successfully");
                systemManager.setCurrentUser(user); // Set the current user after successful login

                LoginPage.this.dispose();
                if (user.getRole().equals("admin")) {
                    new AdminDashboard(systemManager).setVisible(true);
                } else if (user.getRole().equals("staff")) {
                    new StaffDashboard(systemManager, user).setVisible(true); // Pass both SystemManager and User
                } else {
                    new CustomerOptionsWindow(systemManager).setVisible(true);
                }
            } else {
                systemManager.logAction(username, "Failed login attempt");
                JOptionPane.showMessageDialog(LoginPage.this,
                    "Invalid username or password, or account not yet activated",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
