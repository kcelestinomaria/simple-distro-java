package client;

import client.utils.PDFGenerator;
import rmi.StudentDBInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import javafx.event.ActionEvent;

/**
 * GUIOne is the main JavaFX GUI class for the Student Management System.
 * It allows a user to register and login; only upon a successful login are the
 * CRUD operations for student records enabled. The dashboard includes a logout button.
 */
public class GUIOne extends Application {

    private StudentDBInterface studentDB; // RMI Remote Interface
    private TabPane tabPane;
    private Tab crudTab;  // Dashboard tab containing CRUD functionalities

    @Override
    public void start(Stage primaryStage) {
        // Connect to the RMI server
        try {
            studentDB = (StudentDBInterface) Naming.lookup("rmi://localhost:1099/StudentDBService");
        } catch (Exception e) {
            showErrorDialog("Connection Error", "Failed to connect to RMI server.\nMake sure the server is running.");
            return;
        }

        primaryStage.setTitle("Student Management System");

        // Create a TabPane for tabs
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Initially, only show Register and Login tabs
        Tab registerTab = createTab("Register", createRegisterPane());
        Tab loginTab = createTab("Login", createLoginPane());
        tabPane.getTabs().addAll(registerTab, loginTab);

        // Create the CRUD dashboard tab (will be unlocked upon login)
        crudTab = createTab("Dashboard", createCRUDPane());

        // Create a header with a title and image
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        Label headerLabel = new Label("Welcome to the Student Management System");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        headerLabel.setTextFill(Color.DARKBLUE);

        ImageView headerImage = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("../gui/images/student.jpeg"));
            headerImage.setImage(image);
            headerImage.setFitWidth(100);
            headerImage.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Header image not found, skipping image load.");
        }
        headerBox.getChildren().addAll(headerLabel, headerImage);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(headerBox);
        mainPane.setCenter(tabPane);
        mainPane.setPadding(new Insets(20));

        Scene scene = new Scene(mainPane, 700, 600);
        // Uncomment the line below if you have an external stylesheet
        // scene.getStylesheets().add(getClass().getResource("../gui/styles/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Once the user logs in successfully, replace the Register/Login tabs with the CRUD dashboard.
     */
    private void unlockCRUDTabs() {
        Platform.runLater(() -> {
            tabPane.getTabs().clear();
            tabPane.getTabs().add(crudTab);
        });
    }

    /**
     * Creates a CRUD dashboard pane that includes a logout button and tabs for Insert, Search, Update, Delete, and Report.
     */
    private Pane createCRUDPane() {
        // Create a header with a logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> logout());
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_RIGHT);
        header.getChildren().add(logoutBtn);

        // Create a TabPane for CRUD operations and the Report tab
        TabPane crudPane = new TabPane();
        crudPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab insertTab = createTab("Insert", createInsertPane());
        Tab searchTab = createTab("Search", createSearchPane());
        Tab updateTab = createTab("Update", createUpdatePane());
        Tab deleteTab = createTab("Delete", createDeletePane());
        Tab reportTab = createReportTab();
        crudPane.getTabs().addAll(insertTab, searchTab, updateTab, deleteTab, reportTab);

        VBox dashboard = new VBox(10);
        dashboard.setAlignment(Pos.CENTER);
        Label welcome = new Label("Logged in successfully!\nManage student records below:");
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        welcome.setTextFill(Color.DARKGREEN);

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(header, welcome, crudPane);
        return container;
    }

    /**
     * Logs out the current user by resetting the UI to show only Register and Login tabs.
     */
    private void logout() {
        Platform.runLater(() -> {
            tabPane.getTabs().clear();
            Tab registerTab = createTab("Register", createRegisterPane());
            Tab loginTab = createTab("Login", createLoginPane());
            tabPane.getTabs().addAll(registerTab, loginTab);
        });
    }

    private Tab createTab(String title, Pane content) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        return tab;
    }

    private Pane createRegisterPane() {
        VBox registerBox = new VBox(10);
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setPadding(new Insets(20));

        Label title = new Label("Register New User");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");
        Label resultLabel = new Label();
        ProgressIndicator loader = new ProgressIndicator();
        loader.setVisible(false);

        registerBtn.setOnAction((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                resultLabel.setText("Please fill in all fields.");
                resultLabel.setTextFill(Color.RED);
            } else {
                loader.setVisible(true);
                new Thread(() -> {
                    try {
                        String response = studentDB.registerUser(username, password);
                        Platform.runLater(() -> {
                            loader.setVisible(false);
                            resultLabel.setText(response);
                            resultLabel.setTextFill(Color.DARKBLUE);
                        });
                    } catch (RemoteException ex) {
                        Platform.runLater(() -> {
                            loader.setVisible(false);
                            resultLabel.setText("Error registering user.");
                            resultLabel.setTextFill(Color.RED);
                        });
                    }
                }).start();
            }
        });

        registerBox.getChildren().addAll(title, usernameField, passwordField, registerBtn, loader, resultLabel);
        return registerBox;
    }

    private Pane createLoginPane() {
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(20));

        Label title = new Label("Login");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        Label resultLabel = new Label();
        ProgressIndicator loader = new ProgressIndicator();
        loader.setVisible(false);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                resultLabel.setText("Please fill in all fields.");
                resultLabel.setTextFill(Color.RED);
            } else {
                loader.setVisible(true);
                new Thread(() -> {
                    try {
                        String response = studentDB.loginUser(username, password);
                        Platform.runLater(() -> {
                            loader.setVisible(false);
                            resultLabel.setText(response);
                            resultLabel.setTextFill(response.toLowerCase().contains("successful") ? Color.DARKBLUE : Color.RED);
                            if (response.toLowerCase().contains("successful")) {
                                unlockCRUDTabs();
                            }
                        });
                    } catch (RemoteException ex) {
                        Platform.runLater(() -> {
                            loader.setVisible(false);
                            resultLabel.setText("Error during login.");
                            resultLabel.setTextFill(Color.RED);
                        });
                    }
                }).start();
            }
        });

        loginBox.getChildren().addAll(title, usernameField, passwordField, loginBtn, loader, resultLabel);
        return loginBox;
    }

    private Pane createInsertPane() {
        VBox insertBox = new VBox(10);
        insertBox.setAlignment(Pos.CENTER);
        insertBox.setPadding(new Insets(20));

        Label title = new Label("Insert Student Details");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Student Name");

        TextField courseField = new TextField();
        courseField.setPromptText("Student Course");

        Button insertBtn = new Button("Insert");
        insertBtn.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");
        Label resultLabel = new Label();

        insertBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            if (id.isEmpty() || name.isEmpty() || course.isEmpty()) {
                resultLabel.setText("Please fill in all fields.");
                resultLabel.setTextFill(Color.RED);
            } else {
                new Thread(() -> {
                    try {
                        String response = studentDB.insert(id, name, course);
                        Platform.runLater(() -> {
                            resultLabel.setText(response);
                            resultLabel.setTextFill(Color.DARKBLUE);
                        });
                    } catch (RemoteException ex) {
                        Platform.runLater(() -> {
                            resultLabel.setText("Error inserting student.");
                            resultLabel.setTextFill(Color.RED);
                        });
                    }
                }).start();
            }
        });

        insertBox.getChildren().addAll(title, idField, nameField, courseField, insertBtn, resultLabel);
        return insertBox;
    }

    private Pane createSearchPane() {
        VBox searchBox = new VBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(20));

        Label title = new Label("Search Student Details");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        Label resultLabel = new Label();

        searchBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                resultLabel.setText("Please enter a student ID.");
                resultLabel.setTextFill(Color.RED);
            } else {
                new Thread(() -> {
                    try {
                        String response = studentDB.select(id);
                        Platform.runLater(() -> {
                            resultLabel.setText(response);
                            resultLabel.setTextFill(Color.DARKBLUE);
                        });
                    } catch (RemoteException ex) {
                        Platform.runLater(() -> {
                            resultLabel.setText("Student not found.");
                            resultLabel.setTextFill(Color.RED);
                        });
                    }
                }).start();
            }
        });

        searchBox.getChildren().addAll(title, idField, searchBtn, resultLabel);
        return searchBox;
    }

    private Pane createUpdatePane() {
        VBox updateBox = new VBox(10);
        updateBox.setAlignment(Pos.CENTER);
        updateBox.setPadding(new Insets(20));

        Label title = new Label("Update Student Details");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField nameField = new TextField();
        nameField.setPromptText("New Name");

        TextField courseField = new TextField();
        courseField.setPromptText("New Course");

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: darkorange; -fx-text-fill: white;");
        Label resultLabel = new Label();

        updateBtn.setOnAction(e -> {
            new Thread(() -> {
                try {
                    String response = studentDB.update(idField.getText(), nameField.getText(), courseField.getText());
                    Platform.runLater(() -> {
                        resultLabel.setText(response);
                        resultLabel.setTextFill(Color.DARKBLUE);
                    });
                } catch (RemoteException ex) {
                    Platform.runLater(() -> {
                        resultLabel.setText("Error updating student.");
                        resultLabel.setTextFill(Color.RED);
                    });
                }
            }).start();
        });

        updateBox.getChildren().addAll(title, idField, nameField, courseField, updateBtn, resultLabel);
        return updateBox;
    }

    private Pane createDeletePane() {
        VBox deleteBox = new VBox(10);
        deleteBox.setAlignment(Pos.CENTER);
        deleteBox.setPadding(new Insets(20));

        Label title = new Label("Delete Student Details");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
        Label resultLabel = new Label();

        deleteBtn.setOnAction(e -> {
            new Thread(() -> {
                try {
                    String response = studentDB.delete(idField.getText());
                    Platform.runLater(() -> {
                        resultLabel.setText(response);
                        resultLabel.setTextFill(Color.DARKBLUE);
                    });
                } catch (RemoteException ex) {
                    Platform.runLater(() -> {
                        resultLabel.setText("Error deleting student.");
                        resultLabel.setTextFill(Color.RED);
                    });
                }
            }).start();
        });

        deleteBox.getChildren().addAll(title, idField, deleteBtn, resultLabel);
        return deleteBox;
    }

    private Tab createReportTab() {
        Tab reportTab = new Tab("Generate Report");

        VBox reportBox = new VBox(10);
        reportBox.setAlignment(Pos.CENTER);
        reportBox.setPadding(new Insets(20));

        Label title = new Label("Generate PDF Report");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKGREEN);

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: purple; -fx-text-fill: white;");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setPrefHeight(300);

        Button exportPDFBtn = new Button("Export to PDF");
        exportPDFBtn.setDisable(true); // Enable only after data is loaded
        exportPDFBtn.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");

        generateBtn.setOnAction(e -> {
            generateBtn.setDisable(true);
            new Thread(() -> {
                try {
                    String reportData = studentDB.getAllStudents(); // Remote RMI call
                    Platform.runLater(() -> {
                        reportArea.setText(reportData);
                        exportPDFBtn.setDisable(false);
                        generateBtn.setDisable(false);
                    });
                } catch (RemoteException ex) {
                    Platform.runLater(() -> {
                        showErrorDialog("Error", "Failed to retrieve student records.");
                        generateBtn.setDisable(false);
                    });
                }
            }).start();
        });

        exportPDFBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    PDFGenerator.generateReport(reportArea.getText(), file.getAbsolutePath());
                    showInfoDialog("Success", "PDF report saved to: " + file.getAbsolutePath());
                } catch (Exception ex) {
                    showErrorDialog("PDF Error", "Failed to generate PDF: " + ex.getMessage());
                }
            }
        });

        reportBox.getChildren().addAll(title, generateBtn, reportArea, exportPDFBtn);
        reportTab.setContent(reportBox);
        return reportTab;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
