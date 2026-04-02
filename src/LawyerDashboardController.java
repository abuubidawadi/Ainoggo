
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

public class LawyerDashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ImageView lawyerImageView;

    @FXML
    private ImageView themeToggleIcon;

    @FXML
    private ImageView brandLogo;

    @FXML
    private StackPane photoHolder;

    @FXML
    private Button themeToggleButton;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button editProfileBtn;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label specializationLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private Label barIdLabel;

    @FXML
    private Label lawFirmLabel;

    @FXML
    private Label feeLabel;

    @FXML
    private Label bioLabel;

    @FXML
    private VBox upcomingContainer;

    @FXML
    private VBox previousContainer;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    private String lawyerUsername;

    private final String LIGHT_CSS = getClass().getResource("lawyerdashboard.css").toExternalForm();
    private final String DARK_CSS = getClass().getResource("darklawyerdashboard.css").toExternalForm();

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();

    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(44);
            clip.setArcHeight(44);
            clip.widthProperty().bind(lawyerImageView.fitWidthProperty());
            clip.heightProperty().bind(lawyerImageView.fitHeightProperty());
            lawyerImageView.setClip(clip);

            themeToggleButton.setStyle("-fx-alignment: center-right; -fx-padding: 10px;");
            logoutBtn.setStyle("-fx-alignment: center-right; -fx-padding: 10px;");

            applyTheme();
        });
    }

    public void setLawyerUsername(String lawyerUsername) {
        this.lawyerUsername = lawyerUsername;
        loadLawyerProfile();
        loadAppointments();
    }

    private void loadLawyerProfile() {
        String sql = "SELECT name, email, specialization, exp_years, bar_reg_no, law_firm, fee, bio, photo "
                + "FROM lawyers WHERE username = ?";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lawyerUsername);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nameLabel.setText("Name: " + safe(rs.getString("name")));
                    emailLabel.setText("Email: " + safe(rs.getString("email")));
                    specializationLabel.setText("Specialization: " + safe(rs.getString("specialization")));
                    experienceLabel.setText("Experience: " + safe(rs.getString("exp_years")) + " Years");
                    barIdLabel.setText("Bar ID: " + safe(rs.getString("bar_reg_no")));
                    lawFirmLabel.setText("Law Firm: " + safe(rs.getString("law_firm")));

                    Object feeObj = rs.getObject("fee");
                    feeLabel.setText(feeObj == null ? "Fee: Tk. -"
                            : String.format("Fee: Tk. %.0f / session", rs.getDouble("fee")));

                    bioLabel.setText("Bio: " + safe(rs.getString("bio")));

                    String photo = rs.getString("photo");
                    Image image = loadLawyerImage(photo);
                    lawyerImageView.setImage(image);
                    applyCenterSquareCrop(lawyerImageView, image, 160);
                } else {
                    showError("Lawyer profile not found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load lawyer profile.");
        }
    }

    private void applyCenterSquareCrop(ImageView imageView, Image image, double size) {
        if (image == null)
            return;

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double squareSide = Math.min(imageWidth, imageHeight);

        double x = (imageWidth - squareSide) / 2;
        double y = (imageHeight - squareSide) / 2;

        imageView.setViewport(new Rectangle2D(x, y, squareSide, squareSide));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);
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

            ps.setString(1, lawyerUsername);

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

    private Image loadLawyerImage(String photoFromDb) {
        if (photoFromDb == null || photoFromDb.trim().isEmpty()) {
            InputStream is = getClass().getResourceAsStream("/images/anonymous.png");
            return new Image(is);
        }

        String photo = photoFromDb.trim().replace("\\", "/");
        if (photo.contains("/")) {
            photo = photo.substring(photo.lastIndexOf("/") + 1);
        }

        File f = new File("uploaded_photos/" + photo);
        if (f.exists()) {
            return new Image(f.toURI().toString());
        }

        InputStream is2 = getClass().getResourceAsStream("/images/lawyers/" + photo);
        if (is2 != null) {
            return new Image(is2);
        }

        InputStream is3 = getClass().getResourceAsStream("/images/anonymous.png");
        return new Image(is3);
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
        // Alert a = new Alert(Alert.AlertType.INFORMATION);
        // a.setTitle("Edit Profile");
        // a.setHeaderText(null);
        // a.setContentText("Edit profile page will be connected next.");
        // a.showAndWait();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editProfileLawyer.fxml"));
            Parent root = loader.load();

            editProfileLawyerController controller = loader.getController();
            controller.setLawyerUsername(lawyerUsername);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root);
            scene.getStylesheets().clear();

            if (ThemeManager.isDarkMode()) {
                scene.getStylesheets().add(getClass().getResource("darkeditprofile.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("editprofile.css").toExternalForm());
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
            showError("Could not open lawyer dashboard.");
        }
    }
}