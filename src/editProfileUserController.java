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

    String newName = name.getText() == null ? "" : name.getText().trim();
    String newUsername = username.getText() == null ? "" : username.getText().trim();
    String newEmail = email.getText() == null ? "" : email.getText().trim();

    if (newName.isEmpty()) {
        showInfo("Name cannot be empty.");
        return;
    }

    if (newUsername.isEmpty()) {
        showInfo("Username cannot be empty.");
        return;
    }

    if (newEmail.isEmpty()) {
        showInfo("Email cannot be empty.");
        return;
    }

    Connection con = null;
    PreparedStatement checkPs = null;
    PreparedStatement userPs = null;
    PreparedStatement appointmentPs = null;
    ResultSet rs = null;

    try {
        con = database.connectDB();
        if (con == null) {
            showInfo("Database connection failed.");
            return;
        }

        con.setAutoCommit(false);

        // Check if new username already exists for another user
        String checkSql = "SELECT username FROM users WHERE username = ? AND username <> ?";
        checkPs = con.prepareStatement(checkSql);
        checkPs.setString(1, newUsername);
        checkPs.setString(2, Username);
        rs = checkPs.executeQuery();

        if (rs.next()) {
            showInfo("This username is already taken.");
            con.rollback();
            return;
        }

        // Update users table
        String userSql = "UPDATE users SET name=?, username=?, email=? WHERE username=?";
        userPs = con.prepareStatement(userSql);
        userPs.setString(1, newName);
        userPs.setString(2, newUsername);
        userPs.setString(3, newEmail);
        userPs.setString(4, Username);

        int userUpdated = userPs.executeUpdate();

        if (userUpdated <= 0) {
            con.rollback();
            showInfo("Could not update user profile.");
            return;
        }

        // If username changed, update appointments table too
        if (!newUsername.equals(Username)) {
            String appointmentSql = "UPDATE appointments SET user_username=? WHERE user_username=?";
            appointmentPs = con.prepareStatement(appointmentSql);
            appointmentPs.setString(1, newUsername);
            appointmentPs.setString(2, Username);
            appointmentPs.executeUpdate();
        }

        con.commit();

        editedUsername = newUsername;
        Username = newUsername;
        Name = newName;
        Email = newEmail;
        isEdited = true;

        cancelEdit(event);

    } catch (Exception e) {
        e.printStackTrace();
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (checkPs != null) checkPs.close(); } catch (Exception e) {}
        try { if (userPs != null) userPs.close(); } catch (Exception e) {}
        try { if (appointmentPs != null) appointmentPs.close(); } catch (Exception e) {}
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (Exception e) {}
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