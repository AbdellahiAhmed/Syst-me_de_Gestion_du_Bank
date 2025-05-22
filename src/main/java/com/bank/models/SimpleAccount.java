package com.bank.models;

public class SimpleAccount extends Account {
    public SimpleAccount(String accountNumber, String customerName, String phoneNumber, String sex, String branch, double balance) {
        super(accountNumber, customerName, phoneNumber, sex, branch, balance);
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être positif");
        }
        this.balance += amount;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("Solde insuffisant pour effectuer le retrait");
        }
        this.balance -= amount;
    }
} 