package com.bank.services;

import com.bank.models.User;
import com.bank.database.DatabaseConnection;
import com.bank.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.SecureRandom;
import java.util.Base64;


public class UserService {
    private static final String AUTHENTICATE_BY_USERNAME = 
        "SELECT * FROM users WHERE username = ? AND password = ?";
    private static final String AUTHENTICATE_BY_PHONE = 
        "SELECT u.* FROM users u JOIN tblAccount a ON u.account_number = a.acct_no WHERE a.phone_number = ? AND u.password = ?";
    private static final String UPDATE_PASSWORD = 
        "UPDATE users SET password = ? WHERE username = ?";
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        // Au moins une majuscule
        password.append(UPPER.charAt(secureRandom.nextInt(UPPER.length())));
        // Au moins une minuscule
        password.append(LOWER.charAt(secureRandom.nextInt(LOWER.length())));
        // Au moins un chiffre
        password.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
        // Au moins un caractère spécial
        password.append(SPECIAL.charAt(secureRandom.nextInt(SPECIAL.length())));
        
        // Ajouter 4 caractères aléatoires supplémentaires
        String allChars = UPPER + LOWER + DIGITS + SPECIAL;
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }
        
        // Mélanger le mot de passe
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }

    public User authenticate(String identifier, String password, String loginType) throws DatabaseException {
        String sql = "Nom d'utilisateur".equals(loginType) ? AUTHENTICATE_BY_USERNAME : AUTHENTICATE_BY_PHONE;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, identifier);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("account_number"),
                    rs.getString("phone_number")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'authentification : " + e.getMessage());
        }
    }

    public String createUser(User user) throws DatabaseException {
        String generatedPassword = generateRandomPassword();
        String sql = "INSERT INTO users (username, password, role, account_number, phone_number) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, generatedPassword);
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getAccountNumber());
            pstmt.setString(5, user.getPhoneNumber());
            
            pstmt.executeUpdate();
            return generatedPassword;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }

    public void updateUserPassword(String username, String newPassword) throws DatabaseException {
        if (newPassword == null || newPassword.length() < 8) {
            throw new DatabaseException("Le mot de passe doit contenir au moins 8 caractères");
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PASSWORD)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new DatabaseException("Utilisateur non trouvé");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
        }
    }

    public User authenticateByPhone(String phone, String password) throws DatabaseException {
        String sql = AUTHENTICATE_BY_PHONE;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("account_number"),
                    rs.getString("phone_number")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'authentification : " + e.getMessage());
        }
    }
} 