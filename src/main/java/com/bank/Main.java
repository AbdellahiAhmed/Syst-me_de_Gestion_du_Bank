package com.bank;

import com.bank.ui.LoginFrame;
import com.bank.database.DatabaseConnection;
import com.bank.utils.StyleUtil;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            if (!DatabaseConnection.getInstance().testConnection()) {
                System.err.println("Erreur : Impossible de se connecter à la base de données.");
                System.exit(1);
            }


            StyleUtil.applyGlobalStyle();


            SwingUtilities.invokeLater(() -> {
                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Erreur lors du démarrage de l'interface : " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }
            });

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}