/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import static javax.swing.text.html.HTML.Tag.SELECT;
import static jdk.internal.org.objectweb.asm.commons.GeneratorAdapter.AND;

public class Login extends Application {

    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the database connection
        connectToDatabase();

        // Create layout for login screen
        GridPane loginPane = createFormPane();
        Scene loginScene = new Scene(loginPane, 400, 350);

        // Create layout for signup screen
        Scene signupScene = createSignupScene(primaryStage, loginScene);

        // Create forgot password scene
        Scene forgotPasswordScene = createForgotPasswordScene(primaryStage, loginScene);

        // Add login form fields
        addLoginFormFields(loginPane, primaryStage, signupScene, forgotPasswordScene);

        // Set title and show the login scene
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    // Method to connect to MySQL database
    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/employee management system"; // Change to your DB URL
        String user = "root"; // Your DB user
        String password = ""; // Your DB password

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to connect to the database.");
        }
    }

    private void addLoginFormFields(GridPane pane, Stage primaryStage, Scene signupScene, Scene forgotPasswordScene) {
        Label usernameLabel = new Label("\uD83D\uDC64 Username:"); 
        usernameLabel.setFont(new Font("Arial", 14));
        usernameLabel.setTextFill(Color.BLACK);
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("\uD83D\uDD12 Password:"); 
        passwordLabel.setFont(new Font("Arial", 14));
        passwordLabel.setTextFill(Color.BLACK);
        PasswordField passwordField = new PasswordField();

        pane.add(usernameLabel, 0, 1);
        pane.add(usernameField, 1, 1);
        pane.add(passwordLabel, 0, 2);
        pane.add(passwordField, 1, 2);

        Button submitButton = createButton("Login", "skyblue");
        Button signupButton = createButton("Signup", "lightgreen");
        Button forgotPasswordButton = createButton("Forgot Password?", "lightcoral");

        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (isValidLogin(username, password)) {
                // Clear existing thank-you message if any
                pane.getChildren().removeIf(node -> node instanceof Label && "Thank you!".equals(((Label) node).getText()));

                // Show a thank-you message
                Label thankYouLabel = new Label("Thank you for logging in, " + username + "!");
                thankYouLabel.setTextFill(Color.BLACK);
                thankYouLabel.setFont(new Font("Arial", 16));
                pane.add(thankYouLabel, 1, 5); // Add the label below the buttons

                applyFadeTransition(thankYouLabel); // Optional: Add a fade transition for the message
            } else {
                showError("Invalid username or password.");
            }
        });

        signupButton.setOnAction(event -> {
            primaryStage.setScene(signupScene);
            applyFadeTransition(signupScene.getRoot());
        });

        forgotPasswordButton.setOnAction(event -> {
            primaryStage.setScene(forgotPasswordScene);
            applyFadeTransition(forgotPasswordScene.getRoot());
        });

        HBox buttonsBox = new HBox(10, submitButton, signupButton);
        buttonsBox.setAlignment(Pos.CENTER);
        pane.add(buttonsBox, 1, 3);

        HBox forgotPasswordBox = new HBox(forgotPasswordButton);
        forgotPasswordBox.setAlignment(Pos.CENTER_LEFT);
        pane.add(forgotPasswordBox, 0, 4);
    }

    private Scene createSignupScene(Stage primaryStage, Scene loginScene) {
        GridPane signupPane = createFormPane();

        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.BLACK);
        TextField usernameField = new TextField();
        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.BLACK);
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.BLACK);
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setTextFill(Color.BLACK);
        PasswordField confirmPasswordField = new PasswordField();

        signupPane.add(usernameLabel, 0, 1);
        signupPane.add(usernameField, 1, 1);
        signupPane.add(emailLabel, 0, 2);
        signupPane.add(emailField, 1, 2);
        signupPane.add(passwordLabel, 0, 3);
        signupPane.add(passwordField, 1, 3);
        signupPane.add(confirmPasswordLabel, 0, 4);
        signupPane.add(confirmPasswordField, 1, 4);

        Button registerButton = createButton("Register", "lightgreen");
        signupPane.add(registerButton, 1, 5);

        Button backButton = createButton("Back to Login", "lightcoral");
        signupPane.add(backButton, 1, 6);

        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            try {
                validateSignupInput(username, email, password, confirmPassword);
                registerUser(username, email, password);
                showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "You have successfully registered!");
                primaryStage.setScene(loginScene);
                applyFadeTransition(loginScene.getRoot());
            } catch (IllegalArgumentException | SQLException e) {
                showError(e.getMessage());
            }
        });

        backButton.setOnAction(e -> {
            primaryStage.setScene(loginScene);
            applyFadeTransition(loginScene.getRoot());
        });

        return new Scene(signupPane, 400, 350);
    }

    private Scene createForgotPasswordScene(Stage primaryStage, Scene loginScene) {
        GridPane forgotPasswordPane = createFormPane();

        Label usernameLabel = new Label("Enter your username:");
        usernameLabel.setTextFill(Color.BLACK);
        TextField usernameField = new TextField();
        forgotPasswordPane.add(usernameLabel, 0, 1);
        forgotPasswordPane.add(usernameField, 1, 1);

        Label newPasswordLabel = new Label("New Password:");
        newPasswordLabel.setTextFill(Color.BLACK);
        PasswordField newPasswordField = new PasswordField();
        forgotPasswordPane.add(newPasswordLabel, 0, 2);
        forgotPasswordPane.add(newPasswordField, 1, 2);

        Label confirmPasswordLabel = new Label("Confirm New Password:");
        confirmPasswordLabel.setTextFill(Color.BLACK);
        PasswordField confirmPasswordField = new PasswordField();
        forgotPasswordPane.add(confirmPasswordLabel, 0, 3);
        forgotPasswordPane.add(confirmPasswordField, 1, 3);

        Button continueButton = createButton("Reset Password", "lightgreen");
        forgotPasswordPane.add(continueButton, 1, 4);

        Button backButton = createButton("Back to Login", "lightcoral");
        forgotPasswordPane.add(backButton, 1, 5);

        continueButton.setOnAction(event -> {
            String username = usernameField.getText();
            String newPassword = newPasswordField.getText();
            String confirmNewPassword = confirmPasswordField.getText();

            if (isUserExists(username)) {
                if (newPassword.equals(confirmNewPassword)) {
                    try {
                        updatePassword(username, newPassword);
                        showAlert(Alert.AlertType.INFORMATION, "Password Reset", "Your password has been reset!");
                        primaryStage.setScene(loginScene);
                        applyFadeTransition(loginScene.getRoot());
                    } catch (SQLException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    showError("New passwords do not match.");
                }
            } else {
                showError("Username not found.");
            }
        });

        backButton.setOnAction(event -> {
            primaryStage.setScene(loginScene);
            applyFadeTransition(loginScene.getRoot());
        });

        return new Scene(forgotPasswordPane, 400, 350);
    }

    private GridPane createFormPane() {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        return button;
    }

    private void validateSignupInput(String username, String email, String password, String confirmPassword) {
        if (username.length() < 3 || username.length() > 25) {
            throw new IllegalArgumentException("Username must be between 3 and 25 characters.");
        }
        if (password.length() < 8 || !password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include at least one lowercase letter.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }

    private void registerUser(String username, String email, String password) throws SQLException {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }

    private boolean isValidLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error during login.");
            return false;
        }
    }

    private boolean isUserExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error checking username.");
            return false;
        }
    }

    private void updatePassword(String username, String newPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

    private void applyFadeTransition(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
