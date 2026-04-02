import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class editProfileUserController {

    @FXML
    private ImageView ainoggo_home_logo;

    @FXML
    private Text ainoggo_home_text;

    @FXML
    private Hyperlink cancel_edit;

    @FXML
    private BorderPane edit_page;

    @FXML
    private TextField email;

    @FXML
    private StackPane form;

    @FXML
    private HBox header;

    @FXML
    private TextField name;

    @FXML
    private Button save_button;

    @FXML
    private Button theme_toggle_button;

    @FXML
    private ImageView theme_toggle_icon;

    @FXML
    private TextField username;

    private String Username;

    private String Name;

    private String Email;

    private String editedUsername;

    private Boolean isEdited = false;

    private final String LIGHT_CSS = getClass().getResource("editprofile.css").toExternalForm();
    private final String DARK_CSS = getClass().getResource("darkeditprofile.css").toExternalForm();

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();

    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            applyCurrentTheme();
        }); 
    }

    public void setLoggedUser(String username, String name, String email) {
        this.Username = username;
        this.Name = name;
        this.Email = email;
        loadCurrentData();
    }

    private void loadCurrentData() {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name.setText(rs.getString("name"));
                username.setText(rs.getString("username"));
                email.setText(rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void saveEdit(ActionEvent event) {

        String sql = "UPDATE users SET name=?, username=?, email=? WHERE username=?";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.getText());
            ps.setString(2, username.getText());
            ps.setString(3, email.getText());
            ps.setString(4, Username);
            ps.executeUpdate();
            // showInfo("Profile updated successfully!");
            editedUsername = username.getText();
            Username = editedUsername;
            isEdited = true;
            cancelEdit(event); // Return to dashboard
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cancelEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfile.fxml"));
            Parent root = loader.load();
            UserProfileController controller = loader.getController();
            if(isEdited) {
                controller.setLoggedUser(editedUsername, name.getText(), email.getText());
            } else {
                controller.setLoggedUser(Username, Name, Email);
            }
            Stage stage = (Stage) edit_page.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void toggleTheme(ActionEvent event) {
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
        Scene scene = edit_page.getScene();
        if (scene == null) {
            return;
        }

        scene.getStylesheets().clear();

        if (ThemeManager.isDarkMode()) {
            scene.getStylesheets().add(DARK_CSS);
            ainoggo_home_logo.setImage(new javafx.scene.image.Image(LOGO_WHITE));
            if (theme_toggle_icon != null) {
                theme_toggle_icon.setImage(new javafx.scene.image.Image(LIGHT_ICON));
            }
            if (ainoggo_home_text != null) {
                ainoggo_home_text.setStyle("-fx-fill: white; -fx-font-size: 24px; -fx-font-weight: 800;");
            }
        } else {
            scene.getStylesheets().add(LIGHT_CSS);
            ainoggo_home_logo.setImage(new javafx.scene.image.Image(LOGO_BLACK));
            if (theme_toggle_icon != null) {
                theme_toggle_icon.setImage(new javafx.scene.image.Image(DARK_ICON));
            }
            if (ainoggo_home_text != null) {
                ainoggo_home_text.setStyle("-fx-fill: #000000; -fx-font-size: 24px; -fx-font-weight: 800;");
            }
        }
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}