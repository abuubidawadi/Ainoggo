import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class lawyerProfileUser {

    @FXML
    private BorderPane rootPane;

    @FXML
    private DatePicker date_picker;

    @FXML
    private ComboBox<String> time_picker;

    @FXML
    private ImageView lawyer_img;

    @FXML
    private Label lawyer_name;

    @FXML
    private Label bio;

    @FXML
    private Label fee;

    @FXML
    private Label specialization;

    @FXML
    private Label law_firm;

    @FXML
    private Label office_address;

    @FXML
    private Label bar_reg_no;

    @FXML
    private Label exp_yr;

    @FXML
    private Label mail;

    @FXML
    private Button themeToggleButton;

    @FXML
    private ImageView themeToggleIcon;

    @FXML
    private ImageView brandLogo;

    @FXML
    private StackPane photoHolder;

    private String loggedUsername;
    private String loggedName;
    private String loggedEmail;

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();
    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    public void setLoggedUser(String username, String name, String email) {
        this.loggedUsername = username;
        this.loggedName = name;
        this.loggedEmail = email;
    }

    public void loadLawyerById(int lawyerId) {
        String sql = "SELECT name, email, specialization, law_firm, fee, photo, bio, bar_reg_no, exp_years, office_address "
                + "FROM lawyers WHERE id = ?";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, lawyerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    showError("Lawyer not found (id=" + lawyerId + ")");
                    return;
                }

                lawyer_name.setText(safe(rs.getString("name")));
                mail.setText(safe(rs.getString("email")));
                specialization.setText(safe(rs.getString("specialization")));
                law_firm.setText(safe(rs.getString("law_firm")));
                office_address.setText(safe(rs.getString("office_address")));
                bar_reg_no.setText(safe(rs.getString("bar_reg_no")));

                String years = safe(rs.getString("exp_years"));
                exp_yr.setText("-".equals(years) ? "-" : years + " Years");

                bio.setText(safe(rs.getString("bio")));

                Object feeObj = rs.getObject("fee");
                fee.setText(
                        feeObj == null ? "Tk. - / session" : String.format("Tk. %.0f / session", rs.getDouble("fee")));

                String photo = rs.getString("photo");
                Image image = loadLawyerImage(photo);
                lawyer_img.setImage(image);
                applyCenterSquareCrop(lawyer_img, image, 250);
                lawyer_img.setImage(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load lawyer profile. Check terminal.");
        }
    }

    private void applyCenterSquareCrop(ImageView imageView, Image image, double size) {
        if (image == null) {
            return;
        }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();

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

            loggedUsername = null;
            loggedName = null;
            loggedEmail = null;

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not logout. Check terminal.");
        }
    }

    @FXML
    private void godashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setLoggedUser(loggedUsername, loggedName, loggedEmail);

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

    @FXML
    private void toggleTheme(ActionEvent event) {
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
        applyTheme();
    }

    private void applyTheme() {
        if (rootPane == null || rootPane.getScene() == null) {
            return;
        }

        Scene scene = rootPane.getScene();
        scene.getStylesheets().clear();

        if (ThemeManager.isDarkMode()) {
            scene.getStylesheets().add(getClass().getResource("darklawyerfromUser.css").toExternalForm());

            if (themeToggleIcon != null) {
                themeToggleIcon.setImage(new Image(LIGHT_ICON));
            }
            if (brandLogo != null) {
                brandLogo.setImage(new Image(LOGO_WHITE));
            }
        } else {
            scene.getStylesheets().add(getClass().getResource("lawyerfromUser.css").toExternalForm());

            if (themeToggleIcon != null) {
                themeToggleIcon.setImage(new Image(DARK_ICON));
            }
            if (brandLogo != null) {
                brandLogo.setImage(new Image(LOGO_BLACK));
            }
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

    private void applyPortraitCrop(ImageView imageView, Image image, double boxWidth, double boxHeight) {
        if (image == null) {
            return;
        }

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double boxRatio = boxWidth / boxHeight;
        double imageRatio = imageWidth / imageHeight;

        double viewportWidth;
        double viewportHeight;

        if (imageRatio > boxRatio) {
            viewportHeight = imageHeight;
            viewportWidth = imageHeight * boxRatio;
        } else {
            viewportWidth = imageWidth;
            viewportHeight = imageWidth / boxRatio;
        }

        double x = (imageWidth - viewportWidth) / 2;
        double y = (imageHeight - viewportHeight) / 2;

        imageView.setViewport(new Rectangle2D(x, y, viewportWidth, viewportHeight));
        imageView.setFitWidth(boxWidth);
        imageView.setFitHeight(boxHeight);
        imageView.setPreserveRatio(false);
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

    @FXML
    public void initialize() {
        time_picker.getItems().addAll(
                "09:00 AM",
                "10:00 AM",
                "11:00 AM",
                "12:00 PM",
                "01:00 PM",
                "02:00 PM",
                "03:00 PM",
                "04:00 PM",
                "05:00 PM",
                "06:00 PM",
                "07:00 PM");

        Platform.runLater(this::applyTheme);
        Platform.runLater(() -> {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(28);
            clip.setArcHeight(28);

            clip.widthProperty().bind(lawyer_img.fitWidthProperty());
            clip.heightProperty().bind(lawyer_img.fitHeightProperty());

            lawyer_img.setClip(clip);
        });
    }

    @FXML
    private void bookAppointment(ActionEvent event) {
        if (loggedUsername == null || loggedUsername.trim().isEmpty()) {
            showError("Please login again before booking an appointment.");
            return;
        }

        if (date_picker.getValue() == null || time_picker.getValue() == null) {
            showError("Please select date and time.");
            return;
        }

        LocalDate selectedDate = date_picker.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            showError("Please select a future date.");
            return;
        }

        if (selectedDate.equals(LocalDate.now())) {
            try {
                LocalTime selectedTime = LocalTime.parse(time_picker.getValue(),
                        DateTimeFormatter.ofPattern("hh:mm a"));
                if (selectedTime.isBefore(LocalTime.now())) {
                    showError("Please select a future time slot.");
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        String sql = "INSERT INTO appointments(user_username, lawyer_id, date, time, status) VALUES(?,?,?,?,?)";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loggedUsername);
            ps.setInt(2, SelectedLawyer.id);
            ps.setString(3, selectedDate.toString());
            ps.setString(4, time_picker.getValue());
            ps.setString(5, "Pending");

            ps.executeUpdate();

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Appointment Booked");
            a.setHeaderText(null);
            a.setContentText("Appointment booking request sent.");
            a.showAndWait();

            date_picker.setValue(null);
            time_picker.getSelectionModel().clearSelection();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not book appointment.");
        }
    }
}
