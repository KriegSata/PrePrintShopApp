package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.border.TitledBorder;

public class NonSchoolUserForm extends JFrame {
    private final SystemManager systemManager; // Reference to SystemManager
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    // File upload fields
    private File orderReceiptFile;
    private File gcashReceiptFile;
    private File[] printFiles;
    private JTextField orderReceiptField;
    private JTextField gcashReceiptField;
    private JTextField printFilesField;

    public NonSchoolUserForm(SystemManager systemManager) {
        this.systemManager = systemManager; // Store the reference
        setTitle("Non-School User Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 750); // Increased size for better visibility
        setLocationRelativeTo(null);
        setResizable(false); // Lock window resizing

        setupComponents();
    }

    private void setupComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Phone Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Contact Number:"), gbc);

        phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // Add price list panel for non-school users
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                        "Price List",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16)
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pricePanel.setBackground(new Color(255, 255, 255));
        pricePanel.setOpaque(true);
        // Paper types and prices
        JLabel a4Header = new JLabel("A4 Paper");
        a4Header.setFont(new Font("Arial", Font.BOLD, 14));
        a4Header.setForeground(new Color(44, 62, 80));
        a4Header.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(a4Header);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "⚫ Black & White:", "₱3.00/page");
        addPriceRow(pricePanel, "⚫ Colored:", "₱5.00/page");
        pricePanel.add(Box.createVerticalStrut(15));
        JLabel longHeader = new JLabel("Long Bond Paper");
        longHeader.setFont(new Font("Arial", Font.BOLD, 14));
        longHeader.setForeground(new Color(44, 62, 80));
        longHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(longHeader);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "⚫ Black & White:", "₱3.00/page");
        addPriceRow(pricePanel, "⚫ Colored:", "₱5.00/page");
        pricePanel.add(Box.createVerticalStrut(20));
        JLabel qualityHeader = new JLabel("Paper Quality");
        qualityHeader.setFont(new Font("Arial", Font.BOLD, 14));
        qualityHeader.setForeground(new Color(44, 62, 80));
        qualityHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(qualityHeader);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "Standard:", "+₱0.00/page");
        addPriceRow(pricePanel, "Premium:", "+₱1.00/page");
        addPriceRow(pricePanel, "Ultra Premium:", "+₱2.00/page");
        JLabel qualityDesc = new JLabel("Standard: Regular bond paper. Premium: Thicker, smoother. Ultra Premium: Best quality, extra thick and smooth.");
        qualityDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        qualityDesc.setForeground(new Color(80, 80, 80));
        qualityDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        qualityDesc.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pricePanel.add(qualityDesc);
        pricePanel.add(Box.createVerticalStrut(20));
        JLabel noteLabel = new JLabel("Note: Prices include tax");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(new Color(128, 128, 128));
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(noteLabel);
        // Add price panel to the main panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pricePanel, gbc);

        // Add file upload panel below other form fields
        GridBagConstraints gbcFiles = new GridBagConstraints();
        gbcFiles.gridx = 0;
        gbcFiles.gridy = 4;
        gbcFiles.gridwidth = 2;
        gbcFiles.fill = GridBagConstraints.HORIZONTAL;
        gbcFiles.insets = new Insets(10, 0, 0, 0);
        JPanel filePanel = new JPanel(new GridLayout(3, 3, 10, 10));
        filePanel.setBorder(BorderFactory.createTitledBorder("Upload Required Files"));

        // Order Receipt
        JLabel orderReceiptLabel = new JLabel("Order Receipt:");
        orderReceiptField = new JTextField();
        orderReceiptField.setEditable(false);
        JButton orderReceiptButton = new JButton("Choose File");
        orderReceiptButton.addActionListener(e -> chooseOrderReceiptFile());
        filePanel.add(orderReceiptLabel);
        filePanel.add(orderReceiptField);
        filePanel.add(orderReceiptButton);

        // GCash Receipt
        JLabel gcashReceiptLabel = new JLabel("GCash Receipt:");
        gcashReceiptField = new JTextField();
        gcashReceiptField.setEditable(false);
        JButton gcashReceiptButton = new JButton("Choose File");
        gcashReceiptButton.addActionListener(e -> chooseGcashReceiptFile());
        filePanel.add(gcashReceiptLabel);
        filePanel.add(gcashReceiptField);
        filePanel.add(gcashReceiptButton);

        // Print Files
        JLabel printFilesLabel = new JLabel("Print Files:");
        printFilesField = new JTextField();
        printFilesField.setEditable(false);
        JButton printFilesButton = new JButton("Choose Files");
        printFilesButton.addActionListener(e -> choosePrintFiles());
        filePanel.add(printFilesLabel);
        filePanel.add(printFilesField);
        filePanel.add(printFilesButton);

        panel.add(filePanel, gbcFiles);

        // Add submit and back buttons side by side (only once)
        GridBagConstraints gbcButtonRow = new GridBagConstraints();
        gbcButtonRow.gridx = 0;
        gbcButtonRow.gridy = 5;
        gbcButtonRow.gridwidth = 2;
        gbcButtonRow.insets = new Insets(20, 0, 0, 0);
        gbcButtonRow.anchor = GridBagConstraints.CENTER;
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton submitButton = new JButton("Submit Order");
        submitButton.setForeground(Color.BLACK);
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.addActionListener(e -> handleSubmit());
        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> goBack());
        buttonRow.add(submitButton);
        buttonRow.add(backButton);
        panel.add(buttonRow, gbcButtonRow);

        JScrollPane scrollPane = new JScrollPane(panel);
        setContentPane(scrollPane);
    }

    private void chooseOrderReceiptFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            orderReceiptFile = chooser.getSelectedFile();
            orderReceiptField.setText(orderReceiptFile.getName());
        }
    }

    private void chooseGcashReceiptFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            gcashReceiptFile = chooser.getSelectedFile();
            gcashReceiptField.setText(gcashReceiptFile.getName());
        }
    }

    private void choosePrintFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            printFiles = chooser.getSelectedFiles();
            StringBuilder sb = new StringBuilder();
            for (File f : printFiles) {
                sb.append(f.getName()).append(", ");
            }
            printFilesField.setText(sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "");
        }
    }

    private void handleSubmit() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email and phone using KaelLib
        if (!KaelLib.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!KaelLib.isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid phone number. Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (orderReceiptFile == null || gcashReceiptFile == null || printFiles == null || printFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "Please upload all required files before submitting.");
            return;
        }

        // Save uploaded files to uploads/ directory
        String orderReceiptPath = saveUploadedFile(orderReceiptFile, "order_receipt_");
        String gcashReceiptPath = saveUploadedFile(gcashReceiptFile, "gcash_receipt_");
        StringBuilder printFilesPaths = new StringBuilder();
        for (File f : printFiles) {
            String path = saveUploadedFile(f, "print_file_");
            printFilesPaths.append(path).append(";");
        }

        // Here you would save the order info and file paths to a file or database for admin access
        // For demo, just show a dialog with the saved paths
        JOptionPane.showMessageDialog(this,
            "Order submitted!\nOrder Receipt: " + orderReceiptPath +
            "\nGCash Receipt: " + gcashReceiptPath +
            "\nPrint Files: " + printFilesPaths,
            "Order Submitted", JOptionPane.INFORMATION_MESSAGE);
        // Optionally, clear the form or close
    }

    private String saveUploadedFile(File file, String prefix) {
        if (file == null) return "";
        try {
            File uploadsDir = new File("uploads");
            if (!uploadsDir.exists()) uploadsDir.mkdirs();
            String newFileName = prefix + System.currentTimeMillis() + "_" + file.getName();
            File dest = new File(uploadsDir, newFileName);
            java.nio.file.Files.copy(file.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return dest.getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private void goBack() {
        this.dispose();
        new LoginPage(systemManager).setVisible(true);
    }

    // Helper to add price rows
    private void addPriceRow(JPanel panel, String label, String price) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(new Color(248, 249, 250));
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel priceComponent = new JLabel(price);
        priceComponent.setFont(new Font("Arial", Font.BOLD, 13));
        row.add(labelComponent);
        row.add(Box.createHorizontalGlue());
        row.add(priceComponent);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(row);
    }
}
