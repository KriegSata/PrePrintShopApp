package com.example.printshopapp;

public class User {
    private int id;
    private String name;
    private String studentId;
    private String email;
    private String contactNumber;
    private String course; // Field for course
    private String section; // Field for section
    private String username;
    private String password;
    private String role; // "admin", "customer", "staff"
    private boolean isActive;

    // Constructor for admin and staff accounts
    public User(int id, String name, String studentId, String email, String contactNumber, String username, String password, String role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.contactNumber = contactNumber;
        this.course = "N/A"; // Default value for course
        this.section = "N/A"; // Default value for section
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    // Constructor for customer accounts
    public User(int id, String name, String studentId, String email, String contactNumber, String course, String section, String username, String password, String role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.contactNumber = contactNumber;
        this.course = course;
        this.section = section;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters and setters for all fields
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String toFileString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%b",
            id,
            name,
            studentId,
            email,
            contactNumber,
            course,
            section,
            username,
            password,
            isActive
        );
    }
}
