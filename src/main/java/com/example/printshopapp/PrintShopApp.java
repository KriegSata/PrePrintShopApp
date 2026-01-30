package com.example.printshopapp;

import javax.swing.*;

public class PrintShopApp {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Use a bold font for all buttons
            UIManager.put("Button.font", UIManager.getFont("Button.font").deriveFont(java.awt.Font.BOLD));
        } catch (Exception e) {
        }

        // Run the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SystemManager systemManager = new SystemManager();
            LoginPage loginPage = new LoginPage(systemManager);
            loginPage.setVisible(true);
        });
    }
}
