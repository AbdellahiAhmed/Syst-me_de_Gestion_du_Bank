package com.bank.services;

import com.bank.database.DatabaseConnection;
import com.bank.exceptions.DatabaseException;
import com.bank.models.Account;
import com.bank.models.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private final DatabaseConnection dbConnection;
    private final AccountService accountService;

    public TransactionService() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.accountService = new AccountService();
    }


    public void deposit(String accountNumber, double amount) throws DatabaseException {
        validateTransactionInput(accountNumber, amount);

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<Account> accountOpt = accountService.findAccount(accountNumber);
                if (accountOpt.isEmpty()) {
                    throw new DatabaseException("Account not found");
                }

                Account account = accountOpt.get();
                double newBalance = account.getBalance() + amount;
                account.setBalance(newBalance);
                accountService.updateAccount(account);

                Transaction transaction = new Transaction(accountNumber, account.getCustomerName(), amount, 0.0, newBalance);
                saveTransaction(transaction, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new DatabaseException("Error during deposit: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Connection error during deposit: " + e.getMessage(), e);
        }
    }


    public void withdraw(String accountNumber, double amount) throws DatabaseException {
        validateTransactionInput(accountNumber, amount);

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<Account> accountOpt = accountService.findAccount(accountNumber);
                if (accountOpt.isEmpty()) {
                    throw new DatabaseException("Account not found");
                }

                Account account = accountOpt.get();
                if (account.getBalance() < amount) {
                    throw new DatabaseException(String.format(
                        "Insufficient balance. Current balance: %.2f, Requested amount: %.2f",
                        account.getBalance(), amount
                    ));
                }

                double newBalance = account.getBalance() - amount;
                account.setBalance(newBalance);
                accountService.updateAccount(account);

                Transaction transaction = new Transaction(accountNumber, account.getCustomerName(), 0.0, amount, newBalance);
                saveTransaction(transaction, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new DatabaseException("Error during withdrawal: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Connection error during withdrawal: " + e.getMessage(), e);
        }
    }

    public void transfer(String fromAccount, String toAccount, double amount) throws DatabaseException {
        // Vérifier si le compte source a suffisamment de fonds
        Optional<Account> sourceAccountOpt = accountService.findAccount(fromAccount);
        if (sourceAccountOpt.isEmpty()) {
            throw new DatabaseException("Compte source introuvable");
        }

        Account sourceAccount = sourceAccountOpt.get();
        if (sourceAccount.getBalance() < amount) {
            throw new DatabaseException("Solde insuffisant");
        }

        // Vérifier si le compte destinataire existe
        Optional<Account> destAccountOpt = accountService.findAccount(toAccount);
        if (destAccountOpt.isEmpty()) {
            throw new DatabaseException("Compte destinataire introuvable");
        }

        // Effectuer le retrait du compte source
        withdraw(fromAccount, amount);

        // Effectuer le dépôt sur le compte destinataire
        deposit(toAccount, amount);
    }

    private void validateTransactionInput(String accountNumber, double amount) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }


    private void saveTransaction(Transaction transaction, Connection conn) throws SQLException {
        String sql = "INSERT INTO transactions(transaction_id, acct_no, customer_name, deposit, withdraw, balance, date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            long transactionId = System.currentTimeMillis() * 1000 + (long)(Math.random() * 1000);
            pstmt.setLong(1, transactionId);
            pstmt.setString(2, transaction.getAccountNumber());
            pstmt.setString(3, transaction.getCustomerName());
            pstmt.setDouble(4, transaction.getDeposit());
            pstmt.setDouble(5, transaction.getWithdraw());
            pstmt.setDouble(6, transaction.getBalance());
            pstmt.executeUpdate();
            transaction.setTransactionId(transactionId);
        }
    }


    public List<Transaction> getAccountTransactions(String accountNumber) throws DatabaseException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }

        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE acct_no = ? ORDER BY date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        }
    }


    public List<Transaction> getAllTransactions() throws DatabaseException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        }
    }


    public Optional<Transaction> getTransactionById(long transactionId) throws DatabaseException {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Transaction ID must be positive");
        }

        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createTransactionFromResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding transaction: " + e.getMessage(), e);
        }
    }


    public List<Transaction> searchTransactions(String searchCriteria) throws DatabaseException {
        if (searchCriteria == null || searchCriteria.trim().isEmpty()) {
            throw new IllegalArgumentException("Search criteria cannot be empty");
        }

        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE acct_no LIKE ? OR customer_name LIKE ? ORDER BY date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchCriteria.trim() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new DatabaseException("Error searching transactions: " + e.getMessage(), e);
        }
    }

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
                Transaction transaction = new Transaction(
                        rs.getString("acct_no"),
                        rs.getString("customer_name"),
                        rs.getDouble("deposit"),
                        rs.getDouble("withdraw"),
                        rs.getDouble("balance")
                );
        transaction.setTransactionId(rs.getLong("transaction_id"));
                transaction.setDate(rs.getTimestamp("date"));
        return transaction;
    }


    public void deleteTransaction(long transactionId) throws DatabaseException {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("L'identifiant de la transaction doit être positif");
            }

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Vérifier si la transaction existe
                Optional<Transaction> transactionOpt = getTransactionById(transactionId);
                if (transactionOpt.isEmpty()) {
                    throw new DatabaseException("Transaction non trouvée");
                }

                Transaction transaction = transactionOpt.get();
                
                // Supprimer la transaction
                String sql = "DELETE FROM transactions WHERE transaction_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setLong(1, transactionId);
                    if (pstmt.executeUpdate() == 0) {
                        throw new DatabaseException("Erreur lors de la suppression de la transaction");
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new DatabaseException("Erreur lors de la suppression de la transaction : " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur de connexion lors de la suppression de la transaction : " + e.getMessage(), e);
        }
    }
}