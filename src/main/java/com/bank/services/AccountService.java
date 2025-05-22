package com.bank.services;

import com.bank.database.DatabaseConnection;
import com.bank.exceptions.DatabaseException;
import com.bank.models.Account;
import com.bank.models.AccountOperations;
import com.bank.models.SimpleAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AccountService implements AccountOperations {
    private static final String INSERT_ACCOUNT = 
        "INSERT INTO tblAccount (acct_no, customer_name, phone_number, sex, branch, initial_balance) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_ACCOUNT = 
        "UPDATE tblAccount SET customer_name = ?, phone_number = ?, sex = ?, branch = ?, initial_balance = ? WHERE acct_no = ?";
    private static final String SELECT_ACCOUNT = 
        "SELECT acct_no, customer_name, phone_number, sex, branch, initial_balance FROM tblAccount WHERE acct_no = ?";
    private static final String SELECT_ALL_ACCOUNTS = 
        "SELECT acct_no, customer_name, phone_number, sex, branch, initial_balance FROM tblAccount";
    private static final String DELETE_ACCOUNT = 
        "DELETE FROM tblAccount WHERE acct_no = ?";

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone est requis");
        }
        if (!phoneNumber.matches("^[234][0-9]{7}$")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit commencer par 2, 3 ou 4 et contenir exactement 8 chiffres");
        }
    }


    @Override
    public void createAccount(Account account) throws DatabaseException {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }

        validatePhoneNumber(account.getPhoneNumber());

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_ACCOUNT)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getCustomerName());
            pstmt.setString(3, account.getPhoneNumber());
            pstmt.setString(4, account.getSex());
            pstmt.setString(5, account.getBranch());
            pstmt.setDouble(6, account.getBalance());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la création du compte : " + e.getMessage());
        }
    }


    @Override
    public Optional<Account> findAccount(String accountNumber) throws DatabaseException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ACCOUNT)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(new SimpleAccount(
                    rs.getString("acct_no"),
                    rs.getString("customer_name"),
                    rs.getString("phone_number"),
                    rs.getString("sex"),
                    rs.getString("branch"),
                    rs.getDouble("initial_balance")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du compte : " + e.getMessage());
        }
    }


    @Override
    public void updateAccount(Account account) throws DatabaseException {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }

        validatePhoneNumber(account.getPhoneNumber());

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_ACCOUNT)) {
            
            pstmt.setString(1, account.getCustomerName());
            pstmt.setString(2, account.getPhoneNumber());
            pstmt.setString(3, account.getSex());
            pstmt.setString(4, account.getBranch());
            pstmt.setDouble(5, account.getBalance());
            pstmt.setString(6, account.getAccountNumber());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour du compte : " + e.getMessage());
        }
    }


    @Override
    public void deleteAccount(String accountNumber) throws DatabaseException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_ACCOUNT)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du compte : " + e.getMessage());
        }
    }


    @Override
    public List<Account> getAllAccounts() throws DatabaseException {
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_ACCOUNTS)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                accounts.add(new SimpleAccount(
                    rs.getString("acct_no"),
                    rs.getString("customer_name"),
                    rs.getString("phone_number"),
                    rs.getString("sex"),
                    rs.getString("branch"),
                    rs.getDouble("initial_balance")
                ));
            }
            return accounts;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des comptes : " + e.getMessage());
        }
    }

    public Optional<Account> findAccountByPhone(String phoneNumber) throws DatabaseException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone est requis");
        }
        validatePhoneNumber(phoneNumber);
        String sql = "SELECT acct_no, customer_name, phone_number, sex, branch, initial_balance FROM tblAccount WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new SimpleAccount(
                    rs.getString("acct_no"),
                    rs.getString("customer_name"),
                    rs.getString("phone_number"),
                    rs.getString("sex"),
                    rs.getString("branch"),
                    rs.getDouble("initial_balance")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du compte : " + e.getMessage());
        }
    }
}