package com.bank.ui;

import com.bank.models.User;
import com.bank.models.Account;
import com.bank.models.Transaction;
import com.bank.services.AccountService;
import com.bank.services.TransactionService;
import com.bank.exceptions.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;


public class UserDashboard extends JFrame {
    private final User user;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private JTable transactionTable;
    private JLabel balanceLabel;
    private JTextField transferAmountField;
    private JTextField recipientAccountField;

    public UserDashboard(User user) {
        this.user = user;
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        setupUI();
        loadAccountInfo();
    }

    private void setupUI() {
        setTitle("Tableau de Bord - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // En-tête avec les informations du compte
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 240), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel welcomeLabel = new JLabel("Bienvenue, " + user.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 153, 153));
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);

        balanceLabel = new JLabel("Solde: Chargement...");
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        headerPanel.add(balanceLabel, BorderLayout.CENTER);

        // Bouton Déconnexion en haut à droite
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
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

        // Panel de transfert
        JPanel transferPanel = createTransferPanel();
        mainPanel.add(transferPanel, BorderLayout.CENTER);

        // Table des transactions
        JPanel transactionPanel = createTransactionPanel();
        mainPanel.add(transactionPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 240), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // Titre
        JLabel titleLabel = new JLabel("Transfert d'argent");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 153, 153));
        gbc.gridx = 0; gbc.gridwidth = 5;
        panel.add(titleLabel, gbc);

        // Ligne de champs alignés horizontalement
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel recipientLabel = new JLabel("Téléphone destinataire:");
        recipientLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(recipientLabel, gbc);

        gbc.gridx = 1;
        recipientAccountField = new JTextField(15);
        recipientAccountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recipientAccountField.setPreferredSize(new Dimension(180, 35));
        recipientAccountField.setToolTipText("Numéro de téléphone du destinataire");
        panel.add(recipientAccountField, gbc);

        gbc.gridx = 2;
        JLabel amountLabel = new JLabel("Montant (MRU):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(amountLabel, gbc);

        gbc.gridx = 3;
        transferAmountField = new JTextField(10);
        transferAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transferAmountField.setPreferredSize(new Dimension(120, 35));
        panel.add(transferAmountField, gbc);

        gbc.gridx = 4;
        JButton transferButton = createStyledButton("Effectuer le transfert");
        transferButton.setPreferredSize(new Dimension(180, 40));
        transferButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(transferButton, gbc);

        transferButton.addActionListener(e -> handleTransfer());

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 240), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Historique des transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Date", "Type", "Montant", "Solde"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(model);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadAccountInfo() {
        try {
            Optional<Account> accountOpt = accountService.findAccount(user.getAccountNumber());
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                balanceLabel.setText(String.format("Solde: %.2f MRU", account.getBalance()));
                loadTransactions();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des informations: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAccountTransactions(user.getAccountNumber());
            DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
            model.setRowCount(0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Transaction transaction : transactions) {
                String type = transaction.getDeposit() > 0 ? "Dépôt" : "Retrait";
                double amount = transaction.getDeposit() > 0 ? transaction.getDeposit() : transaction.getWithdraw();
                model.addRow(new Object[]{
                    dateFormat.format(transaction.getDate()),
                    type,
                    String.format("%.2f MRU", amount),
                    String.format("%.2f MRU", transaction.getBalance())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des transactions: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTransfer() {
        try {
            String recipientPhone = recipientAccountField.getText().trim();
            String amountStr = transferAmountField.getText().trim();

            if (recipientPhone.isEmpty() || amountStr.isEmpty()) {
                throw new IllegalArgumentException("Veuillez remplir tous les champs");
            }
            if (!recipientPhone.matches("^[234][0-9]{7}$")) {
                throw new IllegalArgumentException("Numéro de téléphone invalide");
            }
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                throw new IllegalArgumentException("Le montant doit être positif");
            }

            Optional<Account> recipientOpt = accountService.findAccountByPhone(recipientPhone);
            if (recipientOpt.isEmpty()) {
                throw new IllegalArgumentException("Aucun compte trouvé pour ce numéro de téléphone");
            }
            String recipientAccount = recipientOpt.get().getAccountNumber();

            transactionService.transfer(user.getAccountNumber(), recipientAccount, amount);
            JOptionPane.showMessageDialog(this, "Transfert effectué avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);

            recipientAccountField.setText("");
            transferAmountField.setText("");
            loadAccountInfo();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 153, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
} 