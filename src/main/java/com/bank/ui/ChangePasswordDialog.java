package com.bank.ui;

import com.bank.services.UserService;
import com.bank.exceptions.DatabaseException;
import com.bank.utils.StyleUtil;

import javax.swing.*;
import java.awt.*;


public class ChangePasswordDialog extends JDialog {
    private final UserService userService;
    private final String username;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ChangePasswordDialog(Frame parent, String username) {
        super(parent, "Changer le mot de passe", true);
        this.username = username;
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 420);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Panel principal avec un dégradé de couleur
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(0, 153, 153);
                Color color2 = new Color(0, 102, 102);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Panel de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(255, 255, 255, 230));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Titre
        JLabel titleLabel = new JLabel("Changer le mot de passe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(StyleUtil.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Utilisateur
        JLabel userLabel = StyleUtil.createStyledLabel("Utilisateur: " + username);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nouveau mot de passe
        JLabel newPassLabel = StyleUtil.createStyledLabel("Nouveau mot de passe");
        newPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(newPassLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        newPasswordField = StyleUtil.createStyledPasswordField();
        newPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(newPasswordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Confirmation
        JLabel confirmLabel = StyleUtil.createStyledLabel("Confirmer le mot de passe");
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(confirmLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        confirmPasswordField = StyleUtil.createStyledPasswordField();
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Bouton de confirmation
        JButton confirmButton = StyleUtil.createStyledButton("Confirmer");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setMaximumSize(new Dimension(200, 40));
        formPanel.add(confirmButton);

        // Centrer le formulaire
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        confirmButton.addActionListener(e -> handlePasswordChange());

        setContentPane(mainPanel);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void handlePasswordChange() {
        try {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                throw new IllegalArgumentException("Veuillez remplir tous les champs");
            }
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
            }
            if (!isValidPassword(newPassword)) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères, dont une majuscule, une minuscule, un chiffre et un caractère spécial");
            }

            userService.updateUserPassword(username, newPassword);
            JOptionPane.showMessageDialog(this,
                "Mot de passe modifié avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du changement de mot de passe: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
} 