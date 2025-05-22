package com.bank.models;

public class User {
    private String username;
    private String password;
    private String role;
    private String accountNumber;
    private String phoneNumber;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role, String accountNumber) {
        this(username, password, role);
        this.accountNumber = accountNumber;
    }

    public User(String username, String password, String role, String accountNumber, String phoneNumber) {
        this(username, password, role, accountNumber);
        this.phoneNumber = phoneNumber;
    }

    // Getters et Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
} 