package com.example.printshopapp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffDashboard extends JFrame {
    private final SystemManager systemManager;
    private final User staffUser;
    private DefaultTableModel tableModel;  // Removed final modifier

    // Define colors for consistent theme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);    // Blue
    private static final Color TEXT_COLOR = new Color(44, 62, 80);         // Dark Gray
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Green
    private static final Color WARNING_COLOR = new Color(230, 126, 34);    // Orange
    private static final Color DANGER_COLOR = new Color(231, 76, 60);      // Red

    // Revenue labels for staff
    private JLabel generatedLabel;
    private JLabel possibleLabel;

    public StaffDashboard(SystemManager systemManager, User staffUser) {
        this.systemManager = systemManager;
        this.staffUser = staffUser;
        setTitle("Staff Dashboard - " + staffUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setupComponents();
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);

        // Create a container panel for the orders that will be opaque
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 255, 255, 240));
        contentPanel.setOpaque(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components
        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(createRevenuePanel(), BorderLayout.WEST);
        contentPanel.add(createOrdersPanel(), BorderLayout.CENTER);
        contentPanel.add(createControlPanel(), BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Staff Order Management Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Staff Member: " + staffUser.getName());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(welcomeLabel);

        return headerPanel;
    }

    private JPanel createRevenuePanel() {
        JPanel revenuePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        revenuePanel.setBackground(new Color(255, 255, 255));
        revenuePanel.setOpaque(true);
        revenuePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "My Revenue",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)
        ));
        generatedLabel = new JLabel();
        generatedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        generatedLabel.setForeground(SUCCESS_COLOR);
        possibleLabel = new JLabel();
        possibleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        possibleLabel.setForeground(WARNING_COLOR);
        revenuePanel.add(generatedLabel);
        revenuePanel.add(possibleLabel);
        updateRevenueLabels();
        return revenuePanel;
    }

    private void updateRevenueLabels() {
        double generated = systemManager.getStaffRevenue(staffUser.getId());
        double possible = systemManager.getStaffPossibleRevenue(staffUser.getId());
        generatedLabel.setText("Generated Revenue: ₱" + String.format("%.2f", generated));
        possibleLabel.setText("Possible Revenue: ₱" + String.format("%.2f", possible));
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(255, 255, 255, 240)); // Semi-transparent white background
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Assigned Orders",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        Color.BLACK
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create table with custom model
        String[] columns = {
                "Order ID",
                "Customer Name",
                "Status",
                "Pages",
                "Copies",
                "Type",
                "Date",
                "Total Amount (₱)",
                "Document"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the Document column (index 8) is editable for the button
                return column == 8;
            }
        };

        JTable ordersTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the Document column (index 8) is editable for the button
                return column == 8;
            }
            @Override
            public boolean isCellSelected(int row, int column) {
                // Allow highlighting/selection, but prevent column/row resizing
                return super.isCellSelected(row, column);
            }
        };
        ordersTable.setFillsViewportHeight(true);
        ordersTable.setRowHeight(30); // Increased row height
        ordersTable.setRowSelectionAllowed(true);
        ordersTable.setColumnSelectionAllowed(false);
        ordersTable.setCellSelectionEnabled(false);
        ordersTable.setFocusable(true);
        // Prevent column reordering and resizing
        ordersTable.getTableHeader().setReorderingAllowed(false);
        ordersTable.getTableHeader().setResizingAllowed(false);
        ordersTable.setAutoCreateRowSorter(true);
        ordersTable.setBackground(Color.WHITE);
        ordersTable.setForeground(Color.BLACK);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ordersTable.setGridColor(new Color(230, 230, 230));

        // Style the table header
        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK);  // Changed from WHITE to BLACK
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35)); // Taller header
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Center-align all columns and set black text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK); // Always set font color to black
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        for (int i = 0; i < ordersTable.getColumnCount(); i++) {
            if (i != 8) { // Don't override the Document column renderer
                ordersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Add custom renderer and editor for the Document column
        ordersTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        ordersTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, systemManager, this));

        // Set column widths
        TableColumn column;
        int[] columnWidths = {80, 150, 100, 60, 60, 100, 100, 100, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            column = ordersTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        // Add table to scroll pane with opaque background
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));

        // Add refresh button
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.setToolTipText("Reload the orders list");

        refreshButton.addActionListener(e -> refreshOrdersTable());

        // Add new action buttons for status
        JButton markCompletedButton = new JButton("Mark as Completed");
        markCompletedButton.setBackground(SUCCESS_COLOR);
        markCompletedButton.setForeground(Color.BLACK);
        markCompletedButton.setFont(new Font("Arial", Font.BOLD, 14));
        markCompletedButton.setFocusPainted(false);
        markCompletedButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        markCompletedButton.setToolTipText("Set selected order to Completed");
        markCompletedButton.addActionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row != -1 && tableModel.getValueAt(row, 0) instanceof String) {
                String orderId = (String) tableModel.getValueAt(row, 0);
                markOrderAsCompleted(orderId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid order to mark as completed.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton markOnProcessButton = new JButton("Mark as On Process");
        markOnProcessButton.setBackground(WARNING_COLOR);
        markOnProcessButton.setForeground(Color.BLACK);
        markOnProcessButton.setFont(new Font("Arial", Font.BOLD, 14));
        markOnProcessButton.setFocusPainted(false);
        markOnProcessButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        markOnProcessButton.setToolTipText("Set selected order to On Process");
        markOnProcessButton.addActionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row != -1 && tableModel.getValueAt(row, 0) instanceof String) {
                String orderId = (String) tableModel.getValueAt(row, 0);
                markOrderAsOnProcess(orderId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid order to mark as on process.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(markOnProcessButton);
        buttonPanel.add(markCompletedButton);

        // Add right-click menu for marking as completed
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem completeItem = new JMenuItem("Mark as Completed");
        completeItem.addActionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row != -1) {
                Object orderIdObj = tableModel.getValueAt(row, 0);
                Object statusObj = tableModel.getValueAt(row, 2);
                if (orderIdObj instanceof String && "Accepted".equals(statusObj)) {
                    String orderId = (String) orderIdObj;
                    markOrderAsCompleted(orderId);
                } else {
                    JOptionPane.showMessageDialog(this, "Only 'Accepted' orders can be marked as completed.", "Not Allowed", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        popupMenu.add(completeItem);
        ordersTable.setComponentPopupMenu(popupMenu);

        // Add components to main panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load of orders
        refreshOrdersTable();

        return panel;
    }

    private void refreshOrdersTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Order order : systemManager.getOrders()) {
            if (order.getAssignedStaffId() == staffUser.getId()) {
                tableModel.addRow(new Object[]{
                        order.getId(),
                        order.getCustomerName(),
                        order.getStatus(),
                        order.getPageCount(),
                        order.getCopies(),
                        order.isColorPrinting() ? "Color" : "B&W",
                        order.getDate(),
                        String.format("₱%.2f", order.getTotalAmount()),
                        (order.getDocumentPath() != null && !order.getDocumentPath().isEmpty()) ? "View" : "No File"
                });
            }
        }

        // Add a "no orders" row if the table is empty
        if (tableModel.getRowCount() == 0) {
            tableModel.addRow(new Object[]{
                    "-", "No assigned orders", "-", "-", "-", "-", "-", "-", "-"
            });
        }
        // Always update revenue labels after refreshing orders
        updateRevenueLabels();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(DANGER_COLOR);
        logoutButton.setForeground(Color.BLACK);  // Changed from WHITE to BLACK
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());

        // Removed View Revenue button as it's redundant
        panel.add(logoutButton);
        return panel;
    }

    private void markOrderAsCompleted(String orderId) {
        systemManager.updateOrderStatusAndRevenue(orderId, "Completed");
        refreshOrdersTable();
        updateRevenueLabels();
        JOptionPane.showMessageDialog(this, "Order marked as completed! Revenue updated.", "Order Completed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void markOrderAsOnProcess(String orderId) {
        systemManager.updateOrderStatusAndRevenue(orderId, "On Process");
        refreshOrdersTable();
        updateRevenueLabels();
        JOptionPane.showMessageDialog(this, "Order marked as On Process!", "Order Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the status of an order in Revenue.txt by order ID.
     * @param orderId The order ID to update.
     * @param newStatus The new status (e.g., "Completed", "On Process").
     * @return true if update was successful, false otherwise.
     */
    public boolean updateRevenueStatus(String orderId, String newStatus) {
        String filePath = "src/main/resources/com/example/printshopapp/Revenue.txt";
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean updated = false;
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            lines = java.nio.file.Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 4 && parts[0].equals(orderId)) {
                    parts[3] = newStatus;
                    lines.set(i, String.join(",", parts));
                    updated = true;
                    break;
                }
            }
            if (updated) {
                java.nio.file.Files.write(path, lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return updated;
    }

    private void loadOrders() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        boolean hasOrders = false;
        for (Order order : systemManager.getOrders()) {
            if (order.getAssignedStaffId() == staffUser.getId()) {
                hasOrders = true;
                tableModel.addRow(new Object[]{
                        order.getId(),
                        order.getCustomerName(),
                        order.getStatus(),
                        order.getPageCount(),
                        order.getCopies(),
                        order.isColorPrinting() ? "Color" : "B&W",
                        order.getDate(),
                        String.format("₱%.2f", order.getTotalAmount()),
                        (order.getDocumentPath() != null && !order.getDocumentPath().isEmpty()) ? "View" : "No File"
                });
            }
        }

        if (!hasOrders) {
            tableModel.addRow(new Object[]{
                    "-", "No assigned orders", "-", "-", "-", "-", "-", "-", "-"
            });
        }
    }

    private void refreshOrders() {
        loadOrders();
        revalidate();
        repaint();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            systemManager.setCurrentUser(null);
            new LoginPage(systemManager).setVisible(true);
            dispose();
        }
    }

    public void showUserFilesDialog(Order order) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Document(s)
        String docPath = order.getDocumentPath();
        if (docPath != null && !docPath.trim().isEmpty()) {
            panel.add(new JLabel("Document(s):"));
            for (String doc : docPath.split(";")) {
                String trimmedDoc = doc.trim();
                if (!trimmedDoc.isEmpty()) {
                    JButton openDocBtn = new JButton(trimmedDoc);
                    openDocBtn.addActionListener(e -> openFile(trimmedDoc));
                    panel.add(openDocBtn);
                }
            }
        }

        // Receipt
        String receiptPath = order.getReceiptPath();
        if (receiptPath != null && !receiptPath.trim().isEmpty()) {
            panel.add(new JLabel("Receipt:"));
            JButton openReceiptBtn = new JButton(receiptPath.trim());
            openReceiptBtn.addActionListener(e -> openFile(receiptPath.trim()));
            panel.add(openReceiptBtn);
        }

        // GCash Receipt
        String gcashPath = order.getGcashReceiptPath();
        if (gcashPath != null && !gcashPath.trim().isEmpty()) {
            panel.add(new JLabel("GCash Receipt:"));
            JButton openGcashBtn = new JButton(gcashPath.trim());
            openGcashBtn.addActionListener(e -> openFile(gcashPath.trim()));
            panel.add(openGcashBtn);
        }

        if (panel.getComponentCount() == 0) {
            panel.add(new JLabel("No files uploaded for this order."));
        }

        JOptionPane.showMessageDialog(this, panel, "Order Files for: " + order.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFile(String fileName) {
        try {
            // Try absolute path first
            java.io.File file = new java.io.File(fileName);
            if (!file.exists()) {
                // Try just the file name (in case only the name is stored)
                String justName = fileName;
                if (fileName.contains("\\")) justName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                else if (fileName.contains("/")) justName = fileName.substring(fileName.lastIndexOf("/") + 1);
                file = new java.io.File(justName);
            }
            if (!file.exists()) {
                // Try src/main/resources/com/example/printshopapp/
                String justName = fileName;
                if (fileName.contains("\\")) justName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                else if (fileName.contains("/")) justName = fileName.substring(fileName.lastIndexOf("/") + 1);
                String devResourcePath = "src/main/resources/com/example/printshopapp/" + justName;
                file = new java.io.File(devResourcePath);
            }
            if (!file.exists()) {
                // Try target/classes/com/example/printshopapp/
                String justName = fileName;
                if (fileName.contains("\\")) justName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                else if (fileName.contains("/")) justName = fileName.substring(fileName.lastIndexOf("/") + 1);
                String compiledResourcePath = "target/classes/com/example/printshopapp/" + justName;
                file = new java.io.File(compiledResourcePath);
            }
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "Could not open file: " + fileName + "\nFile does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.awt.Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not open file: " + fileName + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        // Always enable the button for 'View' (even if no document), except for placeholder row
        boolean isView = "View".equals(value);
        boolean isPlaceholder = table.getValueAt(row, 0) instanceof String && "-".equals(table.getValueAt(row, 0));
        setEnabled(isView && !isPlaceholder);
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean isPushed;
    private int row;
    private DefaultTableModel tableModel;
    private SystemManager systemManager;
    private StaffDashboard staffDashboard;
    public ButtonEditor(JCheckBox checkBox, DefaultTableModel tableModel, SystemManager systemManager, StaffDashboard staffDashboard) {
        super(checkBox);
        this.tableModel = tableModel;
        this.systemManager = systemManager;
        this.staffDashboard = staffDashboard;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.row = row;
        label = (value == null) ? "" : value.toString();
        // Always enable for 'View' except placeholder
        boolean isView = "View".equals(label);
        boolean isPlaceholder = table.getValueAt(row, 0) instanceof String && "-".equals(table.getValueAt(row, 0));
        button.setText(label);
        button.setEnabled(isView && !isPlaceholder);
        isPushed = true;
        return button;
    }
    public Object getCellEditorValue() {
        if (isPushed && "View".equals(label)) {
            int modelRow = row;
            String orderId = (String) tableModel.getValueAt(modelRow, 0);
            Order order = systemManager.getOrders().stream().filter(o -> o.getId().equals(orderId)).findFirst().orElse(null);
            if (order != null) {
                // If there is no document, receipt, or gcash file, show a message
                boolean hasDoc = order.getDocumentPath() != null && !order.getDocumentPath().trim().isEmpty();
                boolean hasReceipt = order.getReceiptPath() != null && !order.getReceiptPath().trim().isEmpty();
                boolean hasGcash = order.getGcashReceiptPath() != null && !order.getGcashReceiptPath().trim().isEmpty();
                if (!hasDoc && !hasReceipt && !hasGcash) {
                    JOptionPane.showMessageDialog(staffDashboard, "No document or receipt uploaded for this order.", "No Document", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    staffDashboard.showUserFilesDialog(order);
                }
            } else {
                JOptionPane.showMessageDialog(staffDashboard, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        isPushed = false;
        return label;
    }
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
