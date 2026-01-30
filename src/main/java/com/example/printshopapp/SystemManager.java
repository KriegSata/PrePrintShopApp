package com.example.printshopapp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemManager {
    private List<User> users = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private Queue<Integer> staffQueue = new LinkedList<>();
    private static final String CUSTOMER_FILE_PATH = "src/main/resources/com/example/printshopapp/Customer.txt";
    public static final String ORDER_FILE_PATH = "src/main/resources/com/example/printshopapp/Order.txt";
    public static final String ORDER_NOTIFICATION_FILE_PATH = "src/main/resources/com/example/printshopapp/Ordernotification.txt";
    private static final String ADMIN_FILE_PATH = "src/main/resources/com/example/printshopapp/Admin.txt";
    private static final Logger LOGGER = Logger.getLogger(SystemManager.class.getName());
    private int nextUserId = 1;
    private int nextOrderId = 1;
    private User currentUser = null; // Track the currently logged-in user

    // Revenue tracking
    private static final String REVENUE_FILE_PATH = "src/main/resources/com/example/printshopapp/Revenue.txt";
    private Map<Integer, Double> staffRevenue = new HashMap<>(); // staffId -> revenue

    // --- Pricing Management ---
    private static final String PRICING_FILE_PATH = "src/main/resources/com/example/printshopapp/PricingConfig.txt";
    private final Map<String, Double> pricing = new HashMap<>();

    private void loadPricingFromFile() {
        pricing.clear();
        Path path = Paths.get(PRICING_FILE_PATH);
        try {
            if (!Files.exists(path)) {
                // Default prices if file missing
                pricing.put("a4_black_white", 3.0);
                pricing.put("a4_colored", 5.0);
                pricing.put("short_black_white", 3.0);
                pricing.put("short_colored", 5.0);
                pricing.put("long_black_white", 3.0);
                pricing.put("long_colored", 5.0);
                pricing.put("thesis", -1.0);
                pricing.put("standard", 0.0);
                pricing.put("premium", 2.0);
                pricing.put("ultra_premium", 4.0);
                savePricingToFile();
                return;
            }
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("#") || !line.contains("=")) continue;
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    pricing.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading pricing: " + e.getMessage());
        }
    }

    public void savePricingToFile() {
        Path path = Paths.get(PRICING_FILE_PATH);
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# PricingConfig.txt");
            lines.add("a4_black_white=" + pricing.getOrDefault("a4_black_white", 3.0));
            lines.add("a4_colored=" + pricing.getOrDefault("a4_colored", 5.0));
            lines.add("short_black_white=" + pricing.getOrDefault("short_black_white", 3.0));
            lines.add("short_colored=" + pricing.getOrDefault("short_colored", 5.0));
            lines.add("long_black_white=" + pricing.getOrDefault("long_black_white", 3.0));
            lines.add("long_colored=" + pricing.getOrDefault("long_colored", 5.0));
            lines.add("thesis=" + pricing.getOrDefault("thesis", -1.0));
            lines.add("standard=" + pricing.getOrDefault("standard", 0.0));
            lines.add("premium=" + pricing.getOrDefault("premium", 2.0));
            lines.add("ultra_premium=" + pricing.getOrDefault("ultra_premium", 4.0));
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error saving pricing: " + e.getMessage());
        }
    }

    public double getPrice(String key) {
        return pricing.getOrDefault(key, 0.0);
    }

    public void setPrice(String key, double value) {
        pricing.put(key, value);
        savePricingToFile();
    }

    public Map<String, Double> getAllPrices() {
        return new HashMap<>(pricing);
    }

    public SystemManager() {
        loadAdminFromFile();
        loadStaffFromFile();
        loadCustomersFromFile();
        loadOrdersFromFile();
        assignStaffAndRevenueForAcceptedOrders();
        loadPricingFromFile(); // Load pricing at startup
    }

    /**
     * Returns a list of all orders that are assigned to staff and are in 'Accepted' status.
     * Used for persistent sidebar display in AdminDashboard.
     */
    public List<Order> getAllAcceptedAssignedOrders() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if ("Accepted".equals(order.getStatus()) && order.getAssignedStaffId() != 0) {
                result.add(order);
            }
        }
        return result;
    }

    // Assign staff and update revenue for all accepted orders on startup
    private void assignStaffAndRevenueForAcceptedOrders() {
        for (Order order : orders) {
            if ("Accepted".equals(order.getStatus())) {
                if (order.getAssignedStaffId() == 0) {
                    assignOrderToNextStaff(order);
                } else {
                    ensurePendingRevenueEntry(order);
                }
            }
        }
    }

    // Ensure Revenue.txt has a pending entry for an accepted order
    private void ensurePendingRevenueEntry(Order order) {
        try {
            Path path = Paths.get(REVENUE_FILE_PATH);
            List<String> lines = Files.exists(path) ? Files.readAllLines(path, StandardCharsets.UTF_8) : new ArrayList<>();
            boolean found = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(order.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                writeRevenueEntry(order.getId(), order.getAssignedStaffId(), order.getTotalAmount(), "Pending");
            }
        } catch (IOException e) {
            LOGGER.warning("Failed to ensure pending revenue entry: " + e.getMessage());
        }
    }

    /**
     * Updates or adds a pending revenue entry for the given order.
     * This is called after an order is accepted or assigned.
     */
    public void updateOrAddPendingRevenueEntry(Order order) {
        ensurePendingRevenueEntry(order);
    }

    /**
     * Admin accepts/approves an order: sets status to 'Accepted', assigns staff, and persists changes.
     * Prevents re-approval of already accepted orders.
     */
    public void acceptOrder(String orderId, String adminResponse) {
        Order order = orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        if ("Accepted".equals(order.getStatus())) {
            throw new IllegalStateException("Order is already accepted.");
        }
        order.setStatus("Accepted");
        order.setAdminResponse(adminResponse);
        order.setReviewed(true);
        if (order.getAssignedStaffId() == 0) {
            assignOrderToNextStaff(order);
        } else {
            ensurePendingRevenueEntry(order);
            saveAllOrdersToFile();
        }
    }

    private void loadAdminFromFile() {
        Path path = Paths.get(ADMIN_FILE_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                // Create default admin if file is empty
                User admin = new User(nextUserId++, "Admin", "N/A", "admin@system.com", "0000000000", "admin1", "passwordadmin", "admin", true);
                users.add(admin);
                saveAdminToFile(admin);
                return;
            }
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("//") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    int id = Integer.parseInt(parts[0].trim());
                    nextUserId = Math.max(nextUserId, id + 1);
                    User admin = new User(
                            id,
                            parts[1].trim(), // name
                            "N/A",
                            parts[2].trim(), // email
                            parts[3].trim(), // contactNumber
                            parts[4].trim(), // username
                            parts[5].trim(), // password
                            "admin",
                            Boolean.parseBoolean(parts[6].trim())
                    );
                    users.add(admin);
                }
            }
            // If no admin found, create default
            if (users.stream().noneMatch(u -> "admin".equals(u.getRole()))) {
                User admin = new User(nextUserId++, "Admin", "N/A", "admin@system.com", "0000000000", "admin1", "passwordadmin", "admin", true);
                users.add(admin);
                saveAdminToFile(admin);
            }
        } catch (IOException e) {
            System.err.println("Error loading admin: " + e.getMessage());
        }
    }

    private void saveAdminToFile(User admin) {
        Path path = Paths.get(ADMIN_FILE_PATH);
        try {
            Files.createDirectories(path.getParent());
            String adminData = String.format("%d,%s,%s,%s,%s,%s,%b%n",
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getContactNumber(),
                    admin.getUsername(),
                    admin.getPassword(),
                    admin.isActive()
            );
            Files.write(path, adminData.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving admin: " + e.getMessage());
        }
    }

    // Modified loadStaffFromFile to properly initialize staff queue
    private void loadStaffFromFile() {
        Path path = Paths.get("src/main/resources/com/example/printshopapp/Staff.txt");
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return;
            }

            // Clear existing staff queue
            staffQueue.clear();

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("//") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int id = Integer.parseInt(parts[0].trim());
                    nextUserId = Math.max(nextUserId, id + 1);
                    User staff = new User(
                            id,
                            parts[1].trim(), // name
                            "N/A", // studentId
                            parts[2].trim(), // email
                            parts[3].trim(), // contactNumber
                            parts[4].trim(), // username
                            parts[5].trim(), // password
                            "staff",
                            true
                    );

                    // Add staff to users list if not already present
                    if (users.stream().noneMatch(u -> u.getId() == id)) {
                        users.add(staff);
                    }

                    // Always add to queue for round-robin assignment
                    staffQueue.offer(id);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading staff: " + e.getMessage());
        }
    }

    private void loadOrdersFromFile() {
        Path path = Paths.get(ORDER_FILE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                LOGGER.warning("Error creating order file: " + e.getMessage());
            }
            return;
        }
        List<Order> tempOrders = new ArrayList<>();
        Set<String> seenOrderIds = new HashSet<>();
        int maxOrderId = nextOrderId;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("OrderID:")) continue;
                String[] parts = trimmed.split(",");
                if (parts.length < 14) {
                    LOGGER.warning("Skipped invalid order line: " + trimmed + " (Expected at least 14 fields, found " + parts.length + ")");
                    continue;
                }
                Order order = Order.fromString(trimmed);
                if (order != null && !seenOrderIds.contains(order.getId())) {
                    tempOrders.add(order);
                    seenOrderIds.add(order.getId());
                    // Only update maxOrderId if the ID is numeric
                    try {
                        int numericId = Integer.parseInt(order.getId());
                        maxOrderId = Math.max(maxOrderId, numericId + 1);
                    } catch (NumberFormatException ignored) {
                        // Ignore non-numeric order IDs
                    }
                } else if (order == null) {
                    LOGGER.warning("Skipped invalid order line: " + trimmed);
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Error loading orders from file: " + e.getMessage());
        }
        orders.clear();
        orders.addAll(tempOrders);
        nextOrderId = maxOrderId;
    }

    /**
     * Reloads the orders list from the Order.txt file, replacing the in-memory list with the latest data.
     */
    public void reloadOrdersFromFile() {
        loadOrdersFromFile();
    }

    public List<User> getUsers() {
        return new ArrayList<>(users); // Return a copy to prevent external modifications
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders); // Return a copy to prevent external modifications
    }

    public String getNextOrderId() {
        // Generate a random 6-character alphanumeric string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(6);
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public void addOrder(Order order) {
        // Prevent duplicate orders: check for same customerName, status, and other fields
        boolean duplicate = orders.stream().anyMatch(o ->
                o.getCustomerName().equals(order.getCustomerName()) &&
                        o.getStatus().equals(order.getStatus()) &&
                        o.getTotalAmount() == order.getTotalAmount() &&
                        o.getPageCount() == order.getPageCount() &&
                        o.getCopies() == order.getCopies() &&
                        o.isColorPrinting() == order.isColorPrinting() &&
                        ((o.getDocumentPath() == null && order.getDocumentPath() == null) || (o.getDocumentPath() != null && o.getDocumentPath().equals(order.getDocumentPath()))) &&
                        ((o.getReceiptPath() == null && o.getReceiptPath() == null) || (o.getReceiptPath() != null && o.getReceiptPath().equals(order.getReceiptPath()))) &&
                        ((o.getGcashReceiptPath() == null && o.getGcashReceiptPath() == null) || (o.getGcashReceiptPath() != null && o.getGcashReceiptPath().equals(order.getGcashReceiptPath())))
        );
        if (duplicate) {
            // Optionally log or notify
            System.out.println("Duplicate order detected, not adding.");
            return;
        }
        orders.add(order);
        // Automatically assign the first available staff to the new order
        assignOrderToNextStaff(order);
    }

    public void updateOrderStatus(String orderId, String status, String adminResponse) {
        orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .ifPresent(order -> {
                    order.setStatus(status);
                    order.setAdminResponse(adminResponse);
                    order.setReviewed(true);
                });
        saveAllOrdersToFile();
    }

    // Save all orders to file (overwrite)
    public void saveAllOrdersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE_PATH, false))) {
            for (Order order : orders) {
                writer.write(order.toString());
                writer.newLine();
            }
        } catch (Exception e) {
            LOGGER.warning("Error saving orders to file: " + e.getMessage());
        }
    }

    public List<Order> getOrdersByStatus(String status) {
        return orders.stream()
                .filter(o -> o.getStatus().equals(status))
                .toList();
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        return orders.stream()
                .filter(o -> o.getCustomerId() == customerId)
                .toList();
    }

    public void registerUser(String name, String studentId, String email, String contactNumber,
                             String course, String section, String username, String password, String role) {
        // Validate inputs
        if (name == null || email == null || username == null || password == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }

        // Check if username already exists
        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            throw new IllegalArgumentException("Username already exists");
        }

        User newUser = new User(nextUserId++, name, studentId, email, contactNumber,
                course, section, username, password, role, false);
        users.add(newUser);
        saveCustomerToFile(newUser);
    }

    public void approveUser(int userId) {
        User user = users.stream()
                .filter(u -> u.getId() == userId && !u.isActive())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found or already active"));

        user.setActive(true);
        updateCustomerInFile(user);
    }

    public User validateLogin(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) &&
                        u.getPassword().equals(password) &&
                        u.isActive())
                .findFirst()
                .orElse(null);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    private void loadCustomersFromFile() {
        Path path = Paths.get(CUSTOMER_FILE_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("//") || line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 10) {
                    int id = Integer.parseInt(parts[0].trim());
                    nextUserId = Math.max(nextUserId, id + 1);

                    User user = new User(
                            id,
                            parts[1].trim(), // name
                            parts[2].trim(), // studentId
                            parts[3].trim(), // email
                            parts[4].trim(), // contactNumber
                            parts[5].trim(), // course
                            parts[6].trim(), // section
                            parts[7].trim(), // username
                            parts[8].trim(), // password
                            "customer",      // role
                            Boolean.parseBoolean(parts[9].trim()) // isActive
                    );
                    users.add(user);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void saveCustomerToFile(User user) {
        Path path = Paths.get(CUSTOMER_FILE_PATH);
        try {
            // Create parent directories if they don't exist
            Files.createDirectories(path.getParent());

            String customerData = formatCustomerAsCSV(user) + System.lineSeparator();
            Files.write(path, customerData.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }

    private void updateCustomerInFile(User user) {
        Path path = Paths.get(CUSTOMER_FILE_PATH);
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                if (line.startsWith("//") || line.trim().isEmpty()) {
                    updatedLines.add(line);
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equals(String.valueOf(user.getId()))) {
                    updatedLines.add(formatCustomerAsCSV(user));
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (!found) {
                updatedLines.add(formatCustomerAsCSV(user));
            }

            Files.write(path, updatedLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    private String formatCustomerAsCSV(User user) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%b",
                user.getId(),
                user.getName(),
                user.getStudentId(),
                user.getEmail(),
                user.getContactNumber(),
                user.getCourse(),
                user.getSection(),
                user.getUsername(),
                user.getPassword(),
                user.isActive()
        );
    }

    /**
     * Updates the given user in the users list and persists the change to the file.
     */
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                break;
            }
        }
        saveAllCustomersToFile(); // Always rewrite the file to reflect all changes
    }

    public void createStaff(String name, String email, String contactNumber, String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        // Check if username already exists
        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            throw new IllegalArgumentException("Username already exists");
        }

        User staff = new User(nextUserId++, name, "N/A", email, contactNumber, username, password, "staff", true);
        users.add(staff);
        staffQueue.add(staff.getId());

        // Append staff details to Staff.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/com/example/printshopapp/Staff.txt", true))) {
            writer.write(String.format("%d,%s,%s,%s,%s,%s%n",
                    staff.getId(), name, email, contactNumber, username, password));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write staff details to file", e);
        }
    }

    public void placeOrder(int customerId, String customerName, double totalAmount,
                           int pageCount, int copies, boolean isColorPrinting,
                           String documentPath, String receiptPath, String gcashReceiptPath) {
        String orderId = getNextOrderId();
        Order order = new Order(orderId, customerId, customerName, "Pending", 0, totalAmount,
                pageCount, copies, isColorPrinting, documentPath, receiptPath, gcashReceiptPath);
        orders.add(order);
        saveAllOrdersToFile();
    }

    private void saveOrderToFile(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE_PATH, true))) {
            writer.write(orderToFileString(order));
            writer.newLine();
        } catch (Exception e) {
            LOGGER.warning("Failed to save order: " + e.getMessage());
        }
    }

    private String orderToFileString(Order order) {
        // Use Order.toString() for full serialization
        return order.toString();
    }

    private void writeOrderNotification(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_NOTIFICATION_FILE_PATH, true))) {
            writer.write(order.getCustomerId() + "," + order.getCustomerName() + ",Order Placed: Order ID " + order.getId() + ", Status: " + order.getStatus() + ", Date: " + order.getDate());
            writer.newLine();
        } catch (Exception e) {
            LOGGER.warning("Failed to write order notification: " + e.getMessage());
        }
    }

    public void updateOrderStatus(String orderId, String status, int staffId) {
        Order order = orders.stream()
                .filter(o -> o.getId().equals(orderId) && o.getAssignedStaffId() == staffId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order not found or not assigned to this staff"));

        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        order.setStatus(status);
    }

    private boolean isValidStatus(String status) {
        return status != null && (
                status.equals("pending") ||
                        status.equals("printing") ||
                        status.equals("ready") ||
                        status.equals("completed")
        );
    }

    public void removeUser(int userId) {
        // Remove user from the list
        users.removeIf(u -> u.getId() == userId);
        // Update the customer file to reflect removal
        saveAllCustomersToFile();
    }

    private void saveAllCustomersToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CUSTOMER_FILE_PATH), StandardCharsets.UTF_8)) {
            for (User user : users) {
                // Only save customers (not admin/staff)
                if ("customer".equals(user.getRole())) {
                    writer.write(user.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error saving customers to file: " + e.getMessage());
        }
    }

    public void logAction(String userName, String action) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("%s - %s: %s%n", timestamp, userName, action);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/com/example/printshopapp/Logbook.txt", true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to logbook", e);
        }
    }

    public boolean isStaffAssigned(int staffId) {
        return orders.stream().anyMatch(order -> order.getAssignedStaffId() == staffId);
    }

    public void addOrderNotification(int customerId, String customerName, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_NOTIFICATION_FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("%d,%s,%s - %s%n",
                    customerId,
                    customerName,
                    timestamp,
                    message));
        } catch (IOException e) {
            LOGGER.warning("Failed to write order notification: " + e.getMessage());
        }
    }

    // Assign the next staff in strict round robin order
    public void assignOrderToNextStaff(Order order) {
        // Only assign staff to Accepted orders that don't already have staff
        if (order == null || !"Accepted".equals(order.getStatus())) return;
        if (order.getAssignedStaffId() != 0) return;

        // Check if we have available staff
        if (staffQueue.isEmpty()) {
            LOGGER.warning("No staff available for assignment");
            return;
        }

        // Strict round robin: poll next staff, assign, then add back to queue
        int selectedStaffId = staffQueue.poll();
        order.setAssignedStaffId(selectedStaffId);
        staffQueue.offer(selectedStaffId); // Put staff at the end of the queue

        // Create pending revenue entry for the new assignment
        writeRevenueEntry(order.getId(), selectedStaffId, order.getTotalAmount(), "Pending");

        // Save the assignment to the Orders.txt file
        saveAllOrdersToFile();

        // Log the assignment
        logAction("System", "Assigned order " + order.getId() + " to staff " + selectedStaffId);
    }

    // Call this when staff completes an order
    public void completeOrder(String orderId, int staffId) {
        Order order = orders.stream().filter(o -> o.getId().equals(orderId) && o.getAssignedStaffId() == staffId).findFirst().orElse(null);
        if (order != null && !"Completed".equals(order.getStatus())) {
            order.setStatus("Completed");
            // Update staff revenue in memory
            staffRevenue.put(staffId, staffRevenue.getOrDefault(staffId, 0.0) + order.getTotalAmount());
            // Update Revenue.txt entry to Completed
            updateRevenueStatus(orderId, staffId, "Completed");
        }
    }

    // --- Revenue Management ---
    // Revenue.txt format: orderId,staffId,amount,status,timestamp
    // status: Pending (possible), Completed (generated), Refunded, Cancelled, etc.
    private void writeRevenueEntry(String orderId, int staffId, double amount, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVENUE_FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(orderId + "," + staffId + "," + amount + "," + status + "," + timestamp);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.warning("Failed to write revenue entry: " + e.getMessage());
        }
    }

    private void updateRevenueStatus(String orderId, int staffId, String newStatus) {
        try {
            Path path = Paths.get(REVENUE_FILE_PATH);
            List<String> lines = Files.exists(path) ? Files.readAllLines(path, StandardCharsets.UTF_8) : new ArrayList<>();
            double amount = 0.0;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(orderId) && Integer.parseInt(parts[1]) == staffId) {
                    amount = Double.parseDouble(parts[2]);
                }
            }
            writeRevenueEntry(orderId, staffId, amount, newStatus);
        } catch (IOException e) {
            LOGGER.warning("Failed to update revenue status: " + e.getMessage());
        }
    }

    // Returns the total generated revenue (orders marked as Completed)
    public double getGeneratedRevenue() {
        double total = 0.0;
        for (Order order : orders) {
            if ("Completed".equalsIgnoreCase(order.getStatus())) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }

    // Returns the total possible revenue (orders marked as Accepted or On Process)
    public double getPossibleRevenue() {
        double total = 0.0;
        for (Order order : orders) {
            String status = order.getStatus();
            if ("Accepted".equalsIgnoreCase(status) || "On Process".equalsIgnoreCase(status)) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }

    // Returns the total generated revenue for a specific staff member (status: Completed)
    public double getStaffRevenue(int staffId) {
        double total = 0.0;
        try {
            Path path = Paths.get(REVENUE_FILE_PATH);
            if (!Files.exists(path)) return 0.0;
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && Integer.parseInt(parts[1]) == staffId && "Completed".equals(parts[3])) {
                    total += Double.parseDouble(parts[2]);
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Failed to read staff revenue: " + e.getMessage());
        }
        return total;
    }

    public double getStaffPossibleRevenue(int staffId) {
        double total = 0.0;
        for (Order order : orders) {
            if (order.getAssignedStaffId() == staffId &&
                    ("Accepted".equals(order.getStatus()) || "On Process".equals(order.getStatus()))) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }

    // Set the status of an order by orderId
    public void setOrderStatus(String orderId, String status) {
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                order.setStatus(status);
                break;
            }
        }
        // Optionally, persist the change to file if needed
        saveAllOrdersToFile();
    }

    public String generateOrderLine(String name, String email, String phone, String orderReceipt, String gcashReceipt, String[] printFiles, double totalCost, int pageCount, int copies, boolean isColorPrinting) {
        StringBuilder printFilesList = new StringBuilder();
        for (String file : printFiles) {
            printFilesList.append(file).append(";");
        }
        // Generate a 6-character alphanumeric order ID
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder idBuilder = new StringBuilder(6);
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            idBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }
        String id = idBuilder.toString();
        String customerId = "0";
        String status = "Pending";
        String assignedStaffId = "";
        String date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String adminResponse = "";
        String isReviewed = "false";
        return String.join(",",
                id,
                customerId,
                name,
                status,
                assignedStaffId,
                String.valueOf(totalCost),
                String.valueOf(pageCount),
                String.valueOf(copies),
                String.valueOf(isColorPrinting),
                printFilesList.toString(),
                orderReceipt,
                gcashReceipt,
                adminResponse,
                isReviewed,
                date
        );
    }

    // Call this after any order status change
    public void updateOrderStatusAndRevenue(String orderId, String newStatus) {
        orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .ifPresent(order -> {
                    order.setStatus(newStatus);
                });
        saveAllOrdersToFile();
        updateRevenueFile();
    }

    // Recalculate and write both potential and generated revenue to Revenue.txt
    public void updateRevenueFile() {
        double potentialRevenue = 0.0;
        double generatedRevenue = 0.0;
        List<String> orderLines = new ArrayList<>();
        for (Order order : orders) {
            String revenueType;
            if ("Completed".equalsIgnoreCase(order.getStatus()) || "Complete".equalsIgnoreCase(order.getStatus())) {
                generatedRevenue += order.getTotalAmount();
                revenueType = "generated";
            } else {
                potentialRevenue += order.getTotalAmount();
                revenueType = "potential";
            }
            // Use getAssignedStaffId() and getDate() for staff and timestamp
            String staffId = String.valueOf(order.getAssignedStaffId());
            String timestamp = "";
            try {
                timestamp = order.getDate();
            } catch (Exception e) {
                // fallback if getDate() does not exist
                timestamp = "";
            }
            orderLines.add(order.getId() + "," + staffId + "," + order.getTotalAmount() + "," + order.getStatus() + "," + timestamp + "," + revenueType);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVENUE_FILE_PATH, false))) {
            writer.write("Potential Revenue," + potentialRevenue);
            writer.newLine();
            writer.write("Generated Revenue," + generatedRevenue);
            writer.newLine();
            writer.write("orderId,staffId,amount,status,timestamp,revenueType");
            writer.newLine();
            for (String line : orderLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {
            LOGGER.warning("Error saving revenue to file: " + e.getMessage());
        }
    }

    // Helper to get revenue values for dashboard
    public double getPotentialRevenue() {
        double total = 0.0;
        for (Order order : orders) {
            if (!"Completed".equalsIgnoreCase(order.getStatus()) && !"Complete".equalsIgnoreCase(order.getStatus())) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }
}
