package com.bank.models;

import java.util.Objects;


public abstract class Account {
    protected String accountNumber;
    protected String customerName;
    protected String phoneNumber;
    protected String sex;
    protected String branch;
    protected double balance;

    protected Account() {
    }

    protected Account(String accountNumber, String customerName, String phoneNumber, String sex, String branch, double balance) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.branch = branch;
        this.balance = balance;
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSex() {
        return sex;
    }

    public String getBranch() {
        return branch;
    }

    public double getBalance() {
        return balance;
    }

    // Setters
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract void deposit(double amount);
    public abstract void withdraw(double amount);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return String.format("Account{accountNumber='%s', customerName='%s', sex='%s', branch='%s', balance=%.2f}",
            accountNumber, customerName, sex, branch, balance);
    }
}