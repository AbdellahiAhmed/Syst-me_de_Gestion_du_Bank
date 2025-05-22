package com.bank.ui;

import com.bank.database.DatabaseConnection;
import com.bank.exceptions.DatabaseException;
import com.bank.models.Account;
import com.bank.models.SimpleAccount;
import com.bank.models.Transaction;
import com.bank.services.AccountService;
import com.bank.services.TransactionService;
import com.bank.models.User;
import com.bank.services.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.text.SimpleDateFormat;
import javax.swing.ListSelectionModel;
import java.util.Optional;


public class MainMenu extends JFrame {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private JTable transactionTable;
    private String currentSearchCriteria;
    private final UserService userService;

    // Champs et boutons du panel de création de compte
    private JTextField searchField, acctNoField, nameField, balanceField;
    private JComboBox<String> sexBox, branchBox;
    private JButton searchBtn, openBtn, modifyBtn, deleteBtn, clearBtn;
    // Champs et boutons du panel de dépôt
    private JTextField depositSearchField, depositNameField, depositBalanceField, depositAmountField;
    private JButton depositSearchBtn, depositBtn, depositClearBtn;
    // Champs et boutons du panel de retrait
    private JTextField withdrawSearchField, withdrawNameField, withdrawBalanceField, withdrawAmountField;
    private JButton withdrawSearchBtn, withdrawBtn, withdrawClearBtn;
    // Champs et boutons du panel de transactions
    private JTextField transactionSearchField;
    private JButton transactionSearchBtn, viewAllBtn, deleteTransactionBtn;
    private JLabel statusLabel; // Nouveau label pour le statut
    private JTextField phoneField; // Nouveau champ pour le téléphone

    public MainMenu() {
        // Initialisation des services
        accountService = new AccountService();
        transactionService = new TransactionService();
        userService = new UserService();
        currentSearchCriteria = null;

        // Configuration de la fenêtre
        setTitle("Banking Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);

        // Création de l'interface stylée
        createStyledLayout();

        // Configuration des événements
        setupEventHandlers();

        // Ajouter la validation des champs numériques
        setupNumericValidation();

        // Créer la barre de statut
        createStatusBar();

        // Gestionnaire pour la fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.getInstance().closeConnection();
            }
        });
    }

    private void createStyledLayout() {
        // Panel principal avec un dégradé de couleur
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // En-tête avec le titre et le bouton de déconnexion
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 240), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Système de Gestion Bancaire");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 153, 153));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Bouton Déconnexion en haut à droite
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(event -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    new com.bank.ui.LoginFrame().setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erreur lors du retour à la page de connexion : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 28));
        tabbedPane.setBackground(new Color(180, 200, 220));
        tabbedPane.setForeground(new Color(0, 80, 120));
        tabbedPane.addTab("Création de Compte", createStyledAccountPanel());
        tabbedPane.addTab("Dépôt d'Argent", createStyledDepositPanel());
        tabbedPane.addTab("Retrait d'Argent", createStyledWithdrawPanel());
        tabbedPane.addTab("Historique des Transactions", createStyledTransactionPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createStyledAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel sectionTitle = new JLabel("Open Bank Account");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        sectionTitle.setForeground(new Color(0, 153, 153));
        sectionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(sectionTitle, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new LineBorder(new Color(180, 220, 240), 2, true));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Enter Acct No:"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(16);
        formPanel.add(searchField, gbc);
        gbc.gridx = 2;
        searchBtn = createStyledButton("Rechercher");
        searchBtn.setPreferredSize(new Dimension(150, 40));
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(searchBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Acct No:"), gbc);
        gbc.gridx = 1;
        acctNoField = new JTextField(16);
        formPanel.add(acctNoField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(16);
        formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(16);
        formPanel.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Sex:"), gbc);
        gbc.gridx = 1;
        sexBox = new JComboBox<>(new String[]{"Select Sex", "Male", "Female"});
        formPanel.add(sexBox, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1;
        branchBox = new JComboBox<>(new String[]{"Select Branch", "Central", "North", "South"});
        formPanel.add(branchBox, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Initial Balance:"), gbc);
        gbc.gridx = 1;
        balanceField = new JTextField(16);
        formPanel.add(balanceField, gbc);
        openBtn = createStyledButton("Créer");
        modifyBtn = createStyledButton("Modifier");
        deleteBtn = createStyledButton("Supprimer");
        clearBtn = createStyledButton("Effacer");
        JButton changePwdBtn = createStyledButton("Changer le mot de passe");
        changePwdBtn.setPreferredSize(new Dimension(200, 40));
        changePwdBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        changePwdBtn.addActionListener(e -> {
            String username = nameField.getText().trim();
            if (username.isEmpty()) {
                showCustomMessage("Veuillez sélectionner ou entrer le nom d'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ChangePasswordDialog dialog = new ChangePasswordDialog(this, username);
            dialog.setVisible(true);
        });
        openBtn.setPreferredSize(new Dimension(150, 40));
        modifyBtn.setPreferredSize(new Dimension(150, 40));
        deleteBtn.setPreferredSize(new Dimension(150, 40));
        clearBtn.setPreferredSize(new Dimension(150, 40));
        openBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        modifyBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(openBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(modifyBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(deleteBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(clearBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(changePwdBtn);
        btnPanel.add(Box.createHorizontalGlue());
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        formPanel.add(btnPanel, gbc);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStyledDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel sectionTitle = new JLabel("Deposit Money");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        sectionTitle.setForeground(new Color(0, 153, 153));
        sectionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(sectionTitle, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new LineBorder(new Color(180, 220, 240), 2, true));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Enter Acct No:"), gbc);
        gbc.gridx = 1;
        depositSearchField = new JTextField(16);
        formPanel.add(depositSearchField, gbc);
        gbc.gridx = 2;
        depositSearchBtn = createStyledButton("Search");
        depositSearchBtn.setPreferredSize(new Dimension(150, 40));
        depositSearchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(depositSearchBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        depositNameField = new JTextField(16);
        formPanel.add(depositNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Current Balance:"), gbc);
        gbc.gridx = 1;
        depositBalanceField = new JTextField(16);
        formPanel.add(depositBalanceField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        depositAmountField = new JTextField(16);
        formPanel.add(depositAmountField, gbc);
        depositBtn = createStyledButton("Deposit");
        depositClearBtn = createStyledButton("Clear");
        depositBtn.setPreferredSize(new Dimension(150, 40));
        depositClearBtn.setPreferredSize(new Dimension(150, 40));
        depositBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        depositClearBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(depositBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(depositClearBtn);
        btnPanel.add(Box.createHorizontalGlue());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        formPanel.add(btnPanel, gbc);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStyledWithdrawPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel sectionTitle = new JLabel("Withdraw Money");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        sectionTitle.setForeground(new Color(0, 153, 153));
        sectionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(sectionTitle, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new LineBorder(new Color(180, 220, 240), 2, true));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Enter Acct No:"), gbc);
        gbc.gridx = 1;
        withdrawSearchField = new JTextField(16);
        formPanel.add(withdrawSearchField, gbc);
        gbc.gridx = 2;
        withdrawSearchBtn = createStyledButton("Search");
        withdrawSearchBtn.setPreferredSize(new Dimension(150, 40));
        withdrawSearchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(withdrawSearchBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        withdrawNameField = new JTextField(16);
        formPanel.add(withdrawNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Current Balance:"), gbc);
        gbc.gridx = 1;
        withdrawBalanceField = new JTextField(16);
        formPanel.add(withdrawBalanceField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        withdrawAmountField = new JTextField(16);
        formPanel.add(withdrawAmountField, gbc);
        withdrawBtn = createStyledButton("Withdraw");
        withdrawClearBtn = createStyledButton("Clear");
        withdrawBtn.setPreferredSize(new Dimension(150, 40));
        withdrawClearBtn.setPreferredSize(new Dimension(150, 40));
        withdrawBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        withdrawClearBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(withdrawBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(withdrawClearBtn);
        btnPanel.add(Box.createHorizontalGlue());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        formPanel.add(btnPanel, gbc);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStyledTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel sectionTitle = new JLabel("Bank Transactions");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        sectionTitle.setForeground(new Color(0, 153, 153));
        sectionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(sectionTitle, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(new Color(245, 247, 250));
        transactionSearchField = new JTextField(20);
        transactionSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        transactionSearchBtn = createStyledButton("Search");
        viewAllBtn = createStyledButton("View All");
        deleteTransactionBtn = createStyledButton("Delete Selected");
        transactionSearchBtn.setPreferredSize(new Dimension(150, 40));
        viewAllBtn.setPreferredSize(new Dimension(150, 40));
        deleteTransactionBtn.setPreferredSize(new Dimension(150, 40));
        transactionSearchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteTransactionBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchPanel.add(transactionSearchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(transactionSearchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(viewAllBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(deleteTransactionBtn);
        panel.add(searchPanel, BorderLayout.NORTH);
        String[] columnNames = {"Transaction ID", "Account No", "Customer Name", "Deposit", "Withdraw", "Balance", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(model);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        transactionTable.setRowHeight(30);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        transactionTable.getTableHeader().setBackground(new Color(180, 220, 240));
        transactionTable.getTableHeader().setForeground(new Color(0, 80, 120));
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(new LineBorder(new Color(180, 220, 240), 2, true));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 153, 153));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 133, 133));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 153));
            }
        });
        
        return button;
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 220, 240)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        statusLabel = new JLabel("Prêt");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void updateStatus() {
        if (currentSearchCriteria != null) {
            statusLabel.setText("Recherche : " + currentSearchCriteria);
        } else {
            statusLabel.setText("Prêt");
        }
    }

    private void setupEventHandlers() {
        searchBtn.addActionListener(event -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                showCustomMessage("Veuillez entrer un numéro de compte", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Optional<Account> account = accountService.findAccount(searchText);
                if (account.isPresent()) {
                    Account acc = account.get();
                    acctNoField.setText(acc.getAccountNumber());
                    nameField.setText(acc.getCustomerName());
                    phoneField.setText(acc.getPhoneNumber());
                    sexBox.setSelectedItem(acc.getSex());
                    branchBox.setSelectedItem(acc.getBranch());
                    formatAmountField(balanceField, acc.getBalance());
                    currentSearchCriteria = searchText;
                    updateStatus();
                } else {
                    showCustomMessage("Compte non trouvé", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (DatabaseException ex) {
                showCustomMessage("Erreur lors de la recherche : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        openBtn.addActionListener(e -> {
            try {
                validateAccountFields();
                Account account = new SimpleAccount(
                    acctNoField.getText().trim(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    (String) sexBox.getSelectedItem(),
                    (String) branchBox.getSelectedItem(),
                    Double.parseDouble(balanceField.getText().trim())
                );
                accountService.createAccount(account);
                String phone = phoneField.getText().trim();
                String password = userService.createUser(new User(
                    nameField.getText().trim(),
                    null,
                    "USER",
                    acctNoField.getText().trim(),
                    phone
                ));
                String message = String.format(
                    "Compte créé avec succès !\n\n" +
                    "Informations de connexion :\n" +
                    "Numéro de téléphone : %s\n" +
                    "Mot de passe : %s\n\n" +
                    "Note : Vous devez utiliser votre numéro de téléphone pour vous connecter.",
                    phone, password
                );
                showCustomMessage(message, "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearFields(acctNoField, nameField, phoneField, sexBox, branchBox, balanceField);
            } catch (NumberFormatException ex) {
                showCustomMessage("Format de solde invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                showCustomMessage(ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                showCustomMessage("Erreur lors de la création du compte : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                showCustomMessage("Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        modifyBtn.addActionListener(e -> {
            try {
                validateAccountFields();
                Account account = new SimpleAccount(
                    acctNoField.getText().trim(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    (String) sexBox.getSelectedItem(),
                    (String) branchBox.getSelectedItem(),
                    Double.parseDouble(balanceField.getText().trim())
                );
                accountService.updateAccount(account);
                showCustomMessage("Account updated successfully", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearFields(acctNoField, nameField, phoneField, sexBox, branchBox, balanceField);
            } catch (IllegalArgumentException ex) {
                showCustomMessage(ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                showCustomMessage("Erreur lors de la mise à jour du compte : " + ex.getMessage(), 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            String accountNumber = acctNoField.getText().trim();
            if (accountNumber.isEmpty()) {
                showCustomMessage("Veuillez entrer un numéro de compte", 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            showCustomConfirmDialog("Êtes-vous sûr de vouloir supprimer ce compte ?", 
                "Confirmer la suppression", confirmEvent -> {
                    try {
                        accountService.deleteAccount(accountNumber);
                        showCustomMessage("Account deleted successfully", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        clearFields(acctNoField, nameField, phoneField, sexBox, branchBox, balanceField);
                    } catch (DatabaseException ex) {
                        showCustomMessage("Erreur lors de la suppression du compte : " + ex.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                });
        });

        clearBtn.addActionListener(e -> clearFields(acctNoField, nameField, phoneField, sexBox, branchBox, balanceField));

        depositSearchBtn.addActionListener(e -> {
            String accountNumber = depositSearchField.getText().trim();
            if (accountNumber.isEmpty()) {
                showCustomMessage("Veuillez entrer un numéro de compte", 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Optional<Account> accountOpt = accountService.findAccount(accountNumber);
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();
                    depositNameField.setText(account.getCustomerName());
                    formatAmountField(depositBalanceField, account.getBalance());
                } else {
                    showCustomMessage("Account not found", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DatabaseException ex) {
                showCustomMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        depositBtn.addActionListener(e -> {
            try {
                String accountNumber = depositSearchField.getText().trim();
                if (accountNumber.isEmpty()) {
                    throw new IllegalArgumentException("Veuillez entrer un numéro de compte");
                }
                Optional<Account> accountOpt = accountService.findAccount(accountNumber);
                if (accountOpt.isEmpty()) {
                    throw new IllegalArgumentException("Compte introuvable");
                }
                String amountStr = depositAmountField.getText().trim();
                if (amountStr.isEmpty()) {
                    throw new IllegalArgumentException("Veuillez entrer un montant");
                }
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    throw new IllegalArgumentException("Le montant doit être positif");
                }
                transactionService.deposit(accountNumber, amount);
                showCustomMessage("Dépôt effectué avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearDepositFields();
            } catch (NumberFormatException ex) {
                showCustomMessage("Format de montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                showCustomMessage(ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                showCustomMessage("Erreur lors du dépôt : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                showCustomMessage("Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        depositClearBtn.addActionListener(e -> {
            depositSearchField.setText("");
            depositNameField.setText("");
            depositBalanceField.setText("");
            depositAmountField.setText("");
        });

        withdrawSearchBtn.addActionListener(e -> {
            String accountNumber = withdrawSearchField.getText().trim();
            if (accountNumber.isEmpty()) {
                showCustomMessage("Veuillez entrer un numéro de compte", 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Optional<Account> accountOpt = accountService.findAccount(accountNumber);
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();
                    withdrawNameField.setText(account.getCustomerName());
                    formatAmountField(withdrawBalanceField, account.getBalance());
                } else {
                    showCustomMessage("Account not found", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DatabaseException ex) {
                showCustomMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        withdrawBtn.addActionListener(e -> {
            try {
                String accountNumber = withdrawSearchField.getText().trim();
                if (accountNumber.isEmpty()) {
                    throw new IllegalArgumentException("Veuillez entrer un numéro de compte");
                }
                Optional<Account> accountOpt = accountService.findAccount(accountNumber);
                if (accountOpt.isEmpty()) {
                    throw new IllegalArgumentException("Compte introuvable");
                }
                String amountStr = withdrawAmountField.getText().trim();
                if (amountStr.isEmpty()) {
                    throw new IllegalArgumentException("Veuillez entrer un montant");
                }
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    throw new IllegalArgumentException("Le montant doit être positif");
                }
                transactionService.withdraw(accountNumber, amount);
                showCustomMessage("Retrait effectué avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearWithdrawFields();
            } catch (NumberFormatException ex) {
                showCustomMessage("Format de montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                showCustomMessage(ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException ex) {
                showCustomMessage("Erreur lors du retrait : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                showCustomMessage("Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        withdrawClearBtn.addActionListener(e -> {
            withdrawSearchField.setText("");
            withdrawNameField.setText("");
            withdrawBalanceField.setText("");
            withdrawAmountField.setText("");
        });

        transactionSearchBtn.addActionListener(e -> {
            String searchCriteria = transactionSearchField.getText().trim();
            if (searchCriteria.isEmpty()) {
                showCustomMessage("Veuillez entrer un critère de recherche", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                List<Transaction> transactions = transactionService.searchTransactions(searchCriteria);
                updateTransactionTable(transactionTable, transactions);
                currentSearchCriteria = searchCriteria;
            } catch (DatabaseException ex) {
                showCustomMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewAllBtn.addActionListener(e -> {
            try {
                List<Transaction> transactions = transactionService.getAllTransactions();
                updateTransactionTable(transactionTable, transactions);
                currentSearchCriteria = null;
            } catch (DatabaseException ex) {
                showCustomMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteTransactionBtn.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow == -1) {
                showCustomMessage("Veuillez sélectionner une transaction à supprimer", 
                                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            showCustomConfirmDialog("Êtes-vous sûr de vouloir supprimer cette transaction ?", 
                "Confirmer la suppression", confirmEvent -> {
                    try {
                        long transactionId = (long) transactionTable.getValueAt(selectedRow, 0);
                        transactionService.deleteTransaction(transactionId);
                        showCustomMessage("Transaction supprimée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir la table
                        if (currentSearchCriteria != null) {
                            List<Transaction> transactions = transactionService.searchTransactions(currentSearchCriteria);
                            updateTransactionTable(transactionTable, transactions);
                        } else {
                            List<Transaction> transactions = transactionService.getAllTransactions();
                            updateTransactionTable(transactionTable, transactions);
                        }
                    } catch (DatabaseException ex) {
                        showCustomMessage("Erreur lors de la suppression de la transaction : " + ex.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                });
        });
    }

    private void validateAccountFields() {
        String accountNumber = acctNoField.getText().trim();
        String customerName = nameField.getText().trim();
        String phoneNumber = phoneField.getText().trim();
        String sex = (String) sexBox.getSelectedItem();
        String branch = (String) branchBox.getSelectedItem();
        String balanceStr = balanceField.getText().trim();

        if (accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (!accountNumber.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Le numéro de compte doit contenir exactement 10 chiffres (ni plus, ni moins).");
        }
        if (customerName.isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (!phoneNumber.matches("^[234][0-9]{7}$")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit commencer par 2, 3 ou 4 et contenir exactement 8 chiffres");
        }
        if ("Select Sex".equals(sex)) {
            throw new IllegalArgumentException("Please select a sex");
        }
        if ("Select Branch".equals(branch)) {
            throw new IllegalArgumentException("Please select a branch");
        }
        if (balanceStr.isEmpty()) {
            throw new IllegalArgumentException("Initial balance is required");
        }
        try {
            double balance = Double.parseDouble(balanceStr);
            if (balance < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid balance format");
        }
    }

    private void clearFields(JTextField acctNoField, JTextField nameField, JTextField phoneField,
                           JComboBox<String> sexBox, JComboBox<String> branchBox, JTextField balanceField) {
        acctNoField.setText("");
        nameField.setText("");
        phoneField.setText("");
        sexBox.setSelectedIndex(0);
        branchBox.setSelectedIndex(0);
        balanceField.setText("");
        updateStatus();
    }

    private void clearDepositFields() {
        depositSearchField.setText("");
        depositNameField.setText("");
        depositBalanceField.setText("");
        depositAmountField.setText("");
    }

    private void clearWithdrawFields() {
        withdrawSearchField.setText("");
        withdrawNameField.setText("");
        withdrawBalanceField.setText("");
        withdrawAmountField.setText("");
    }

    private void updateTransactionTable(JTable table, List<Transaction> transactions) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Transaction transaction : transactions) {
            model.addRow(new Object[]{
                transaction.getTransactionId(),
                transaction.getAccountNumber(),
                transaction.getCustomerName(),
                String.format("%.2f MRU", transaction.getDeposit()),
                String.format("%.2f MRU", transaction.getWithdraw()),
                String.format("%.2f MRU", transaction.getBalance()),
                dateFormat.format(transaction.getDate())
            });
        }
        updateStatus();
    }

    private void setupNumericValidation() {
        // Validation pour le champ de solde initial
        balanceField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        // Validation pour le champ de montant de dépôt
        depositAmountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        // Validation pour le champ de montant de retrait
        withdrawAmountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        // Validation pour le champ de numéro de compte
        acctNoField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        // Validation pour le champ de numéro de téléphone
        phoneField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
    }

    private void formatAmountField(JTextField field, double amount) {
        field.setText(String.format("%.2f", amount));
    }

    private void showCustomMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("Button.background", new Color(0, 153, 153));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        JOptionPane.showMessageDialog(this, message, title, messageType);
        // Réinitialiser le style pour ne pas impacter d'autres dialogues
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
        UIManager.put("Button.font", null);
    }

    private void showCustomConfirmDialog(String message, String title, ActionListener onConfirm) {
        int response = JOptionPane.showConfirmDialog(this, message, title,
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            onConfirm.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "confirm"));
        }
    }
}