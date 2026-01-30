package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.example.printshopapp.OrderForm;
import com.example.printshopapp.GuestPreOrderForm;
import com.example.printshopapp.KaelLib;
import com.example.printshopapp.LoginPage;

public class PaymentForm extends JFrame {
    private final double amount;
    private final JTextField specificAmountField;
    private final JLabel qrCodeLabel;
    private ButtonGroup amountGroup;
    private final OrderForm orderForm; // Can be null for guests
    private final GuestPreOrderForm guestForm; // Can be null for regular customers

    // Constructor for regular customers
    public PaymentForm(double amount, OrderForm orderForm) {
        this.amount = amount;
        this.orderForm = orderForm;
        this.guestForm = null;
        specificAmountField = new JTextField();
        qrCodeLabel = new JLabel();
        setupForm();
    }

    // Constructor for guest customers
    public PaymentForm(double amount, GuestPreOrderForm guestForm) {
        this.amount = amount;
        this.orderForm = null;
        this.guestForm = guestForm;
        specificAmountField = new JTextField();
        qrCodeLabel = new JLabel();
        setupForm();
    }

    private void setupForm() {
        setTitle("GCash Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Changed from EXIT_ON_CLOSE

        // Enlarge the window size
        setSize(1000, 800);
        setLocationRelativeTo(null); // Center the window on the screen
        setResizable(true); // Allow window resizing

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add important instructions title
        JLabel titleLabel = new JLabel("Important Instructions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel amountLabel = new JLabel("Amount to Pay: " + KaelLib.formatCurrency(amount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountPanel.add(amountLabel);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel gcashPanel = new JPanel();
        gcashPanel.setLayout(new BoxLayout(gcashPanel, BoxLayout.Y_AXIS));
        gcashPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Select Payment Amount"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        gcashPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupGcashPanel(gcashPanel);
        mainPanel.add(gcashPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton backButton = new JButton("Back");
        JButton payButton = new JButton("Pay Now");
        backButton.setForeground(Color.BLACK);
        payButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> goBack());
        payButton.addActionListener(e -> processPayment());
        buttonPanel.add(backButton);
        buttonPanel.add(payButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");
    }

    private void setupGcashPanel(JPanel gcashPanel) {
        amountGroup = new ButtonGroup();
        JPanel amountPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        amountPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] amounts = {"50", "100", "150", "500", "Specific Amount"};
        for (String amount : amounts) {
            JRadioButton amountButton = new JRadioButton(amount.equals("Specific Amount") ? 
                amount : "₱" + amount);
            amountButton.setActionCommand(amount);
            amountButton.addActionListener(e -> handleAmountSelection(amount));
            amountGroup.add(amountButton);
            amountPanel.add(amountButton);
        }

        gcashPanel.add(amountPanel);
        gcashPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel specificPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        specificPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        specificPanel.setBorder(BorderFactory.createTitledBorder("Specific Amount"));
        
        JLabel pesoLabel = new JLabel("₱");
        specificAmountField.setPreferredSize(new Dimension(150, 30));
        specificAmountField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        specificPanel.add(pesoLabel);
        specificPanel.add(specificAmountField);
        specificPanel.setVisible(false); // Initially hidden
        gcashPanel.add(specificPanel);

        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        qrPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel qrLabel = new JLabel("Scan to Pay");
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        qrCodeLabel.setPreferredSize(new Dimension(250, 250));
        qrCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        qrPanel.add(Box.createVerticalStrut(10));
        qrPanel.add(qrLabel);
        qrPanel.add(Box.createVerticalStrut(10));
        qrPanel.add(qrCodeLabel);
        gcashPanel.add(qrPanel);
    }

    private void handleAmountSelection(String selected) {
        JPanel specificPanel = (JPanel) specificAmountField.getParent();
        specificPanel.setVisible(selected.equals("Specific Amount"));

        try {
            // Build the QR code path based on selection
            String qrPath = selected.equals("Specific Amount") ?
                "/com/example/printshopapp/images/qrcode.jpg" :
                "/com/example/printshopapp/images/" + selected + "qrcode.jpg";

            // Get the QR code image from resources
            java.net.URL imageUrl = getClass().getResource(qrPath);
            if (imageUrl == null) {
                throw new Exception("QR code image not found: " + qrPath);
            }

            ImageIcon qrCode = new ImageIcon(imageUrl);
            Image image = qrCode.getImage();

            if (image != null) {
                // Scale the image while maintaining aspect ratio
                double scale = Math.min(250.0 / image.getWidth(null), 250.0 / image.getHeight(null));
                int width = (int) (image.getWidth(null) * scale);
                int height = (int) (image.getHeight(null) * scale);

                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                qrCodeLabel.setIcon(new ImageIcon(scaledImage));
                qrCodeLabel.setText(null); // Clear any error text
            } else {
                throw new Exception("Failed to load QR code image");
            }
        } catch (Exception e) {
            qrCodeLabel.setIcon(null);
            qrCodeLabel.setText("QR Code not available");
            qrCodeLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                "Error loading QR code. Please try a different amount or contact support.",
                "QR Code Error",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void goBack() {
        this.dispose();
        if (orderForm != null) {
            // Show the parent window if available
            JFrame parent = null;
            try {
                java.lang.reflect.Field parentField = OrderForm.class.getDeclaredField("parentWindow");
                parentField.setAccessible(true);
                parent = (JFrame) parentField.get(orderForm);
            } catch (Exception ignored) {}
            if (parent != null) {
                parent.setVisible(true);
            }
        } else if (guestForm != null) {
            if (guestForm.getParent() != null && guestForm.getParent() instanceof JFrame parent) {
                parent.setVisible(true);
            }
            // Just dispose this window, do not create a new GuestPreOrderForm
        }
    }

    private void processPayment() {
        if (amountGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Please select a payment amount");
            return;
        }

        // Validate amount
        String selectedAmount = amountGroup.getSelection().getActionCommand();
        if (!validateAmount(selectedAmount)) {
            return;
        }

        try {
            // Get order details
            String name = guestForm != null ? guestForm.getName() : orderForm.getName();
            String email = guestForm != null ? guestForm.getEmail() : orderForm.getEmail();
            String phone = guestForm != null ? guestForm.getPhone() : orderForm.getPhone();
            int pageCount = guestForm != null ? guestForm.getPageCount() : orderForm.getPageCount();
            int copiesCount = guestForm != null ? guestForm.getCopiesCount() : orderForm.getCopiesCount();
            boolean isColorPrinting = guestForm != null ? guestForm.isColorPrinting() : orderForm.isColorPrinting();

            // Create and save the order
            SystemManager systemManager = guestForm != null ? guestForm.getSystemManager() : orderForm.getSystemManager();
            String orderId = systemManager.getNextOrderId();
            int customerId = guestForm != null ? -1 : systemManager.getCurrentUser().getId(); // -1 for guest orders
            Order newOrder = new Order(
                orderId,
                customerId,
                name,
                "Paid", // initial status after payment
                0, // no staff assigned yet
                amount,
                pageCount,
                copiesCount,
                isColorPrinting,
                "", "", "" // documentPath, receiptPath, gcashReceiptPath
            );

            // Save the order in the system
            systemManager.addOrder(newOrder);

            // Remove writing to Order.txt here. Only call systemManager.addOrder(newOrder) if needed for in-memory logic.

            // Show instructions, then receipt, then success message, using dialogs similar to showReceiptInstructions
            try {
                // 1. Show instructions dialog
                JDialog instructionDialog = new JDialog(this, "Payment Instructions", true);
                instructionDialog.setSize(500, 800);
                instructionDialog.setLocationRelativeTo(this);
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                JLabel titleLabel = new JLabel("Important Instructions");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
                titleLabel.setForeground(Color.BLACK);
                titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(titleLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                JTextArea instructionsArea = new JTextArea(
                    "Please follow these steps carefully:\n\n" +
                    "1. Take a screenshot of your payment receipt\n" +
                    "2. Send the following to ctumain.psits@gmail.com:\n" +
                    "   - Payment receipt screenshot\n" +
                    "   - Order form screenshot\n" +
                    "   - File(s) to be printed\n\n" +
                    "Important Notes:\n" +
                    "- Include your name and contact number in the email\n" +
                    "- Files should be in PDF format\n" +
                    "- Maximum file size: 25MB\n\n" +
                    "Your order will be processed once we receive all required items."
                );
                instructionsArea.setEditable(false);
                instructionsArea.setLineWrap(true);
                instructionsArea.setWrapStyleWord(true);
                instructionsArea.setBackground(panel.getBackground());
                instructionsArea.setFont(new Font("Arial", Font.PLAIN, 14));
                panel.add(instructionsArea);
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                JLabel qrLabel = new JLabel("Scan QR Code to Pay:");
                qrLabel.setFont(new Font("Arial", Font.BOLD, 14));
                qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(qrLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                JPanel qrPanel = new JPanel();
                qrPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                qrPanel.add(qrCodeLabel);
                qrPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(qrPanel);
                JButton okButton = new JButton("Continue to Receipt");
                okButton.setForeground(Color.BLACK);
                okButton.setBackground(new Color(0, 120, 212));
                okButton.addActionListener(e -> instructionDialog.dispose());
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.setOpaque(false);
                buttonPanel.add(okButton);
                panel.add(buttonPanel);
                instructionDialog.add(panel);
                instructionDialog.setVisible(true);

                // 2. Show receipt dialog
                JDialog receiptDialog = new JDialog(this, "Order Receipt", true);
                receiptDialog.setSize(700, 600);
                receiptDialog.setLocationRelativeTo(this);
                JPanel receiptPanel = new JPanel();
                receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
                receiptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                JLabel headerLabel = new JLabel("PSITS Print Shop Receipt");
                headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
                headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                receiptPanel.add(headerLabel);
                receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                JLabel dateLabel = new JLabel("Date: " + java.time.LocalDateTime.now().format(formatter));
                dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                receiptPanel.add(dateLabel);
                receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                JTextArea orderDetails = new JTextArea(String.format(
                    "Order Details:\n\n" +
                    "Customer Information:\n" +
                    "  Name: %s\n" +
                    "  Email: %s\n" +
                    "  Contact Number: %s\n\n" +
                    "Order Information:\n" +
                    "  Number of Pages: %d\n" +
                    "  Number of Copies: %d\n" +
                    "  Printing Type: %s\n\n" +
                    "Total Amount: %s",
                    name, email, phone, pageCount, copiesCount,
                    isColorPrinting ? "Colored" : "Black & White",
                    KaelLib.formatCurrency(amount)
                ));
                orderDetails.setEditable(false);
                orderDetails.setLineWrap(true);
                orderDetails.setWrapStyleWord(true);
                orderDetails.setBackground(receiptPanel.getBackground());
                orderDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
                receiptPanel.add(orderDetails);
                JLabel instructionLabel = new JLabel("Please take a screenshot of this receipt");
                instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
                instructionLabel.setForeground(Color.RED);
                instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                receiptPanel.add(instructionLabel);
                JButton continueButton = new JButton("Proceed to Upload Files");
                continueButton.setForeground(Color.BLACK);
                continueButton.setBackground(new Color(0, 120, 212));
                continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                continueButton.addActionListener(e -> {
                    receiptDialog.dispose();
                    this.dispose();
                    if (orderForm != null) {
                        orderForm.setVisible(true);
                    } else if (guestForm != null) {
                        guestForm.setVisible(true);
                    }
                });
                receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                receiptPanel.add(continueButton);
                receiptDialog.add(receiptPanel);
                receiptDialog.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to process payment: " + ex.getMessage());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to process payment: " + ex.getMessage());
        }
    }

    private boolean validateAmount(String selectedAmount) {
        if (selectedAmount.equals("Specific Amount")) {
            try {
                double specificAmount = Double.parseDouble(specificAmountField.getText());
                if (specificAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount greater than 0");
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount");
                return false;
            }
        }
        return true;
    }
}
