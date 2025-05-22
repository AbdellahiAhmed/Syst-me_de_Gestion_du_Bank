package com.bank.ui;

import com.bank.models.User;
import com.bank.services.UserService;
import com.bank.exceptions.DatabaseException;
import com.bank.utils.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final UserService userService;
    private static final int MIN_WIDTH = 450;
    private static final int MIN_HEIGHT = 600;

    public LoginFrame() {
        userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Connexion - Système Bancaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MIN_WIDTH, MIN_HEIGHT);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(true);

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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Logo et titre dans un panel centré
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Logo
        JLabel logoLabel = new JLabel("🏦", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Titre
        JLabel titleLabel = new JLabel("Système Bancaire", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de formulaire avec effet de transparence
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(255, 255, 255, 230));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Connexion", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        subtitleLabel.setForeground(StyleUtil.PRIMARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Username
        JLabel usernameLabel = StyleUtil.createStyledLabel("Numéro de téléphone");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(usernameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        usernameField = StyleUtil.createStyledTextField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setToolTipText("Numéro de téléphone (ou 'admin' pour l'administrateur)");
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password
        JLabel passwordLabel = StyleUtil.createStyledLabel("Mot de passe");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        passwordField = StyleUtil.createStyledPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Boutons
        JButton loginButton = StyleUtil.createStyledButton("Se connecter");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(loginButton);

        // Centrer le formulaire
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Actions des boutons
        loginButton.addActionListener(e -> handleLogin());

        // Gestionnaire de redimensionnement
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                
                // Ajuster la taille des champs de texte en fonction de la largeur de la fenêtre
                int fieldWidth = Math.min(width - 100, 400);
                Dimension newFieldSize = new Dimension(fieldWidth, 35);
                usernameField.setPreferredSize(newFieldSize);
                usernameField.setMaximumSize(newFieldSize);
                passwordField.setPreferredSize(newFieldSize);
                passwordField.setMaximumSize(newFieldSize);
                
                // Ajuster la taille des boutons
                int buttonWidth = Math.min(width - 100, 300);
                Dimension newButtonSize = new Dimension(buttonWidth, 40);
                loginButton.setPreferredSize(newButtonSize);
                loginButton.setMaximumSize(newButtonSize);
                
                // Forcer la mise à jour de l'interface
                revalidate();
                repaint();
            }
        });

        setContentPane(mainPanel);
    }

    private void handleLogin() {
        String loginInput = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (loginInput.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        // Cas admin
        if (loginInput.equals("admin") && password.equals("admin123")) {
            new MainMenu().setVisible(true);
            this.dispose();
            return;
        }

        // Validation du numéro de téléphone utilisateur
        if (!loginInput.matches("^[234][0-9]{7}$")) {
            showError("Le numéro de téléphone doit commencer par 2, 3 ou 4 et contenir exactement 8 chiffres");
            return;
        }

        try {
            User user = userService.authenticateByPhone(loginInput, password);
            if (user != null) {
                new UserDashboard(user).setVisible(true);
                this.dispose();
            } else {
                showError("Identifiants incorrects");
            }
        } catch (DatabaseException ex) {
            showError("Erreur lors de l'authentification : " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE);
    }
} 