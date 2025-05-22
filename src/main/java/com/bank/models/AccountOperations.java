package com.bank.models;

import com.bank.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

public interface AccountOperations {
    void createAccount(Account account) throws DatabaseException;
    Optional<Account> findAccount(String accountNumber) throws DatabaseException;
    void updateAccount(Account account) throws DatabaseException;
    void deleteAccount(String accountNumber) throws DatabaseException;
    List<Account> getAllAccounts() throws DatabaseException;
} 