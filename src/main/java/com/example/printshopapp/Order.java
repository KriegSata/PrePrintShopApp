package com.example.printshopapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private final String id;
    private final int customerId;
    private final String customerName;
    private String status;
    private int assignedStaffId;
    private final LocalDateTime date;
    private final double totalAmount;
    private final int pageCount;
    private final int copies;
    private final boolean isColorPrinting;
    private String adminResponse;  // Store admin's reason for acceptance/rejection
    private int isReviewed;        // Track if admin has reviewed the order (0 = not reviewed, 1 = reviewed)
    private String documentPath;   // Path to uploaded document
    private String receiptPath;    // Path to uploaded receipt
    private String gcashReceiptPath; // Path to uploaded GCash receipt

    public Order(String id, int customerId, String customerName,
                String status, int assignedStaffId, double totalAmount,
                int pageCount, int copies, boolean isColorPrinting,
                String documentPath, String receiptPath, String gcashReceiptPath) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;  // FIX: Use the status from file/parameter
        this.assignedStaffId = assignedStaffId;
        this.date = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.pageCount = pageCount;
        this.copies = copies;
        this.isColorPrinting = isColorPrinting;
        this.adminResponse = "";
        this.isReviewed = 0; // Default to 0 (not reviewed)
        this.documentPath = documentPath;
        this.receiptPath = receiptPath;
        this.gcashReceiptPath = gcashReceiptPath;
    }

    // Getters
    public String getId() { return id; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public int getAssignedStaffId() { return assignedStaffId; }
    public int getPageCount() { return pageCount; }
    public int getCopies() { return copies; }
    public boolean isColorPrinting() { return isColorPrinting; }

    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getFormattedTotalAmount() {
        return KaelLib.formatCurrency(totalAmount);
    }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    public String getReceiptPath() { return receiptPath; }
    public void setReceiptPath(String receiptPath) { this.receiptPath = receiptPath; }
    public String getGcashReceiptPath() { return gcashReceiptPath; }
    public void setGcashReceiptPath(String gcashReceiptPath) { this.gcashReceiptPath = gcashReceiptPath; }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedStaffId(int staffId) {
        this.assignedStaffId = staffId;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String response) {
        this.adminResponse = response;
    }

    public boolean isReviewed() {
        return isReviewed == 1;
    }

    public void setReviewed(boolean reviewed) {
        this.isReviewed = reviewed ? 1 : 0;
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%s,%s,%d,%.1f,%d,%d,%b,%s,%s,%d,%s,%s,%s",
            id, customerId, customerName, status, assignedStaffId,
            totalAmount, pageCount, copies, isColorPrinting, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            adminResponse, isReviewed, documentPath == null ? "" : documentPath, receiptPath == null ? "" : receiptPath, gcashReceiptPath == null ? "" : gcashReceiptPath);
    }

    // Add a constructor that can recreate an Order from a saved string
    public static Order fromString(String line) {
        // Split all fields, so that file paths with commas are preserved only in the last field
        String[] parts = line.split(",", -1); // -1 keeps trailing empty strings
        if (parts.length < 15) return null;
        java.util.function.Function<String, Integer> parseIntSafe = s -> {
            try { return (s == null || s.isEmpty()) ? 0 : Integer.parseInt(s); } catch (Exception e) { return 0; }
        };
        java.util.function.Function<String, Double> parseDoubleSafe = s -> {
            try { return (s == null || s.isEmpty()) ? 0.0 : Double.parseDouble(s); } catch (Exception e) { return 0.0; }
        };
        java.util.function.Function<String, Boolean> parseBoolSafe = s -> {
            try { return (s == null || s.isEmpty()) ? false : Boolean.parseBoolean(s); } catch (Exception e) { return false; }
        };
        String id = parts[0];
        int customerId = parts.length > 1 ? parseIntSafe.apply(parts[1]) : 0;
        String customerName = parts.length > 2 ? parts[2] : "";
        String status = parts.length > 3 ? parts[3] : "Pending";
        int assignedStaffId = parts.length > 4 ? parseIntSafe.apply(parts[4]) : 0;
        double totalAmount = parts.length > 5 ? parseDoubleSafe.apply(parts[5]) : 0.0;
        int pageCount = parts.length > 6 ? parseIntSafe.apply(parts[6]) : 0;
        int copies = parts.length > 7 ? parseIntSafe.apply(parts[7]) : 0;
        boolean isColorPrinting = parts.length > 8 ? parseBoolSafe.apply(parts[8]) : false;
        String date = parts.length > 9 ? parts[9] : "";
        String adminResponse = parts.length > 10 ? parts[10] : "";
        int isReviewed = parts.length > 11 ? parseIntSafe.apply(parts[11]) : 0;
        String documentPath = parts.length > 12 ? parts[12] : "";
        String receiptPath = parts.length > 13 ? parts[13] : "";
        String gcashReceiptPath = parts.length > 14 ? parts[14] : "";
        Order order = new Order(
            id, customerId, customerName, status, assignedStaffId, totalAmount, pageCount, copies, isColorPrinting, documentPath, receiptPath, gcashReceiptPath
        );
        order.adminResponse = adminResponse;
        order.isReviewed = isReviewed;
        return order;
    }
}
