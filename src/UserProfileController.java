
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;

public class UserProfileController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ImageView themeToggleIcon;

    @FXML
    private ImageView brandLogo;

    @FXML
    private Button themeToggleButton;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button editProfileBtn;

    @FXML
    private Label nameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private VBox upcomingContainer;

    @FXML
    private VBox previousContainer;

    private String username;
    private String name;
    private String email;

    private final String LIGHT_CSS = getClass().getResource("userprofile.css").toExternalForm();
    private final String DARK_CSS = getClass().getResource("darkuserprofile.css").toExternalForm();

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();

    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {

            themeToggleButton.setStyle("-fx-alignment: center-right; -fx-padding: 10px;");
            logoutBtn.setStyle("-fx-alignment: center-right; -fx-padding: 10px;");

            applyTheme();
        });
    }

    public void setLoggedUser(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
        loadProfile();
        loadAppointments();
    }

    private void loadProfile() {
        // String sql = "SELECT name, username, email"
        //       + "FROM users WHERE username = ?";

        // try (Connection con = database.connectDB();
        //         PreparedStatement ps = con.prepareStatement(sql)) {

        //     ps.setString(1, username);

            // try (ResultSet rs = ps.executeQuery()) {
            //     if (rs.next()) {
            //         nameLabel.setText("Name: " + name);
            //         usernameLabel.setText("Username: " + username);
            //         emailLabel.setText("Email: " + email);
            //     } else {
            //         showError("profile not found.");
            //     }
            // }

        try {
            nameLabel.setText("Name: " + safe(name));
            usernameLabel.setText("Username: " + safe(username));
            emailLabel.setText("Email: " + safe(email));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load profile.");
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lawyerlogreg.fxml"));
            Parent root = loader.load();

            LawyerEnterSceneController controller = loader.getController();
            controller.showLoginPage();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root);
            scene.getStylesheets().clear();

            if (ThemeManager.isDarkMode()) {
                scene.getStylesheets().add(getClass().getResource("darklogindesign.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("loginDesign.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Ainoggo");
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);
            stage.setMaximized(maximized);
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not logout.");
        }
    }

    @FXML
    private void gotoDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setLoggedUser(username, name, email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root);
            scene.getStylesheets().clear();

            if (ThemeManager.isDarkMode()) {
                scene.getStylesheets().add(getClass().getResource("darkdashboard.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Ainoggo");
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);
            stage.setMaximized(maximized);
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not go to dashboard. Check terminal.");
        }
    }

    private void loadAppointments() {
        upcomingContainer.getChildren().clear();
        previousContainer.getChildren().clear();

        String sql = "SELECT a.id, a.user_username, a.date, a.time, a.status "
                + "FROM appointments a "
                + "JOIN lawyers l ON l.id = a.lawyer_id "
                + "WHERE l.username = ? "
                + "ORDER BY a.date ASC, a.time ASC";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int appointmentId = rs.getInt("id");
                    String client = rs.getString("user_username");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String status = rs.getString("status");

                    boolean isPast = isAppointmentPast(date, time);

                    if (isPast) {
                        previousContainer.getChildren().add(0,
                                createPreviousCard(client, date, time, status));
                    } else {
                        upcomingContainer.getChildren().add(
                                createUpcomingCard(appointmentId, client, date, time, status));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load appointments.");
        }
    }

    private boolean isAppointmentPast(String dateStr, String timeStr) {
        try {
            LocalDate appointmentDate = LocalDate.parse(dateStr);

            LocalTime appointmentTime = parseTime(timeStr);

            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
            LocalDateTime now = LocalDateTime.now();

            return appointmentDateTime.isBefore(now);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return LocalTime.MIDNIGHT;
        }

        timeStr = timeStr.trim();

        try {
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e1) {
            try {
                return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e2) {
                try {
                    return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"));
                } catch (Exception e3) {
                    try {
                        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("hh:mm a"));
                    } catch (Exception e4) {
                        return LocalTime.MIDNIGHT;
                    }
                }
            }
        }
    }

    private VBox createUpcomingCard(int appointmentId, String client, String date, String time, String status) {
        VBox card = new VBox(8);
        card.getStyleClass().add("appointment-card");

        Label clientLabel = new Label(client);
        clientLabel.getStyleClass().add("appointment-client");

        Label dateLabel = new Label("Date: " + date);
        dateLabel.getStyleClass().add("appointment-meta");

        Label timeLabel = new Label("Time: " + time);
        timeLabel.getStyleClass().add("appointment-meta");

        Label statusLabel = new Label("Status: " + status);
        statusLabel.getStyleClass().add("appointment-status");

        card.getChildren().addAll(clientLabel, dateLabel, timeLabel, statusLabel);

        if ("Pending".equalsIgnoreCase(status)) {
            Button acceptBtn = new Button("Accept");
            Button rejectBtn = new Button("Reject");

            acceptBtn.getStyleClass().add("primary-button");
            rejectBtn.getStyleClass().add("secondary-button");

            acceptBtn.setOnAction(e -> {
                updateAppointmentStatus(appointmentId, "Accepted");
                loadAppointments();
            });

            rejectBtn.setOnAction(e -> {
                updateAppointmentStatus(appointmentId, "Rejected");
                loadAppointments();
            });

            HBox buttonBox = new HBox(10, acceptBtn, rejectBtn);
            card.getChildren().add(buttonBox);
        }

        return card;
    }

    private VBox createPreviousCard(String client, String date, String time, String status) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("appointment-card", "previous-card");

        Label clientLabel = new Label(client);
        clientLabel.getStyleClass().add("appointment-client");

        Label dateLabel = new Label("Date: " + date);
        dateLabel.getStyleClass().add("appointment-meta");

        Label timeLabel = new Label("Time: " + time);
        timeLabel.getStyleClass().add("appointment-meta");

        Label statusLabel = new Label("Status: " + status);
        statusLabel.getStyleClass().add("appointment-status");

        card.getChildren().addAll(clientLabel, dateLabel, timeLabel, statusLabel);

        return card;
    }

    private void updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not update appointment status.");
        }
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void applyTheme() {
        if (rootPane == null || rootPane.getScene() == null) {
            return;
        }

        Scene scene = rootPane.getScene();
        scene.getStylesheets().clear();

        if (ThemeManager.isDarkMode()) {
            scene.getStylesheets().add(DARK_CSS);

            if (themeToggleIcon != null) {
                themeToggleIcon.setImage(new Image(LIGHT_ICON));
            }
            if (brandLogo != null) {
                brandLogo.setImage(new Image(LOGO_WHITE));
            }
        } else {
            scene.getStylesheets().add(LIGHT_CSS);

            if (themeToggleIcon != null) {
                themeToggleIcon.setImage(new Image(DARK_ICON));
            }
            if (brandLogo != null) {
                brandLogo.setImage(new Image(LOGO_BLACK));
            }
        }
    }

    @FXML
    private void toggleTheme(ActionEvent event) {
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
        applyTheme();
    }

    @FXML
    private void editProfile(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Edit Profile");
        a.setHeaderText(null);
        a.setContentText("Edit profile page will be connected next.");
        a.showAndWait();
    }
}