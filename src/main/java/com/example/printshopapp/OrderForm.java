package com.example.printshopapp;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class OrderForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField schoolIdField; // Field for school ID
    private JTextField courseField; // New field for course
    private JTextField sectionField; // New field for section
    private JSpinner pageCountSpinner;
    private JSpinner copiesSpinner;
    private JCheckBox colorPrintingCheckbox;
    private JComboBox<String> paperSizeCombo;
    private JComboBox<String> paperQualityCombo; // New field for paper quality
    private JLabel totalCostLabel;
    private double totalCost = 0.0;
    private final SystemManager systemManager; // Reference to SystemManager
    private JFrame parentWindow; // Reference to parent window

    // File upload fields
    private File selectedDocumentFile;
    private File selectedReceiptFile;
    private File selectedGcashReceiptFile;
    private File orderReceiptFile;
    private File gcashReceiptFile;
    private File[] printFiles;
    private JTextField orderReceiptField;
    private JTextField gcashReceiptField;
    private JTextField printFilesField;

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

    public OrderForm(SystemManager systemManager, JFrame parentWindow) {
        this.systemManager = systemManager;
        this.parentWindow = parentWindow;
        setTitle("Print Shop Pre-Order");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create a main container panel with BorderLayout
        JPanel containerPanel = new JPanel(new BorderLayout(20, 0));
        containerPanel.setOpaque(false);

        // Create the form panel (left side)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupComponents(mainPanel, gbc);
        setupListeners();

        // --- Price panel setup ---
        pricePanelWrapper = new JPanel();
        pricePanelWrapper.setLayout(new BoxLayout(pricePanelWrapper, BoxLayout.Y_AXIS));
        pricePanel = buildPricePanel();
        pricePanelWrapper.add(pricePanel);
        containerPanel.add(pricePanelWrapper, BorderLayout.EAST);

        // Add the form panel to the container (left side)
        containerPanel.add(mainPanel, BorderLayout.CENTER);

        // Add the container to the frame
        add(containerPanel);

        setPreferredSize(new Dimension(1000, 900)); // Increased window height
        pack();
        setLocationRelativeTo(null);
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");

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

        // If a user is logged in, fill and gray out their details
        User currentUser = systemManager.getCurrentUser();
        if (currentUser != null) {
            nameField = new JTextField(currentUser.getName(), 20);
            nameField.setEditable(false);
            nameField.setBackground(Color.LIGHT_GRAY);
            emailField = new JTextField(currentUser.getEmail(), 20);
            emailField.setEditable(false);
            emailField.setBackground(Color.LIGHT_GRAY);
            phoneField = new JTextField(currentUser.getContactNumber(), 20);
            phoneField.setEditable(false);
            phoneField.setBackground(Color.LIGHT_GRAY);
            schoolIdField = new JTextField(currentUser.getStudentId() != null ? currentUser.getStudentId() : "", 20);
            schoolIdField.setEditable(false);
            schoolIdField.setBackground(Color.LIGHT_GRAY);
            courseField = new JTextField(currentUser.getCourse() != null ? currentUser.getCourse() : "", 20);
            courseField.setEditable(false);
            courseField.setBackground(Color.LIGHT_GRAY);
            sectionField = new JTextField(currentUser.getSection() != null ? currentUser.getSection() : "", 20);
            sectionField.setEditable(false);
            sectionField.setBackground(Color.LIGHT_GRAY);
        } else {
            nameField = new JTextField(20);
            emailField = new JTextField(20);
            phoneField = new JTextField(20);
            schoolIdField = new JTextField(20);
            courseField = new JTextField(20);
            sectionField = new JTextField(20);
        }

        // Customer Details
        addFormField(panel, "Name:", nameField, gbc, 0);
        addFormField(panel, "Email:", emailField, gbc, 1);
        addFormField(panel, "Phone:", phoneField, gbc, 2);
        addFormField(panel, "School ID:", schoolIdField, gbc, 3);
        addFormField(panel, "Course:", courseField, gbc, 4);
        addFormField(panel, "Section:", sectionField, gbc, 5);

        // Document Specifications
        String[] paperSizes = {"A4", "Short Bond Paper (Letter)", "Long Bond Paper (8.5 x 13 inches)"};
        paperSizeCombo = new JComboBox<>(paperSizes);
        addFormField(panel, "Paper Size:", paperSizeCombo, gbc, 6);

        // Add paper quality dropdown
        String[] qualities = {"Standard", "Premium", "Ultra Premium"};
        paperQualityCombo = new JComboBox<>(qualities);
        addFormField(panel, "Paper Quality:", paperQualityCombo, gbc, 7);

        pageCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        addFormField(panel, "Number of Pages:", pageCountSpinner, gbc, 8);

        copiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        addFormField(panel, "Number of Copies:", copiesSpinner, gbc, 9);

        colorPrintingCheckbox = new JCheckBox("Color Printing");
        addFormField(panel, "", colorPrintingCheckbox, gbc, 10);

        // --- Add 'Do you have papers to provide?' option ---
        providePapersCheckbox = new JCheckBox("Do you have papers to provide?");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(providePapersCheckbox, gbc);

        // Paper size menu
        JLabel sizeLabel = new JLabel("Paper Size:");
        JComboBox<String> sizeCombo = new JComboBox<>(new String[]{"A4", "Short", "Long"});
        sizeCombo.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        panel.add(sizeCombo, gbc);

        // Paper quality menu
        JLabel qualityLabel = new JLabel("Paper Quality:");
        qualityCombo = new JComboBox<>(new String[]{"Standard", "Premium", "Ultra Premium"});
        qualityCombo.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(qualityLabel, gbc);
        gbc.gridx = 1;
        panel.add(qualityCombo, gbc);

        // Paper quantity menu
        JLabel quantityLabel = new JLabel("Paper Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1)); // Default to 0
        quantitySpinner.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 15;  // Moved down to avoid overlap
        panel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

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

        // Total Cost (moved down)
        totalCostLabel = new JLabel(KaelLib.formatCurrency(0.0));
        gbc.gridy = 16;
        addFormField(panel, "Total Cost:", totalCostLabel, gbc, 16);

        // Add pickup note (moved down)
        JLabel pickupNote = new JLabel("Note: Orders are for PICKUP ONLY at the Print Shop");
        pickupNote.setFont(new Font("Arial", Font.BOLD, 12));
        pickupNote.setForeground(new Color(255, 0, 0)); // Red color for emphasis
        gbc.gridy = 17;
        gbc.gridwidth = 2;
        panel.add(pickupNote, gbc);

        // Proceed Button (moved down)
        JButton proceedButton = new JButton("Proceed to Payment");
        proceedButton.setForeground(Color.BLACK);
        proceedButton.addActionListener(e -> proceedToPayment());
        gbc.gridy = 18;
        gbc.gridwidth = 2;
        panel.add(proceedButton, gbc);

        // Add file upload panel below other form fields (moved down)
        GridBagConstraints gbcFiles = new GridBagConstraints();
        gbcFiles.gridx = 0;
        gbcFiles.gridy = 19;
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

        // Add submit and back buttons side by side
        GridBagConstraints gbcButtonRow = new GridBagConstraints();
        gbcButtonRow.gridx = 0;
        gbcButtonRow.gridy = 21;
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
    }

    private void goBack() {
        // Return to the previous window (if any), do not create a new window
        if (parentWindow != null) {
            parentWindow.setVisible(true);
        }
        this.dispose();
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
        ActionListener updateCostListener = e -> updateTotalCost();
        ChangeListener spinnerListener = e -> updateTotalCost();

        colorPrintingCheckbox.addActionListener(updateCostListener);
        pageCountSpinner.addChangeListener(spinnerListener);
        copiesSpinner.addChangeListener(spinnerListener);
        paperSizeCombo.addActionListener(updateCostListener);
        paperQualityCombo.addActionListener(updateCostListener);
        // Add listeners for provided paper fields
        providePapersCheckbox.addActionListener(updateCostListener);
        qualityCombo.addActionListener(updateCostListener);
        // Find the quantity spinner for provided paper and add listener
        // (Assumes only one JSpinner in the parent of qualityCombo)
        Component[] comps = ((Container)qualityCombo.getParent()).getComponents();
        for (Component c : comps) {
            if (c instanceof JSpinner) {
                ((JSpinner)c).addChangeListener(spinnerListener);
            }
        }
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
        if ("Thesis Paper".equals(quality)) qualityPrice = systemManager.getPrice("thesis");
        else if ("Standard".equals(quality)) qualityPrice = systemManager.getPrice("standard");
        else if ("Premium".equals(quality)) qualityPrice = systemManager.getPrice("premium");
        else if ("Ultra Premium".equals(quality)) qualityPrice = systemManager.getPrice("ultra_premium");

        double pricePerPage = basePrice + qualityPrice;
        double total = pages * copies * pricePerPage;

        // Deduct paper cost if user provides their own paper
        if (providePapersCheckbox.isSelected()) {
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
        // Only open the PaymentForm, do not submit or save the order yet
        PaymentForm paymentForm = new PaymentForm(totalCost, this);
        paymentForm.setVisible(true);
        this.setVisible(false); // Hide this window but do not dispose, so user can return if needed
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
        if (orderReceiptFile == null || gcashReceiptFile == null || printFiles == null || printFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "Please upload all required files before submitting.");
            return;
        }
        try {
            String[] printFilePaths = Arrays.stream(printFiles).map(File::getAbsolutePath).toArray(String[]::new);
            int pageCount = (Integer) pageCountSpinner.getValue();
            int copies = (Integer) copiesSpinner.getValue();
            boolean isColorPrinting = colorPrintingCheckbox.isSelected();
            String orderLine = systemManager.generateOrderLine(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    orderReceiptFile != null ? orderReceiptFile.getAbsolutePath() : "",
                    gcashReceiptFile != null ? gcashReceiptFile.getAbsolutePath() : "",
                    printFilePaths,
                    totalCost,
                    pageCount,
                    copies,
                    isColorPrinting
            );
            java.nio.file.Files.write(
                java.nio.file.Paths.get("src/main/resources/com/example/printshopapp/Order.txt"),
                (orderLine + System.lineSeparator()).getBytes(),
                java.nio.file.StandardOpenOption.APPEND
            );
            // Write notification for this order
            String notificationMsg = String.format("%s|Order placed|Your order has been placed and is pending. Order details: %s, %d pages, %d copies, Total: ₱%.2f|%s",
                java.time.LocalDateTime.now().toString(),
                nameField.getText().trim(),
                pageCount,
                copies,
                totalCost,
                emailField.getText().trim()
            );
            java.nio.file.Files.write(
                java.nio.file.Paths.get("src/main/resources/com/example/printshopapp/Ordernotification.txt"),
                (notificationMsg + System.lineSeparator()).getBytes(),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND
            );
            JOptionPane.showMessageDialog(this, "Order submitted successfully!");
            // After submitting, return to the previous window (if any)
            if (parentWindow != null) {
                parentWindow.setVisible(true);
            }
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit order: " + ex.getMessage());
        }
    }

    public SystemManager getSystemManager() {
        return systemManager;
    }

    // Add getter for name
    public String getName() {
        return nameField.getText().trim();
    }

    // Getter methods for fields used in PaymentForm
    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public int getPageCount() {
        return (Integer) pageCountSpinner.getValue();
    }

    public int getCopiesCount() {
        return (Integer) copiesSpinner.getValue();
    }

    public boolean isColorPrinting() {
        return colorPrintingCheckbox.isSelected();
    }

    // Simple form validation: require name, email, phone, and at least one print file
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required.");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required.");
            return false;
        }
        if (printFiles == null || printFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one file to print.");
            return false;
        }
        return true;
    }
}

