package com.bank.utils;

import javax.swing.*;
import java.awt.*;

public class StyleUtil {
    // Couleurs
    public static final Color PRIMARY_COLOR = new Color(0, 153, 153);
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    public static final Color PANEL_COLOR = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(180, 220, 240);
    public static final Color TEXT_COLOR = new Color(51, 51, 51);
    public static final Color ERROR_COLOR = new Color(220, 53, 69);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    // Polices
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Dimensions
    public static final Dimension BUTTON_SIZE = new Dimension(300, 40);
    public static final Dimension TEXT_FIELD_SIZE = new Dimension(300, 35);
    public static final int PADDING = 20;

    public static void applyGlobalStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Style global pour les boutons
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Button.focusPainted", false);
            UIManager.put("Button.borderPainted", false);
            
            // Style global pour les champs de texte
            UIManager.put("TextField.background", PANEL_COLOR);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextField.font", TEXT_FONT);
            UIManager.put("TextField.caretForeground", PRIMARY_COLOR);
            
            // Style global pour les labels
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("Label.font", LABEL_FONT);
            
            // Style global pour les panneaux
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            
            // Style global pour les dialogues
            UIManager.put("OptionPane.background", PANEL_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            UIManager.put("OptionPane.messageFont", TEXT_FONT);
            UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style global : " + e.getMessage());
        }
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }

    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(TEXT_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(PANEL_COLOR);
        textField.setPreferredSize(TEXT_FIELD_SIZE);
        textField.setMaximumSize(TEXT_FIELD_SIZE);
        return textField;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(TEXT_FONT);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBackground(PANEL_COLOR);
        passwordField.setPreferredSize(TEXT_FIELD_SIZE);
        passwordField.setMaximumSize(TEXT_FIELD_SIZE);
        return passwordField;
    }

    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        return panel;
    }
} 