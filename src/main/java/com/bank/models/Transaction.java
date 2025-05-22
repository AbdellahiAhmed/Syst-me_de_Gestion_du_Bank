package com.bank.models;

import java.util.Date;
import java.util.Objects;

public class Transaction {
    private long transactionId;
    private String accountNumber;
    private String customerName;
    private double deposit;
    private double withdraw;
    private double balance;
    private Date date;

    public Transaction() {
        this.date = new Date();
    }

    public Transaction(String accountNumber, String customerName, double deposit, double withdraw, double balance) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.deposit = deposit;
        this.withdraw = withdraw;
        this.balance = balance;
        this.date = new Date();
    }

    // Getters et Setters
    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return String.format("Transaction{transactionId=%d, accountNumber='%s', customerName='%s', deposit=%.2f, withdraw=%.2f, balance=%.2f, date=%s}",
            transactionId, accountNumber, customerName, deposit, withdraw, balance, date);
    }
}