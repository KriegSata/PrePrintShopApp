package com.example.printshopapp;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import com.example.printshopapp.KaelLib.BackgroundPanel;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class AdminDashboard extends JFrame {
    private final SystemManager systemManager; // Reference to SystemManager
    private JTable ordersTable;  // Added class field
    private DefaultTableModel ordersTableModel;  // Added class field
    private JPanel revenueSummaryPanel; // Revenue summary panel
    private JLabel generatedLabel; // Generated revenue label
    private JLabel possibleLabel; // Possible revenue label
    private JTextArea staffInfoArea; // Store reference for sidebar updates

    public AdminDashboard(SystemManager systemManager) {
        this.systemManager = systemManager;
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800); // Increased window width for horizontal enlargement
        setLocationRelativeTo(null);
        setResizable(false); // Lock window resizing

        // Log opening the admin dashboard window
        systemManager.logAction("Admin", "Opened Admin Dashboard window");

        // Set up background image panel
        BackgroundPanel mainPanel = new BackgroundPanel("/com/example/printshopapp/images/background1.png");
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        setupComponents(mainPanel);
    }

    private void setupComponents(JPanel mainPanel) {
        // Center panel to hold tables
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 10, 10));
        centerPanel.setOpaque(false);

        // Header and revenue summary stacked vertically at the top
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Print Shop Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 120, 212));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);
        JPanel revenuePanel = createRevenueSummaryPanel();
        revenuePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        revenuePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(revenuePanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Add tables to center panel
        centerPanel.add(createPendingAccountsPanel());
        centerPanel.add(createOrdersHistoryPanel());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add price list panel to the west side
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

        // Header for regular paper
        JLabel a4Header = new JLabel("A4 Paper");
        a4Header.setFont(new Font("Arial", Font.BOLD, 14));
        a4Header.setForeground(new Color(44, 62, 80));
        a4Header.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(a4Header);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "⚫ Black & White:", "₱" + systemManager.getPrice("a4_black_white") + "/page");
        addPriceRow(pricePanel, "\uD83C\uDF08 Colored:", "₱" + systemManager.getPrice("a4_colored") + "/page");
        pricePanel.add(Box.createVerticalStrut(15));

        // Header for short bond paper
        JLabel shortHeader = new JLabel("Short Bond Paper");
        shortHeader.setFont(new Font("Arial", Font.BOLD, 14));
        shortHeader.setForeground(new Color(44, 62, 80));
        shortHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(shortHeader);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "⚫ Black & White:", "₱" + systemManager.getPrice("short_black_white") + "/page");
        addPriceRow(pricePanel, "\uD83C\uDF08 Colored:", "₱" + systemManager.getPrice("short_colored") + "/page");
        pricePanel.add(Box.createVerticalStrut(15));

        // Header for long bond paper
        JLabel longHeader = new JLabel("Long Bond Paper");
        longHeader.setFont(new Font("Arial", Font.BOLD, 14));
        longHeader.setForeground(new Color(44, 62, 80));
        longHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(longHeader);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "⚫ Black & White:", "₱" + systemManager.getPrice("long_black_white") + "/page");
        addPriceRow(pricePanel, "\uD83C\uDF08 Colored:", "₱" + systemManager.getPrice("long_colored") + "/page");

        // Add paper quality section and descriptions
        pricePanel.add(Box.createVerticalStrut(20));
        JLabel qualityHeader = new JLabel("Paper Quality");
        qualityHeader.setFont(new Font("Arial", Font.BOLD, 14));
        qualityHeader.setForeground(new Color(44, 62, 80));
        qualityHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(qualityHeader);
        pricePanel.add(Box.createVerticalStrut(5));
        addPriceRow(pricePanel, "Thesis Paper (60gsm):", "+₱-1.00/page (Light and cheaper)");
        addPriceRow(pricePanel, "Standard (85gsm):", "+₱0.00/page");
        addPriceRow(pricePanel, "Premium (130gsm):", "+₱1.00/page");
        addPriceRow(pricePanel, "Ultra Premium (180gsm):", "+₱2.00/page");
        // Add paper quality descriptions
        JTextArea paperDesc = new JTextArea(
                "Thesis Paper: Light and cheaper, 60gsm\n" +
                        "Standard: Common thickness 85gsm\n" +
                        "Premium: Thicker for brocures 130gsm\n" +
                        "Ultra Premium: Thickest for certificates 180gsm");
        paperDesc.setFont(new Font("Arial", Font.ITALIC, 12));
        paperDesc.setEditable(false);
        paperDesc.setOpaque(false);
        pricePanel.add(paperDesc);

        // Add note at the bottom
        pricePanel.add(Box.createVerticalStrut(20));
        JLabel noteLabel = new JLabel("Note: Prices include tax");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(new Color(128, 128, 128));
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pricePanel.add(noteLabel);

        // Set preferred size and add to main panel
        pricePanel.setPreferredSize(new Dimension(250, 0));
        mainPanel.add(pricePanel, BorderLayout.WEST);

        // Sidebar for staff availability
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(320, 0));
        sidebarPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                "Staff Availability",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14))
        );
        sidebarPanel.setBackground(new Color(255,255,255)); // Solid white for best compatibility
        sidebarPanel.setOpaque(true);

        staffInfoArea = new JTextArea(); // Use class field
        staffInfoArea.setEditable(false);
        staffInfoArea.setFont(new Font("Arial", Font.PLAIN, 13));
        staffInfoArea.setBackground(new Color(255,255,255)); // Solid white
        staffInfoArea.setOpaque(true);
        updateStaffInfo(staffInfoArea);

        JScrollPane scrollPane = new JScrollPane(staffInfoArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(255,255,255)); // Solid white
        scrollPane.setOpaque(true);
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);

        // Only add the sidebar ONCE at BorderLayout.EAST
        mainPanel.add(sidebarPanel, BorderLayout.EAST);

        // Refresh staff info and dashboard periodically (every 5 seconds)
        Timer timer = new Timer(5000, e -> {
            updateStaffInfo(staffInfoArea);
            refreshOrdersTable(ordersTableModel);
            refreshRevenueSummary();
        });
        timer.start();

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton createStaffButton = createStyledButton("Create Staff Account", new Color(0, 120, 212));
        JButton logoutButton = createStyledButton("Logout", new Color(212, 40, 40));
        JButton auditTrailButton = createStyledButton("Audit Trail", new Color(0, 150, 0));
        JButton editPricingButton = createStyledButton("Edit Pricing", new Color(255, 193, 7));

        createStaffButton.addActionListener(e -> createStaff());
        logoutButton.addActionListener(e -> logout());
        auditTrailButton.addActionListener(e -> viewAuditTrail());
        editPricingButton.addActionListener(e -> openEditPricingDialog());

        buttonPanel.add(createStaffButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(auditTrailButton); // Add the Audit Trail button
        buttonPanel.add(editPricingButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
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

    private JPanel createRevenueSummaryPanel() {
        revenueSummaryPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        // Set pure white background
        revenueSummaryPanel.setBackground(new Color(255, 255, 255)); // pure white
        revenueSummaryPanel.setOpaque(true);
        revenueSummaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                "Revenue Summary",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)
        ));
        generatedLabel = new JLabel();
        generatedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        generatedLabel.setForeground(new Color(0, 150, 0));
        possibleLabel = new JLabel();
        possibleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        possibleLabel.setForeground(new Color(230, 126, 34));
        revenueSummaryPanel.add(generatedLabel);
        revenueSummaryPanel.add(possibleLabel);
        refreshRevenueSummary();
        return revenueSummaryPanel;
    }

    // Call this to update revenue summary labels
    private void refreshRevenueSummary() {
        double generated = systemManager.getGeneratedRevenue();
        double possible = systemManager.getPossibleRevenue();
        if (generatedLabel != null && possibleLabel != null) {
            generatedLabel.setText("Generated Revenue: ₱" + String.format("%.2f", generated));
            possibleLabel.setText("Possible Revenue: ₱" + String.format("%.2f", possible));
        }
    }

    private JPanel createPendingAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                        "Pending Customer Accounts",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        try {
            // Create table model with pending users
            String[] columns = {"ID", "Name", "School ID", "Email", "Course", "Section", "Actions"};
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6; // Only make Actions column editable
                }
            };

            // Get pending users
            java.util.List<User> pendingUsers = systemManager.getUsers().stream()
                    .filter(u -> !u.isActive() && "customer".equals(u.getRole()))
                    .collect(java.util.stream.Collectors.toList());

            // Add pending users to table
            for (User user : pendingUsers) {
                tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getName(),
                        user.getStudentId(),
                        user.getEmail(),
                        user.getCourse(),
                        user.getSection(),
                        "Actions"
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(50);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.setBackground(Color.WHITE);
            table.setOpaque(true);

            // Style the table header
            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(0, 120, 212));
            header.setForeground(Color.BLACK);

            // Set column widths
            int[] columnWidths = {50, 150, 100, 200, 100, 80, 100};
            for (int i = 0; i < columnWidths.length; i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }

            // Add button renderer and editor for Actions column
            TableColumn actionColumn = table.getColumnModel().getColumn(6);
            actionColumn.setCellRenderer(new DualButtonRenderer());
            actionColumn.setCellEditor(new DualButtonEditor(new JTextField()));

            // Add table to scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Add appropriate message or table
            if (pendingUsers.isEmpty()) {
                JLabel emptyLabel = new JLabel("No pending accounts to approve", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                panel.add(emptyLabel, BorderLayout.CENTER);
            } else {
                panel.add(scrollPane, BorderLayout.CENTER);
            }

        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error loading pending accounts: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createOrdersHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 212), 2),
                "Orders History",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)
        ));

        // Create table model with columns
        String[] columns = {
                "Order ID", "Customer Name", "Status", "Date", "Total Amount",
                "Type", "Pages", "Copies", "Actions"
        };
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only action column is editable
            }
        };

        ordersTable = new JTable(ordersTableModel);
        ordersTable.setFillsViewportHeight(true);

        // Custom renderer and editor for the action buttons
        ordersTable.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        ordersTable.getColumnModel().getColumn(8).setCellEditor(new DualActionButtonEditor(ordersTable));

        // Load orders data
        refreshOrdersTable(ordersTableModel);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.setBackground(new Color(0, 120, 212));
        refreshButton.setForeground(Color.BLACK); // Set font color to black
        refreshButton.addActionListener(e -> refreshOrdersTable(ordersTableModel));

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshOrdersTable(DefaultTableModel model) {
        model.setRowCount(0);
        // Reload orders from file to ensure latest status
        systemManager.reloadOrdersFromFile();
        for (Order order : systemManager.getOrders()) {
            // Show Beep if order is pending or paid
            boolean showBeep = "Pending".equalsIgnoreCase(order.getStatus()) || "Paid".equalsIgnoreCase(order.getStatus());
            String statusToShow = order.getStatus();
            model.addRow(new Object[]{
                    order.getId(),
                    order.getCustomerName(),
                    statusToShow,
                    order.getDate(),
                    order.getFormattedTotalAmount(),
                    order.isColorPrinting() ? "Color" : "B&W",
                    order.getPageCount(),
                    order.getCopies(),
                    showBeep ? "Beep" : "" // Show Beep if order is pending or paid
            });
        }
    }

    private void updateStaffInfo(JTextArea staffInfoArea) {
        StringBuilder infoBuilder = new StringBuilder();
        List<User> staffList = systemManager.getUsers().stream()
                .filter(user -> "staff".equals(user.getRole()))
                .toList();
        for (User staff : staffList) {
            String status = staff.isActive() ? "Available" : "Unavailable";
            infoBuilder.append(staff.getName())
                    .append(" (ID: ").append(staff.getId()).append(") - ").append(status).append("\n");
            List<Order> assignedOrders = systemManager.getOrders().stream()
                    .filter(order -> order.getAssignedStaffId() == staff.getId())
                    .toList();
            if (assignedOrders.isEmpty()) {
                infoBuilder.append("  No assigned orders.\n");
            } else {
                for (Order order : assignedOrders) {
                    String orderIdStr = String.valueOf(order.getId());
                    String shortOrderId = orderIdStr.length() > 6 ? orderIdStr.substring(orderIdStr.length() - 6) : orderIdStr;
                    infoBuilder.append("  - Order ID: ").append(shortOrderId)
                            .append(", Status: ").append(order.getStatus()).append("\n");
                }
            }
        }
        staffInfoArea.setText(infoBuilder.toString());
    }

    // Custom renderer for action buttons
    class ActionButtonRenderer extends JButton implements TableCellRenderer {
        public ActionButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String status = (String) table.getValueAt(row, 2); // Status column
            String action = (String) table.getValueAt(row, 8); // Action column
            if (action == null || action.isEmpty()) {
                setText("");
                setEnabled(false);
                setBackground(Color.WHITE);
                setForeground(Color.LIGHT_GRAY);
            } else {
                setText("Beep");
                setEnabled(true);
                setBackground(new Color(0, 120, 212));
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    // Custom editor for action buttons
    class ActionButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public ActionButtonEditor(JTable table) {
            super(new JCheckBox());
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            String action = (String) table.getValueAt(row, 8); // Action column
            if (action == null || action.isEmpty()) {
                button.setText("");
                button.setEnabled(false);
                button.setBackground(Color.WHITE);
                button.setForeground(Color.LIGHT_GRAY);
            } else {
                button.setText("Beep");
                button.setEnabled(true);
                button.setBackground(new Color(0, 120, 212));
                button.setForeground(Color.BLACK);
            }
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                String action = (String) table.getValueAt(row, 8);
                if (action != null && !action.isEmpty()) {
                    String orderId = (String) table.getValueAt(row, 0);
                    if (!systemManager.getOrders().get(row).isReviewed()) {
                        showOrderReviewDialog(orderId, row);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

    private void showOrderReviewDialog(String orderId, int row) {
        JDialog dialog = new JDialog(this, "Review Order", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea reasonArea = new JTextArea(5, 30);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        // Set default message
        String defaultMsg = "Thank you! We have recieved all the details, we will assign a staff, please check from time to time your notification tab for updates on your order";
        reasonArea.setText(defaultMsg);
        JScrollPane scrollPane = new JScrollPane(reasonArea);
        panel.add(new JLabel("Enter reason/comments:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton acceptButton = new JButton("Accept Order");
        JButton rejectButton = new JButton("Reject Order");
        JButton backButton = new JButton("Back");
        JButton seeFilesButton = new JButton("See Files");
        seeFilesButton.setBackground(new Color(46, 204, 113));
        seeFilesButton.setForeground(Color.BLACK);
        seeFilesButton.setFont(new Font("Arial", Font.BOLD, 12));

        seeFilesButton.addActionListener(e -> {
            Order order = systemManager.getOrders().stream().filter(o -> o.getId().equals(orderId)).findFirst().orElse(null);
            if (order != null) {
                showOrderFilesDialog(order);
            }
        });

        acceptButton.addActionListener(e -> {
            processOrderReview(orderId, true, reasonArea.getText().trim());
            refreshOrdersTable(ordersTableModel);
            ordersTable.clearSelection();
            ordersTable.revalidate();
            ordersTable.repaint();
            dialog.dispose();
        });

        rejectButton.addActionListener(e -> {
            processOrderReview(orderId, false, reasonArea.getText().trim());
            refreshOrdersTable(ordersTableModel);
            ordersTable.clearSelection();
            ordersTable.revalidate();
            ordersTable.repaint();
            dialog.dispose();
        });

        backButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(seeFilesButton);
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Dialog to show order files
    private void showOrderFilesDialog(Order order) {
        JDialog fileDialog = new JDialog(this, "Order Files for: " + order.getId(), true);
        fileDialog.setLayout(new BorderLayout());
        fileDialog.setSize(600, 400);
        fileDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Files for Order: " + order.getId());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(10));

        boolean hasFiles = false;

        // Print files section
        if (order.getDocumentPath() != null && !order.getDocumentPath().trim().isEmpty()
            && !order.getDocumentPath().trim().equalsIgnoreCase("false")) {

            JPanel documentsPanel = createSectionPanel("Print Documents");
            for (String doc : order.getDocumentPath().split(";")) {
                String trimmedDoc = doc.trim();
                if (!trimmedDoc.isEmpty() && !trimmedDoc.equalsIgnoreCase("false")) {
                    addFileLink(documentsPanel, trimmedDoc, "📄");
                    hasFiles = true;
                }
            }
            mainPanel.add(documentsPanel);
            mainPanel.add(Box.createVerticalStrut(10));
        }

        // Order receipt section
        if (order.getReceiptPath() != null && !order.getReceiptPath().trim().isEmpty()
            && !order.getReceiptPath().trim().equalsIgnoreCase("false")) {

            JPanel receiptPanel = createSectionPanel("Order Receipt");
            addFileLink(receiptPanel, order.getReceiptPath().trim(), "🧾");
            mainPanel.add(receiptPanel);
            mainPanel.add(Box.createVerticalStrut(10));
            hasFiles = true;
        }

        // GCash receipt section
        if (order.getGcashReceiptPath() != null && !order.getGcashReceiptPath().trim().isEmpty()
            && !order.getGcashReceiptPath().trim().equalsIgnoreCase("false")) {

            JPanel gcashPanel = createSectionPanel("GCash Receipt");
            addFileLink(gcashPanel, order.getGcashReceiptPath().trim(), "💳");
            mainPanel.add(gcashPanel);
            mainPanel.add(Box.createVerticalStrut(10));
            hasFiles = true;
        }

        if (!hasFiles) {
            JPanel noFilesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            noFilesPanel.setBackground(Color.WHITE);
            JLabel noFilesLabel = new JLabel("No files uploaded for this order");
            noFilesLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            noFilesPanel.add(noFilesLabel);
            mainPanel.add(noFilesPanel);
        }

        // Scroll pane for the main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        fileDialog.add(scrollPane, BorderLayout.CENTER);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> fileDialog.dispose());
        buttonPanel.add(closeButton);
        fileDialog.add(buttonPanel, BorderLayout.SOUTH);

        fileDialog.setVisible(true);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 212), 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return panel;
    }

    private void addFileLink(JPanel panel, String filePath, String icon) {
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setBackground(Color.WHITE);
        filePanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        // File name and icon panel (top row)
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        namePanel.add(iconLabel);

        // Extract filename from path
        String fileName = new java.io.File(filePath).getName();
        JLabel nameLabel = new JLabel(fileName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(new Color(33, 33, 33));
        namePanel.add(nameLabel);

        filePanel.add(namePanel);

        // Full path panel (bottom row)
        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pathPanel.setBackground(Color.WHITE);

        JLabel pathLabel = new JLabel("Path: " + filePath);
        pathLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        pathLabel.setForeground(new Color(0, 102, 204));
        pathLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mouse listener for hover effects and click
        pathLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openFileSmart(filePath);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                pathLabel.setForeground(new Color(0, 0, 153));
                pathLabel.setText("<html><u>Path: " + filePath + "</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                pathLabel.setForeground(new Color(0, 102, 204));
                pathLabel.setText("Path: " + filePath);
            }
        });

        pathPanel.add(pathLabel);
        filePanel.add(pathPanel);

        panel.add(filePanel);
    }

    // Helper to resolve classpath to absolute file path
    private String resolveClassPath(String classPath) {
        if (classPath == null) return null;
        java.io.File f = new java.io.File(classPath);
        if (f.isAbsolute() && f.exists()) return classPath;
        f = new java.io.File(System.getProperty("user.dir"), classPath);
        if (f.exists()) return f.getAbsolutePath();
        f = new java.io.File(System.getProperty("user.dir"), "src/main/resources/" + classPath);
        if (f.exists()) return f.getAbsolutePath();
        return classPath;
    }

    // Helper to check if file is a supported preview type
    private boolean isPreviewableFile(String filePath) {
        String lower = filePath.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".docx") || lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png");
    }

    // Enhanced file opener: preview for supported types, otherwise open with system default
    private void openFileSmart(String fileName) {
        if (fileName == null || fileName.trim().isEmpty() || fileName.trim().equalsIgnoreCase("false")) {
            JOptionPane.showMessageDialog(this, "File path is empty or invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String resolvedPath = resolveClassPath(fileName);
            java.io.File file = new java.io.File(resolvedPath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "Could not open file: " + fileName + "\nFile does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (isPreviewableFile(resolvedPath)) {
                if (resolvedPath.toLowerCase().endsWith(".pdf")) {
                    // Try to open PDF with system default
                    java.awt.Desktop.getDesktop().open(file);
                } else if (resolvedPath.toLowerCase().endsWith(".docx")) {
                    // Try to open DOCX with system default
                    java.awt.Desktop.getDesktop().open(file);
                } else if (resolvedPath.toLowerCase().endsWith(".jpeg") || resolvedPath.toLowerCase().endsWith(".jpg") || resolvedPath.toLowerCase().endsWith(".png")) {
                    // Preview image in dialog
                    ImageIcon icon = new ImageIcon(resolvedPath);
                    Image img = icon.getImage();
                    Image scaled = img.getScaledInstance(600, 800, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaled);
                    JLabel label = new JLabel(icon);
                    JScrollPane scroll = new JScrollPane(label);
                    scroll.setPreferredSize(new Dimension(600, 800));
                    JOptionPane.showMessageDialog(this, scroll, file.getName(), JOptionPane.PLAIN_MESSAGE);
                } else {
                    java.awt.Desktop.getDesktop().open(file);
                }
            } else {
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not open file: " + fileName + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Minimal processOrderReview for order review actions
    private void processOrderReview(String orderId, boolean accepted, String reason) {
        if (accepted) {
            systemManager.setOrderStatus(orderId, "Accepted");
            // Use new round-robin assignment method
            Order assignedOrder = systemManager.getOrders().stream()
                    .filter(o -> o.getId().equals(orderId))
                    .findFirst().orElse(null);
            if (assignedOrder != null) {
                systemManager.assignOrderToNextStaff(assignedOrder);
            }
        } else {
            systemManager.setOrderStatus(orderId, "Declined");
        }
        JOptionPane.showMessageDialog(this, "Order " + orderId + " reviewed. Accepted: " + accepted + ", Reason: " + reason);
    }

    // Add this method to create styled buttons
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

    private void createStaff() {
        try {
            // Create custom dialog for staff creation
            JDialog dialog = new JDialog(this, "Create Staff Account", true);
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Add input fields
            JTextField nameField = new JTextField(20);
            JTextField emailField = new JTextField(20);
            JTextField phoneField = new JTextField(20);
            JTextField usernameField = new JTextField(20);
            JPasswordField passwordField = new JPasswordField(20);

            // Add fields to panel
            addField(panel, "Name:", nameField, gbc, 0);
            addField(panel, "Email:", emailField, gbc, 1);
            addField(panel, "Phone:", phoneField, gbc, 2);
            addField(panel, "Username:", usernameField, gbc, 3);
            addField(panel, "Password:", passwordField, gbc, 4);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            JButton createButton = new JButton("Create Account");
            JButton cancelButton = new JButton("Cancel");

            createButton.addActionListener(e -> {
                try {
                    // Validate input
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String username = usernameField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                            username.isEmpty() || password.isEmpty()) {
                        throw new IllegalArgumentException("All fields are required");
                    }

                    if (!KaelLib.isValidEmail(email)) {
                        throw new IllegalArgumentException("Invalid email format");
                    }

                    if (!KaelLib.isValidPhoneNumber(phone)) {
                        throw new IllegalArgumentException("Invalid phone number format (must be 10 digits)");
                    }

                    if (password.length() < 6) {
                        throw new IllegalArgumentException("Password must be at least 6 characters long");
                    }

                    // Create staff account
                    systemManager.createStaff(name, email, phone, username, password);
                    JOptionPane.showMessageDialog(dialog,
                            "Staff account created successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error creating staff account: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(createButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);

            dialog.add(panel);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating staff account: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to add labeled fields to the form
    private void addField(JPanel panel, String labelText, JComponent field,
                          GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        field.setPreferredSize(new Dimension(200, 30));
        panel.add(field, gbc);
        gbc.weightx = 0.0;
    }


    private void logout() {
        // Actually log out: clear session, close dashboard, show login page
        if (systemManager != null) systemManager.logout();
        this.dispose();
        new LoginPage(systemManager).setVisible(true);
    }

    private void viewAuditTrail() {
        try {
            java.util.List<String> logs = java.nio.file.Files.readAllLines(
                    java.nio.file.Paths.get("src/main/resources/com/example/printshopapp/Logbook.txt"),
                    java.nio.charset.StandardCharsets.UTF_8
            );

            JTextArea textArea = new JTextArea(String.join("\n", logs));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(800, 600));

            JOptionPane.showMessageDialog(this, scrollPane, "Audit Trail", JOptionPane.INFORMATION_MESSAGE);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load audit trail: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditPricingDialog() {
        JDialog dialog = new JDialog(this, "Edit Pricing", true);
        dialog.setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- A4 ---
        formPanel.add(new JLabel("A4 - Black & White (₱/page):"));
        JTextField a4BWField = new JTextField(String.valueOf(systemManager.getPrice("a4_black_white")));
        formPanel.add(a4BWField);
        formPanel.add(new JLabel("A4 - Colored (₱/page):"));
        JTextField a4ColorField = new JTextField(String.valueOf(systemManager.getPrice("a4_colored")));
        formPanel.add(a4ColorField);

        // --- Short ---
        formPanel.add(new JLabel("Short - Black & White (₱/page):"));
        JTextField shortBWField = new JTextField(String.valueOf(systemManager.getPrice("short_black_white")));
        formPanel.add(shortBWField);
        formPanel.add(new JLabel("Short - Colored (₱/page):"));
        JTextField shortColorField = new JTextField(String.valueOf(systemManager.getPrice("short_colored")));
        formPanel.add(shortColorField);

        // --- Long ---
        formPanel.add(new JLabel("Long - Black & White (₱/page):"));
        JTextField longBWField = new JTextField(String.valueOf(systemManager.getPrice("long_black_white")));
        formPanel.add(longBWField);
        formPanel.add(new JLabel("Long - Colored (₱/page):"));
        JTextField longColorField = new JTextField(String.valueOf(systemManager.getPrice("long_colored")));
        formPanel.add(longColorField);

        // --- Paper Qualities ---
        formPanel.add(new JLabel("Thesis Quality (₱/page):"));
        JTextField thesisField = new JTextField(String.valueOf(systemManager.getPrice("thesis")));
        formPanel.add(thesisField);
        formPanel.add(new JLabel("Standard Quality (₱/page):"));
        JTextField standardField = new JTextField(String.valueOf(systemManager.getPrice("standard")));
        formPanel.add(standardField);
        formPanel.add(new JLabel("Premium Quality (₱/page):"));
        JTextField premiumField = new JTextField(String.valueOf(systemManager.getPrice("premium")));
        formPanel.add(premiumField);
        formPanel.add(new JLabel("Ultra Quality (₱/page):"));
        JTextField ultraPremiumField = new JTextField(String.valueOf(systemManager.getPrice("ultra_premium")));
        formPanel.add(ultraPremiumField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                systemManager.setPrice("a4_black_white", Double.parseDouble(a4BWField.getText()));
                systemManager.setPrice("a4_colored", Double.parseDouble(a4ColorField.getText()));
                systemManager.setPrice("short_black_white", Double.parseDouble(shortBWField.getText()));
                systemManager.setPrice("short_colored", Double.parseDouble(shortColorField.getText()));
                systemManager.setPrice("long_black_white", Double.parseDouble(longBWField.getText()));
                systemManager.setPrice("long_colored", Double.parseDouble(longColorField.getText()));
                systemManager.setPrice("thesis", Double.parseDouble(thesisField.getText()));
                systemManager.setPrice("standard", Double.parseDouble(standardField.getText()));
                systemManager.setPrice("premium", Double.parseDouble(premiumField.getText()));
                systemManager.setPrice("ultra_premium", Double.parseDouble(ultraPremiumField.getText()));
                systemManager.savePricingToFile();
                JOptionPane.showMessageDialog(dialog, "Pricing updated successfully.");
                dialog.dispose();
                this.dispose();
                new AdminDashboard(systemManager).setVisible(true); // Refresh dashboard
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    class DualButtonEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private JButton approveButton = new JButton("Approve");
        private JButton declineButton = new JButton("Decline");
        private int editingRow = -1;
        private JTable table;

        public DualButtonEditor(JTextField textField) {
            super(textField);
            approveButton.setBackground(new Color(0, 120, 212));
            approveButton.setForeground(Color.BLACK);
            approveButton.setFont(new Font("Arial", Font.BOLD, 12));
            declineButton.setBackground(new Color(212, 40, 40));
            declineButton.setForeground(Color.BLACK);
            declineButton.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(approveButton);
            panel.add(declineButton);

            approveButton.addActionListener(e -> {
                if (editingRow != -1 && table != null) {
                    int userId = Integer.parseInt(table.getValueAt(editingRow, 0).toString());
                    try {
                        systemManager.approveUser(userId);
                        JOptionPane.showMessageDialog(table, "User approved successfully.");
                        ((DefaultTableModel) table.getModel()).removeRow(editingRow);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(table, "Error approving user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                fireEditingStopped();
            });
            declineButton.addActionListener(e -> {
                if (editingRow != -1 && table != null) {
                    int userId = Integer.parseInt(table.getValueAt(editingRow, 0).toString());
                    try {
                        systemManager.removeUser(userId);
                        JOptionPane.showMessageDialog(table, "User declined and removed.");
                        ((DefaultTableModel) table.getModel()).removeRow(editingRow);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(table, "Error declining user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.editingRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // Renderer for two buttons in a cell
    private class DualButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton approveButton = new JButton("Approve");
        private final JButton declineButton = new JButton("Decline");

        public DualButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            approveButton.setBackground(new Color(0, 120, 212));
            approveButton.setForeground(Color.BLACK);
            approveButton.setFont(new Font("Arial", Font.BOLD, 12));
            declineButton.setBackground(new Color(212, 40, 40));
            declineButton.setForeground(Color.BLACK);
            declineButton.setFont(new Font("Arial", Font.BOLD, 12));
            add(approveButton);
            add(declineButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class DualActionButtonEditor extends DefaultCellEditor {
        private JButton beepButton = new JButton("Beep");
        private String currentOrderId;
        private int currentRow;
        private JTable table;

        public DualActionButtonEditor(JTable table) {
            super(new JTextField());
            this.table = table;
            beepButton.setBackground(new Color(255, 215, 0));
            beepButton.setForeground(Color.BLACK);
            beepButton.setFont(new Font("Arial", Font.BOLD, 12));
            beepButton.setFocusable(false);
            beepButton.addActionListener(e -> {
                if (currentOrderId != null) {
                    showOrderReviewDialog(currentOrderId, currentRow);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentOrderId = (String) table.getValueAt(row, 0); // Order ID column
            currentRow = row;
            // Get the status from the table (corrected to column 2, index 2)
            String status = ((String) table.getValueAt(row, 2)).toLowerCase();
            if ((status.equals("pending") || status.equals("paid")) && value != null && "Beep".equals(value.toString())) {
                beepButton.setEnabled(true);
                beepButton.setVisible(true);
                return beepButton;
            } else {
                beepButton.setEnabled(false);
                beepButton.setVisible(false);
                return new JLabel("");
            }
        }

        @Override
        public Object getCellEditorValue() {
            return "Beep";
        }
    }
}
