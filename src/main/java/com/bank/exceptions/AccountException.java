package com.bank.exceptions;

// Exception spécifique à la gestion des comptes
public class AccountException extends BusinessException {
    public AccountException(String message) {
        super(message);
    }
    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
