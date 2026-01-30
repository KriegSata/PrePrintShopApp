package com.example.printshopapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class CustomerOptionsWindow extends JFrame {
    private final SystemManager systemManager;
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final int BUTTON_WIDTH = 180;
    private static final int BUTTON_HEIGHT = 100;

    public CustomerOptionsWindow(SystemManager systemManager) {
        this.systemManager = systemManager;
        setTitle("Customer Options");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        setupComponents();
        KaelLib.setBackgroundImage(this, "/com/example/printshopapp/images/background1.png");
    }

    private void setupComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Welcome message
        User currentUser = systemManager.getCurrentUser();
        String userName = currentUser != null ? currentUser.getName() : "Guest";
        JLabel titleLabel = new JLabel("Welcome, " + userName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("What would you like to do today?");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Button Panel with Grid Layout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Create and add buttons
        JButton orderButton = createMenuButton("Place New Order", "Create a new printing order", "/com/example/printshopapp/images/order_icon.png");
        JButton notificationButton = createMenuButton("Notifications", "View your order notifications", "/com/example/printshopapp/images/notification_icon.png");
        JButton editDetailsButton = createMenuButton("Account Settings", "Modify your account information", "/com/example/printshopapp/images/account_icon.png");

        // Add action listeners
        orderButton.addActionListener(e -> openOrderForm());
        notificationButton.addActionListener(e -> showNotificationsDialog());
        editDetailsButton.addActionListener(e -> openEditDetailsWindow());

        // Position buttons
        gbc.gridx = 0; gbc.gridy = 0;
        buttonPanel.add(orderButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(notificationButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        buttonPanel.add(editDetailsButton, gbc);

        // Logout Panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setOpaque(false);
        JButton logoutButton = new JButton("Logout");
        styleLogoutButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        logoutPanel.add(logoutButton);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text, String tooltip, String iconPath) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SECONDARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
                button.setForeground(TEXT_COLOR);
            }
        });

        return button;
    }

    private void styleLogoutButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);  // Changed to black
        button.setBackground(new Color(231, 76, 60));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(192, 57, 43));
                button.setForeground(Color.WHITE); // Changed to white on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(231, 76, 60));
                button.setForeground(Color.BLACK); // Back to black when not hovering
            }
        });
    }

    private void openEditDetailsWindow() {
        new AccountEditForm(systemManager).setVisible(true);
        dispose();
    }

    private void openOrderForm() {
        OrderForm orderForm = new OrderForm(systemManager, this);
        orderForm.setVisible(true);
        // Do NOT hide this window, so it stays visible in the background
    }

    private void showNotificationsDialog() {
        User currentUser = systemManager.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog notificationDialog = new JDialog(this, "Your Notifications", true);
        notificationDialog.setSize(600, 400);
        notificationDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"Date/Time", "Message", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only action column is editable
            }
        };

        // Load notifications
        try (BufferedReader reader = new BufferedReader(new FileReader(SystemManager.ORDER_NOTIFICATION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    String customerIdStr = parts[0].trim();
                    String customerName = parts[1].trim();
                    String fullMessage = parts[2].trim();

                    if (customerIdStr.equals(String.valueOf(currentUser.getId())) ||
                        customerName.equals(currentUser.getName())) {

                        // Try to extract date from the message (look for 'Date: ')
                        String date = "";
                        String message = fullMessage;
                        int dateIdx = fullMessage.lastIndexOf("Date: ");
                        if (dateIdx != -1) {
                            date = fullMessage.substring(dateIdx + 6).trim();
                            // Remove the date part from the message
                            message = fullMessage.substring(0, dateIdx).replaceAll(",\\s*$", "").trim();
                        }
                        // Add button if message contains "Reason:"
                        String action = message.contains("Reason:") ? "View Reason" : "";
                        model.addRow(new Object[]{date, message, action});
                    }
                }
            }
        } catch (Exception e) {
            model.addRow(new Object[]{"", "Error loading notifications", ""});
        }

        JTable notificationTable = new JTable(model);
        notificationTable.setRowHeight(30);
        notificationTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Date/Time column
        notificationTable.getColumnModel().getColumn(1).setPreferredWidth(350); // Message column
        notificationTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Action column

        // Make table non-selectable
        notificationTable.setRowSelectionAllowed(false);
        notificationTable.setColumnSelectionAllowed(false);
        notificationTable.setCellSelectionEnabled(false);

        // Custom renderer and editor for the action button
        notificationTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        notificationTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(notificationTable));

        JScrollPane scrollPane = new JScrollPane(notificationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> notificationDialog.dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        notificationDialog.add(mainPanel);
        notificationDialog.setVisible(true);
    }

    // Custom button renderer
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.BLACK); // Set font color to black
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            setText(value != null && !value.toString().isEmpty() ? "Beep" : "");
            setForeground(Color.BLACK); // Ensure font color is black
            return this;
        }
    }

    // Custom button editor
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public ButtonEditor(JTable table) {
            super(new JCheckBox());
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                   boolean isSelected, int row, int column) {
            label = value != null && !value.toString().isEmpty() ? "Beep" : "";
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && "Beep".equals(label)) {
                showReasonDialog(table.getValueAt(table.getSelectedRow(), 1).toString());
            }
            isPushed = false;
            return label;
        }
    }

    private void showReasonDialog(String message) {
        int reasonIndex = message.indexOf("Reason:");
        if (reasonIndex >= 0) {
            String reason = message.substring(reasonIndex + 7).trim();
            JDialog reasonDialog = new JDialog(this, "Order Status Reason", true);
            reasonDialog.setSize(400, 200);
            reasonDialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextArea reasonArea = new JTextArea(reason);
            reasonArea.setEditable(false);
            reasonArea.setLineWrap(true);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(reasonArea);

            JButton reapplyButton = new JButton("Re-apply");
            reapplyButton.addActionListener(e -> {
                reasonDialog.dispose();
                openOrderForm();
            });

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> reasonDialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.add(reapplyButton);
            buttonPanel.add(closeButton);

            panel.add(new JLabel("Reason:"), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            reasonDialog.add(panel);
            reasonDialog.setVisible(true);
        }
    }

    private void logout() {
        int first = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (first == JOptionPane.YES_OPTION) {
            int second = JOptionPane.showConfirmDialog(
                this,
                "This will log you out of your account. Are you absolutely sure?",
                "Final Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (second == JOptionPane.YES_OPTION) {
                systemManager.setCurrentUser(null);
                new LoginPage(systemManager).setVisible(true);
                dispose();
            }
        }
    }
}
