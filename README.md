# 📋 Documentation du Projet - Système de Gestion Bancaire

## ✨ Introduction

Le **Système de Gestion Bancaire** est une application de bureau développée en Java 24 utilisant Swing pour l'interface utilisateur et MySQL pour la gestion des données. Il permet d'assurer la gestion de comptes utilisateurs dans un contexte bancaire, avec des fonctionnalités de connexion, gestion des clients et opérations de base.

---

## ⚖ Objectifs du projet

* Créer un système bancaire basique avec authentification.
* Utiliser **Java Swing** pour l’interface graphique.
* Gérer les données via une base **MySQL**.
* Implémenter une gestion d’erreurs (exceptions).
* Organiser le projet avec un seul point d’entrée `Main.java`.

---

## 📂 Structure du projet

```
SystèmeBancaire/
├── src/
│   ├── main/               -> Point d’entrée (Main.java)
│   ├── database/           -> Connexion MySQL (DatabaseConnection.java)
│   ├── ui/                 -> Interfaces utilisateur (LoginUI.java, etc.)
│   ├── controllers/        -> Logique métier (LoginController.java, etc.)
│   └── models/             -> Représentation des données (User.java, etc.)
├── ressources/             -> Images, fichiers .properties
└── README.md
```

---

## 📆 Fonctionnalités principales

### 1. Authentification des utilisateurs

* Connexion via un numéro de téléphone et un mot de passe.
* Vérification des identifiants dans la base de données `bank`.

### 2. Interface graphique (GUI)

* Conçue avec **Java Swing** : `JFrame`, `JPanel`, `JTextField`, `JPasswordField`, `JButton`, `JLabel`, etc.
* Interface d'accueil simple et moderne.

### 3. Connexion à la base de données

* JDBC pour se connecter à MySQL.
* Classe `DatabaseConnection` pour centraliser la connexion.

### 4. Gestion des erreurs

* Gestion des exceptions via `try-catch`.
* Affichage de messages d'erreur utilisateurs clairs en cas d'échec de connexion ou d'erreur SQL.

---

## 🔧 Technologies et outils

* **Langage** : Java (JDK 24)
* **Interface graphique** : Java Swing
* **Base de données** : MySQL
* **IDE** : IntelliJ IDEA Ultimate
* **Versionnage** : Git et GitHub

---

## 📝 Configuration de la base de données

### Nom de la base : `bank`

#### Table : `users`

```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  phone VARCHAR(15) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);
```

### Exemple d'insertion :

```sql
INSERT INTO users(phone, password) VALUES ('44556677', 'pass123');
```

---

## 💡 Points d'amélioration possibles

* Ajout de rôles (Admin / Client).
* Ajout de gestion de comptes, solde, transactions.
* Chiffrement du mot de passe (à l'aide de SHA-256 ou Bcrypt).
* Export PDF ou Excel des données.

---

## 💼 Équipe de développement

Ce projet a été réalisé par :

* **Abdellahi Ahmed**
* **Mohamed Oumar**

Dans le cadre d'un projet universitaire (2025).

---

## 📄 Licence

Projet à usage académique uniquement. Reproduction interdite sans autorisation.
