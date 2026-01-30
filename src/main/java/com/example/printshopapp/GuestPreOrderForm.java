package com.example.printshopapp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class GuestPreOrderForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JSpinner pageCountSpinner;
    private JSpinner copiesSpinner;
    private JCheckBox colorPrintingCheckbox;
    private JComboBox<String> paperSizeCombo;
    private JComboBox<String> paperQualityCombo;
    private JLabel totalCostLabel;
    private double totalCost = 0.0;
    private final SystemManager systemManager;
    private JFrame parentWindow;

    // Add fields to store selected files
    private File orderReceiptFile;
    private File gcashReceiptFile;
    private File[] printFiles;

    private JButton submitOrderButton; // Store as class field
    private boolean isSubmitting = false;

    // Declare as fields so they are accessible in updateTotalCost
    private JCheckBox providePapersCheckbox;
    private JComboBox<String> qualityCombo;

    // --- Price panel refresh support ---
    private JPanel pricePanelWrapper; // holds the price panel for refresh
    private JPanel pricePanel; // the actual price panel
    private javax.swing.Timer priceRefreshTimer;

    public void refreshPricePanel() {
        pricePanelWrapper.removeAll();
        pricePanel = buildPricePanel();
        pricePanelWrapper.add(pricePanel);
        pricePanelWrapper.revalidate();
        pricePanelWrapper.repaint();
    }

    private JPanel buildPricePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                        "Price List",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16)
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(new Color(255, 255, 255));
        panel.setOpaque(true);
        // Header for regular paper
        JLabel a4Header = new JLabel("A4 Paper");
        a4Header.setFont(new Font("Arial", Font.BOLD, 14));
        a4Header.setForeground(new Color(44, 62, 80));
        a4Header.setAlignmentX(Component.CENTER_ALIGNMENT);
        a4Header.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(a4Header);
        panel.add(Box.createVerticalStrut(5));
        addPriceRow(panel, "⚫ Black & White:", "₱" + systemManager.getPrice("a4_black_white") + "/page");
        addPriceRow(panel, " Colored:", "₱" + systemManager.getPrice("a4_colored") + "/page");
        panel.add(Box.createVerticalStrut(15));
        JLabel shortHeader = new JLabel("Short Bond Paper");
        shortHeader.setFont(new Font("Arial", Font.BOLD, 14));
        shortHeader.setForeground(new Color(44, 62, 80));
        shortHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        shortHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(shortHeader);
        panel.add(Box.createVerticalStrut(5));
        addPriceRow(panel, "⚫ Black & White:", "₱" + systemManager.getPrice("short_black_white") + "/page");
        addPriceRow(panel, " Colored:", "₱" + systemManager.getPrice("short_colored") + "/page");
        panel.add(Box.createVerticalStrut(15));
        JLabel longHeader = new JLabel("Long Bond Paper");
        longHeader.setFont(new Font("Arial", Font.BOLD, 14));
        longHeader.setForeground(new Color(44, 62, 80));
        longHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        longHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(longHeader);
        panel.add(Box.createVerticalStrut(5));
        addPriceRow(panel, "⚫ Black & White:", "₱" + systemManager.getPrice("long_black_white") + "/page");
        addPriceRow(panel, " Colored:", "₱" + systemManager.getPrice("long_colored") + "/page");
        panel.add(Box.createVerticalStrut(20));
        JLabel qualityHeader = new JLabel("Paper Quality");
        qualityHeader.setFont(new Font("Arial", Font.BOLD, 14));
        qualityHeader.setForeground(new Color(44, 62, 80));
        qualityHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        qualityHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(qualityHeader);
        panel.add(Box.createVerticalStrut(5));
        addPriceRow(panel, "Thesis Paper:", (systemManager.getPrice("thesis") >= 0 ? "+₱" : "-₱") + Math.abs(systemManager.getPrice("thesis")) + "/page");
        addPriceRow(panel, "Standard:", (systemManager.getPrice("standard") >= 0 ? "+₱" : "-₱") + Math.abs(systemManager.getPrice("standard")) + "/page");
        addPriceRow(panel, "Premium:", (systemManager.getPrice("premium") >= 0 ? "+₱" : "-₱") + Math.abs(systemManager.getPrice("premium")) + "/page");
        addPriceRow(panel, "Ultra Premium:", (systemManager.getPrice("ultra_premium") >= 0 ? "+₱" : "-₱") + Math.abs(systemManager.getPrice("ultra_premium")) + "/page");
        JTextArea paperDesc = new JTextArea(
                "Thesis Paper: Light and cheaper, 60gsm\n" +
                "Standard: Common thickness 85gsm\n" +
                "Premium: Thicker for brocures 130gsm\n" +
                "Ultra Premium: Thickest for certificates 180gsm");
        paperDesc.setEditable(false);
        paperDesc.setOpaque(false);
        paperDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        paperDesc.setLineWrap(true);
        paperDesc.setWrapStyleWord(true);
        paperDesc.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(paperDesc);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    public GuestPreOrderForm(SystemManager systemManager, JFrame parentWindow) {
        this.systemManager = systemManager;
        this.parentWindow = parentWindow;
        setTitle("Print Shop Pre-Order (Guest)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(1000, 900)); // Increased window height

        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Print Shop Pre-Order - Guest");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Add form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                        "Order Details",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        setupComponents(formPanel, gbc);
        setupListeners();

        // --- Add 'Do you have papers to provide?' option ---
        providePapersCheckbox = new JCheckBox("Do you have papers to provide?");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(providePapersCheckbox, gbc);

        // Paper size menu
        JLabel sizeLabel = new JLabel("Paper Size:");
        JComboBox<String> sizeCombo = new JComboBox<>(new String[]{"A4", "Short", "Long"});
        sizeCombo.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(sizeCombo, gbc);

        // Paper quality menu
        JLabel qualityLabel = new JLabel("Paper Quality:");
        // Remove Thesis Paper from provided paper options
        qualityCombo = new JComboBox<>(new String[]{"Standard", "Premium", "Ultra Premium"});
        qualityCombo.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(qualityLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(qualityCombo, gbc);

        // Paper quantity menu
        JLabel quantityLabel = new JLabel("Paper Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1)); // Default to 0
        quantitySpinner.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(quantitySpinner, gbc);

        // Enable/disable menus based on checkbox
        providePapersCheckbox.addActionListener(e -> {
            boolean enabled = providePapersCheckbox.isSelected();
            sizeCombo.setEnabled(enabled);
            qualityCombo.setEnabled(enabled);
            quantitySpinner.setEnabled(enabled);
            if (!enabled) {
                quantitySpinner.setValue(0); // Reset to 0 if unchecked
            }
            updateTotalCost();
        });
        sizeCombo.addActionListener(e -> updateTotalCost());
        qualityCombo.addActionListener(e -> updateTotalCost());
        quantitySpinner.addChangeListener(e -> updateTotalCost());

        // Enable/disable paper size and quality based on checkbox
        providePapersCheckbox.addItemListener(e -> {
            boolean selected = providePapersCheckbox.isSelected();
            sizeCombo.setEnabled(selected);
            qualityCombo.setEnabled(selected);
        });

        // Remove JScrollPane to avoid scrolling and add formPanel directly
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");

        // --- Price panel setup ---
        pricePanelWrapper = new JPanel();
        pricePanelWrapper.setLayout(new BoxLayout(pricePanelWrapper, BoxLayout.Y_AXIS));
        pricePanel = buildPricePanel();
        pricePanelWrapper.add(pricePanel);
        getContentPane().add(pricePanelWrapper, BorderLayout.EAST);

        // --- Timer for auto-refresh ---
        priceRefreshTimer = new javax.swing.Timer(5000, e -> refreshPricePanel());
        priceRefreshTimer.start();
    }

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

    private void setupComponents(JPanel panel, GridBagConstraints gbc) {
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Style text fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        styleTextField(nameField);
        styleTextField(emailField);
        styleTextField(phoneField);

        addFormField(panel, "Name:", nameField, gbc, 0);
        addFormField(panel, "Email:", emailField, gbc, 1);
        addFormField(panel, "Phone:", phoneField, gbc, 2);

        // Remove A3 and Legal from paper size options
        String[] paperSizes = {"A4", "Short Bond Paper (Letter)", "Long Bond Paper (8.5 x 13 inches)"};
        paperSizeCombo = new JComboBox<>(paperSizes);
        styleComboBox(paperSizeCombo);
        addFormField(panel, "Paper Size:", paperSizeCombo, gbc, 3);

        // Add paper quality dropdown
        String[] qualities = {"Thesis (Cheap)", "Standard", "Premium", "Ultra Premium"};
        paperQualityCombo = new JComboBox<>(qualities);
        addFormField(panel, "Paper Quality:", paperQualityCombo, gbc, 4);

        pageCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        copiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        styleSpinner(pageCountSpinner);
        styleSpinner(copiesSpinner);
        addFormField(panel, "Number of Pages:", pageCountSpinner, gbc, 5);
        addFormField(panel, "Number of Copies:", copiesSpinner, gbc, 6);

        colorPrintingCheckbox = new JCheckBox("Color Printing");
        styleCheckBox(colorPrintingCheckbox);
        addFormField(panel, "", colorPrintingCheckbox, gbc, 7);

        totalCostLabel = new JLabel(KaelLib.formatCurrency(0.0));
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addFormField(panel, "Total Cost:", totalCostLabel, gbc, 8);

        // Add pickup note
        JLabel pickupNote = new JLabel("Note: Orders are for PICKUP ONLY at the Print Shop");
        pickupNote.setFont(new Font("Arial", Font.BOLD, 12));
        pickupNote.setForeground(new Color(255, 0, 0)); // Red color for emphasis
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(pickupNote, gbc);

        // Add buttons in a 2-row panel for better visibility
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(10, 10, 10, 10);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1.0;

        JButton proceedButton = createStyledButton("Proceed to Payment", new Color(0, 120, 212));
        proceedButton.setFont(new Font("Arial", Font.BOLD, 16));
        proceedButton.addActionListener(unused -> proceedToPayment());
        btnGbc.gridx = 0; btnGbc.gridy = 0;
        buttonPanel.add(proceedButton, btnGbc);

        submitOrderButton = createStyledButton("Submit Order", new Color(39, 174, 96));
        submitOrderButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitOrderButton.addActionListener(unused -> {
            if (isSubmitting) return;
            isSubmitting = true;
            submitOrderButton.setEnabled(false); // Disable immediately
            handleSubmitOrder();
        });
        btnGbc.gridx = 1; btnGbc.gridy = 0;
        buttonPanel.add(submitOrderButton, btnGbc);

        JButton orderReceiptButton = createStyledButton("Upload Order Receipt", new Color(46, 204, 113));
        orderReceiptButton.setFont(new Font("Arial", Font.BOLD, 16));
        orderReceiptButton.addActionListener(unused -> uploadOrderReceipt());
        btnGbc.gridx = 0; btnGbc.gridy = 1;
        buttonPanel.add(orderReceiptButton, btnGbc);

        JButton gcashReceiptButton = createStyledButton("Upload GCash Receipt", new Color(241, 196, 15));
        gcashReceiptButton.setFont(new Font("Arial", Font.BOLD, 16));
        gcashReceiptButton.addActionListener(unused -> uploadGcashReceipt());
        btnGbc.gridx = 1; btnGbc.gridy = 1;
        buttonPanel.add(gcashReceiptButton, btnGbc);

        JButton printFilesButton = createStyledButton("Upload Print Files", new Color(52, 152, 219));
        printFilesButton.setFont(new Font("Arial", Font.BOLD, 16));
        printFilesButton.addActionListener(unused -> uploadPrintFiles());
        btnGbc.gridx = 0; btnGbc.gridy = 2;
        buttonPanel.add(printFilesButton, btnGbc);

        JButton backButton = createStyledButton("Back", new Color(128, 128, 128));
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(unused -> goBack());
        btnGbc.gridx = 1; btnGbc.gridy = 2;
        buttonPanel.add(backButton, btnGbc);

        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setPreferredSize(new Dimension(200, 30));
        ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);  // Set text color to black
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addFormField(JPanel panel, String label, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
        gbc.weightx = 0.0;
    }

    private void setupListeners() {
        ActionListener updateCostListener = unused -> updateTotalCost();
        ChangeListener spinnerListener = unused -> updateTotalCost();

        colorPrintingCheckbox.addActionListener(updateCostListener);
        pageCountSpinner.addChangeListener(spinnerListener);
        copiesSpinner.addChangeListener(spinnerListener);
        paperSizeCombo.addActionListener(updateCostListener);
        paperQualityCombo.addActionListener(updateCostListener);
    }

    private void updateTotalCost() {
        int pages = (Integer) pageCountSpinner.getValue();
        int copies = (Integer) copiesSpinner.getValue();

        if (pages == 0) {
            totalCost = 0.0;
            totalCostLabel.setText(KaelLib.formatCurrency(totalCost));
            return;
        }

        // Determine paper size key
        String paperSize = (String) paperSizeCombo.getSelectedItem();
        String bwKey = "a4_black_white", colorKey = "a4_colored";
        if ("Short Bond Paper (Letter)".equals(paperSize) || "Short".equals(paperSize)) {
            bwKey = "short_black_white";
            colorKey = "short_colored";
        } else if ("Long Bond Paper (8.5 x 13 inches)".equals(paperSize) || "Long".equals(paperSize)) {
            bwKey = "long_black_white";
            colorKey = "long_colored";
        }
        boolean isColor = colorPrintingCheckbox.isSelected();
        double basePrice = systemManager.getPrice(isColor ? colorKey : bwKey);

        // Add paper quality price
        String quality = (String) paperQualityCombo.getSelectedItem();
        double qualityPrice = 0.0;
        if ("Thesis (Cheap)".equals(quality) || "Thesis Paper".equals(quality)) qualityPrice = systemManager.getPrice("thesis");
        else if ("Standard".equals(quality)) qualityPrice = systemManager.getPrice("standard");
        else if ("Premium".equals(quality)) qualityPrice = systemManager.getPrice("premium");
        else if ("Ultra Premium".equals(quality)) qualityPrice = systemManager.getPrice("ultra_premium");

        double pricePerPage = basePrice + qualityPrice;
        double total = pages * copies * pricePerPage;

        // Deduct if user provides their own paper
        if (providePapersCheckbox != null && providePapersCheckbox.isSelected()) {
            int providedQty = 0;
            String providedSize = null;
            String providedQuality = null;
            try {
                Component[] comps = ((Container)qualityCombo.getParent()).getComponents();
                for (Component c : comps) {
                    if (c instanceof JSpinner) {
                        providedQty = (Integer)((JSpinner)c).getValue();
                    }
                    if (c instanceof JComboBox) {
                        JComboBox cb = (JComboBox) c;
                        if (cb == qualityCombo) providedQuality = (String) cb.getSelectedItem();
                        if (cb == paperSizeCombo) providedSize = (String) cb.getSelectedItem();
                    }
                }
            } catch (Exception ignore) {}
            if (providedQty > 0) {
                double sizeDiscount = 0.0;
                if (providedSize != null) {
                    if ("A4".equalsIgnoreCase(providedSize)) sizeDiscount = 1.5;
                    else if ("Short".equalsIgnoreCase(providedSize) || "Short Bond Paper (Letter)".equalsIgnoreCase(providedSize)) sizeDiscount = 1.0;
                    else if ("Long".equalsIgnoreCase(providedSize) || "Long Bond Paper (8.5 x 13 inches)".equalsIgnoreCase(providedSize)) sizeDiscount = 1.5;
                }
                double qualityDiscount = 0.0;
                if (providedQuality != null) {
                    if ("Premium".equalsIgnoreCase(providedQuality)) qualityDiscount = 1.5;
                    else if ("Ultra Premium".equalsIgnoreCase(providedQuality)) qualityDiscount = 2.5;
                    // Standard: no discount
                }
                double totalDiscount = (sizeDiscount + qualityDiscount) * providedQty;
                total -= totalDiscount;
            }
        }

        totalCost = Math.max(0, total);
        totalCostLabel.setText(KaelLib.formatCurrency(totalCost));
    }

    private void proceedToPayment() {
        if (!validateForm()) {
            return;
        }
        // Open the PaymentForm for GCash payment and order receipt
        PaymentForm paymentForm = new PaymentForm(totalCost, this);
        paymentForm.setVisible(true);
        this.setVisible(false); // Hide this window but do not dispose, so user can return if needed
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name");
            return false;
        }
        String email = emailField.getText().trim();
        if (!KaelLib.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address");
            return false;
        }
        String phone = phoneField.getText().trim();
        if (!KaelLib.isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number");
            return false;
        }
        return true;
    }

    private void goBack() {
        if (parentWindow != null) {
            parentWindow.setVisible(true);
        }
        dispose();
    }

    private void uploadOrderReceipt() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            orderReceiptFile = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Order Receipt selected: " + orderReceiptFile.getName());
        }
    }

    private void uploadGcashReceipt() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            gcashReceiptFile = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "GCash Receipt selected: " + gcashReceiptFile.getName());
        }
    }

    private void uploadPrintFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            printFiles = chooser.getSelectedFiles();
            StringBuilder sb = new StringBuilder();
            for (File f : printFiles) {
                sb.append(f.getName()).append(", ");
            }
            JOptionPane.showMessageDialog(this, "Print Files selected: " + (sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "None"));
        }
    }

    private void handleSubmitOrder() {
        // Validate all requirements before proceeding
        if (!validateForm()) {
            submitOrderButton.setEnabled(true);
            isSubmitting = false;
            return;
        }
        if (orderReceiptFile == null || !orderReceiptFile.exists() || !orderReceiptFile.canRead()) {
            JOptionPane.showMessageDialog(this, "Please upload a valid Order Receipt before submitting.", "Missing or Invalid File", JOptionPane.ERROR_MESSAGE);
            submitOrderButton.setEnabled(true);
            isSubmitting = false;
            return;
        }
        if (gcashReceiptFile == null || !gcashReceiptFile.exists() || !gcashReceiptFile.canRead()) {
            JOptionPane.showMessageDialog(this, "Please upload a valid GCash Receipt before submitting.", "Missing or Invalid File", JOptionPane.ERROR_MESSAGE);
            submitOrderButton.setEnabled(true);
            isSubmitting = false;
            return;
        }
        if (printFiles == null || printFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "Please upload at least one print file before submitting.", "Missing File", JOptionPane.ERROR_MESSAGE);
            submitOrderButton.setEnabled(true);
            isSubmitting = false;
            return;
        }
        for (File f : printFiles) {
            if (f == null || !f.exists() || !f.canRead()) {
                JOptionPane.showMessageDialog(this, "One or more print files are missing or unreadable.", "File Error", JOptionPane.ERROR_MESSAGE);
                submitOrderButton.setEnabled(true);
                isSubmitting = false;
                return;
            }
        }
        try {
            // Write the order to Order.txt
            String[] printFileNames = Arrays.stream(printFiles).map(File::getName).toArray(String[]::new);
            String orderLine = systemManager.generateOrderLine(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    orderReceiptFile.getName(),
                    gcashReceiptFile.getName(),
                    printFileNames,
                    totalCost,
                    (Integer) pageCountSpinner.getValue(),
                    (Integer) copiesSpinner.getValue(),
                    colorPrintingCheckbox.isSelected()
            );
            java.nio.file.Files.write(
                    java.nio.file.Paths.get("src/main/resources/com/example/printshopapp/Order.txt"),
                    (orderLine + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.APPEND
            );
            JOptionPane.showMessageDialog(this, "Order submitted successfully!\nThank you for your order.", "Order Submitted", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            submitOrderButton.setEnabled(true);
            isSubmitting = false;
        }
    }

    // Getter methods for PaymentForm
    public String getName() { return nameField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public int getPageCount() { return (Integer) pageCountSpinner.getValue(); }
    public int getCopiesCount() { return (Integer) copiesSpinner.getValue(); }
    public boolean isColorPrinting() { return colorPrintingCheckbox.isSelected(); }

    public SystemManager getSystemManager() {
        return systemManager;
    }
}

