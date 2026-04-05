
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
        // + "FROM users WHERE username = ?";

        // try (Connection con = database.connectDB();
        // PreparedStatement ps = con.prepareStatement(sql)) {

        // ps.setString(1, username);

        // try (ResultSet rs = ps.executeQuery()) {
        // if (rs.next()) {
        // nameLabel.setText("Name: " + name);
        // usernameLabel.setText("Username: " + username);
        // emailLabel.setText("Email: " + email);
        // } else {
        // showError("profile not found.");
        // }
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

        // Query adjusted to fetch Lawyer Name based on the User's username
        String sql = "SELECT a.id, l.name AS lawyer_name, a.date, a.time, a.status "
                + "FROM appointments a "
                + "JOIN lawyers l ON l.id = a.lawyer_id "
                + "WHERE a.user_username = ? "
                + "ORDER BY a.date ASC, a.time ASC";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int appointmentId = rs.getInt("id");
                    String lawyerName = rs.getString("lawyer_name");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String status = rs.getString("status");

                    boolean isPast = isAppointmentPast(date, time);

                    if (isPast) {
                        previousContainer.getChildren().add(0,
                                createPreviousCard(lawyerName, date, time, status));
                    } else {
                        upcomingContainer.getChildren().add(
                                createUpcomingCard(appointmentId, lawyerName, date, time, status));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load your appointments.");
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

    private VBox createUpcomingCard(int appointmentId, String lawyerName, String date, String time, String status) {
        VBox card = new VBox(8);
        card.getStyleClass().add("appointment-card");

        Label lawyerLabel = new Label("Lawyer: " + lawyerName);
        lawyerLabel.getStyleClass().add("appointment-client"); 

        Label dateLabel = new Label("Date: " + date);
        dateLabel.getStyleClass().add("appointment-meta");

        Label timeLabel = new Label("Time: " + time);
        timeLabel.getStyleClass().add("appointment-meta");

        Label statusLabel = new Label("Status: " + status);
        statusLabel.getStyleClass().add("appointment-status");

        card.getChildren().addAll(lawyerLabel, dateLabel, timeLabel, statusLabel);
        return card;
    }

    private VBox createPreviousCard(String lawyerName, String date, String time, String status) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("appointment-card", "previous-card");

        Label lawyerLabel = new Label("Lawyer: " + lawyerName);
        lawyerLabel.getStyleClass().add("appointment-client");

        Label dateLabel = new Label("Date: " + date);
        dateLabel.getStyleClass().add("appointment-meta");

        Label timeLabel = new Label("Time: " + time);
        timeLabel.getStyleClass().add("appointment-meta");

        Label statusLabel = new Label("Status: " + status);
        statusLabel.getStyleClass().add("appointment-status");

        card.getChildren().addAll(lawyerLabel, dateLabel, timeLabel, statusLabel);
        return card;
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
       

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editProfileUser.fxml"));
            Parent root = loader.load();
            editProfileUserController controller = loader.getController();
            controller.setLoggedUser(username, name, email);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}