
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainSceneController {

    @FXML
    private ImageView ainoggo_home_logo_log;

    @FXML
    private ImageView ainoggo_home_logo_reg;

    @FXML
    private Text ainoggo_home_text_log;

    @FXML
    private Text ainoggo_home_text_reg;

    @FXML
    private Button login_button;

    @FXML
    private Button login_button_reg_page;

    @FXML
    private Hyperlink login_forgot_password;

    @FXML
    private BorderPane login_page;

    @FXML
    private StackPane login_page_form;

    @FXML
    private HBox login_page_header;

    @FXML
    private StackPane login_page_img;

    @FXML
    private PasswordField login_pass;

    @FXML
    private TextField login_username;

    @FXML
    private Button reg_button_log_page;

    @FXML
    private CheckBox reg_checkbox;

    @FXML
    private PasswordField reg_conf_pass;

    @FXML
    private TextField reg_email;

    @FXML
    private TextField reg_name;

    @FXML
    private BorderPane reg_page;

    @FXML
    private StackPane reg_page_form;

    @FXML
    private HBox reg_page_header;

    @FXML
    private StackPane reg_page_img;

    @FXML
    private PasswordField reg_pass;

    @FXML
    private TextField reg_username;

    @FXML
    private Button register_button;

    @FXML
    private Hyperlink switch_to_lawyer_login;

    @FXML
    private Hyperlink switch_to_lawyer_reg;

    @FXML
    private Button theme_toggle_button_login;

    @FXML
    private Button theme_toggle_button_reg;

    @FXML
    private ImageView theme_toggle_icon_login;

    @FXML
    private ImageView theme_toggle_icon_reg;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

    public void showLoginPage() {
        login_page.setVisible(true);
        login_page.setManaged(true);
        reg_page.setVisible(false);
        reg_page.setManaged(false);
    }

    public void showRegisterPage() {
        login_page.setVisible(false);
        login_page.setManaged(false);
        reg_page.setVisible(true);
        reg_page.setManaged(true);
    }

    public void registration(ActionEvent event) {

        String checkUsername = "SELECT * FROM users WHERE username = ?";
        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(checkUsername);
            prepare.setString(1, reg_username.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists");
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (reg_name.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your name");
            alert.showAndWait();
        } else if (reg_email.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your email");
            alert.showAndWait();
        } else if (reg_username.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your username");
            alert.showAndWait();
        } else if (reg_pass.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your password");
            alert.showAndWait();
        } else if (reg_conf_pass.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please confirm your password");
            alert.showAndWait();
        } else if (!reg_pass.getText().equals(reg_conf_pass.getText())) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Password does not match");
            alert.showAndWait();
        } else if (!reg_checkbox.isSelected()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please accept the terms and conditions");
            alert.showAndWait();
        } else if (reg_pass.getText().length() < 8) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Password must be at least 8 characters long");
            alert.showAndWait();
        } else {
            connect = database.connectDB();
            String sql = "INSERT INTO users (name, username, email, password, agreed) VALUES (?, ?, ?, ?, ?)";
            try {
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, reg_name.getText());
                prepare.setString(2, reg_username.getText());
                prepare.setString(3, reg_email.getText());
                prepare.setString(4, reg_pass.getText());
                prepare.setBoolean(5, reg_checkbox.isSelected());
                prepare.executeUpdate();

                // alert = new Alert(Alert.AlertType.INFORMATION);
                // alert.setTitle("Registration Successful");
                // alert.setHeaderText(null);
                // alert.setContentText("You have successfully registered");
                // alert.showAndWait();

                reg_username.setText("");
                reg_name.setText("");
                reg_email.setText("");
                reg_pass.setText("");
                reg_conf_pass.setText("");
                reg_checkbox.setSelected(false);

                showLoginPage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void login(ActionEvent event) {

        if (event.getSource() != login_button)
            return;

        String userInput = login_username.getText();
        String passInput = login_pass.getText();

        if (userInput == null || userInput.trim().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your username or email");
            alert.showAndWait();
            return;
        }

        if (passInput == null || passInput.trim().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your password");
            alert.showAndWait();
            return;
        }

        String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";

        try {
            connect = database.connectDB();

            if (connect == null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("Cannot connect to database.");
                alert.showAndWait();
                return;
            }

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, userInput.trim());
            prepare.setString(2, userInput.trim());
            prepare.setString(3, passInput);

            result = prepare.executeQuery();

            if (result.next()) {
                String loggedUsername = result.getString("username");
                String loggedName = result.getString("name");
                String loggedEmail = result.getString("email");

                login_username.setText("");
                login_pass.setText("");

                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
                    Parent root = loader.load();

                    UserDashboardController controller = loader.getController();
                    controller.setLoggedUser(loggedUsername, loggedName, loggedEmail);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1000, 650));
                    stage.setResizable(true);
                    stage.setTitle("Ainoggo - Dashboard");
                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username/email or password.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong. Check the terminal.");
            alert.showAndWait();
        } finally {
            try {
                if (result != null)
                    result.close();
            } catch (Exception ignored) {
            }
            try {
                if (prepare != null)
                    prepare.close();
            } catch (Exception ignored) {
            }
            try {
                if (connect != null)
                    connect.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void forgotPassword(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Forgot password feature is not available yet.");
        alert.showAndWait();
    }

    public void switchbetweenloginandreg(ActionEvent event) {

        if (event.getSource() == login_button_reg_page) {
            showLoginPage();
        } else if (event.getSource() == reg_button_log_page) {
            showRegisterPage();
        }
    }

    @FXML
    private void switchToLawyerLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lawyerlogreg.fxml"));
            Parent root = loader.load();

            LawyerEnterSceneController controller = loader.getController();
            controller.showLoginPage(); // force login page visible

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
        }
    }

    @FXML
    private void switchToLawyerRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lawyerlogreg.fxml"));
            Parent root = loader.load();

            LawyerEnterSceneController controller = loader.getController();
            controller.showRegisterPage(); // force register page visible

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
        }
    }

    private void applyCurrentTheme() {
        Scene scene = login_page.getScene();
        if (scene == null)
            return;

        scene.getStylesheets().clear();

        if (ThemeManager.isDarkMode()) {
            scene.getStylesheets().add(DARK_CSS);

            ainoggo_home_logo_log.setImage(new javafx.scene.image.Image(LOGO_WHITE));
            ainoggo_home_logo_reg.setImage(new javafx.scene.image.Image(LOGO_WHITE));

            if (theme_toggle_icon_login != null) {
                theme_toggle_icon_login.setImage(new javafx.scene.image.Image(LIGHT_ICON));
            }

            if (theme_toggle_icon_reg != null) {
                theme_toggle_icon_reg.setImage(new javafx.scene.image.Image(LIGHT_ICON));
            }
        } else {
            scene.getStylesheets().add(LIGHT_CSS);

            ainoggo_home_logo_log.setImage(new javafx.scene.image.Image(LOGO_BLACK));
            ainoggo_home_logo_reg.setImage(new javafx.scene.image.Image(LOGO_BLACK));

            if (theme_toggle_icon_login != null) {
                theme_toggle_icon_login.setImage(new javafx.scene.image.Image(DARK_ICON));
            }

            if (theme_toggle_icon_reg != null) {
                theme_toggle_icon_reg.setImage(new javafx.scene.image.Image(DARK_ICON));
            }
        }
    }

    public void initialize() {
        login_page.setVisible(true);
        login_page.setManaged(true);
        reg_page.setVisible(false);
        reg_page.setManaged(false);
        String imagePath = getClass().getResource("/images/login_bg.jpg").toExternalForm();

        login_page.setStyle(
                "-fx-background-image: url('" + imagePath + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");

        String imagePath2 = getClass().getResource("/images/register_bg.jpg").toExternalForm();

        reg_page.setStyle(
                "-fx-background-image: url('" + imagePath2 + "');" +
                        "-fx-background-size: 100% 100%;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");

        javafx.application.Platform.runLater(() -> applyCurrentTheme());
    }

    private final String LIGHT_CSS = getClass().getResource("loginDesign.css").toExternalForm();
    private final String DARK_CSS = getClass().getResource("darklogindesign.css").toExternalForm();

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();

    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    @FXML
    private void toggleTheme(ActionEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();

        scene.getStylesheets().clear();

        if (ThemeManager.isDarkMode()) {
            scene.getStylesheets().add(LIGHT_CSS);

            ainoggo_home_logo_log.setImage(new javafx.scene.image.Image(LOGO_BLACK));
            ainoggo_home_logo_reg.setImage(new javafx.scene.image.Image(LOGO_BLACK));

            if (theme_toggle_icon_login != null) {
                theme_toggle_icon_login.setImage(new javafx.scene.image.Image(DARK_ICON));
            }

            if (theme_toggle_icon_reg != null) {
                theme_toggle_icon_reg.setImage(new javafx.scene.image.Image(DARK_ICON));
            }

            ThemeManager.setDarkMode(false);
        } else {
            scene.getStylesheets().add(DARK_CSS);

            ainoggo_home_logo_log.setImage(new javafx.scene.image.Image(LOGO_WHITE));
            ainoggo_home_logo_reg.setImage(new javafx.scene.image.Image(LOGO_WHITE));

            if (theme_toggle_icon_login != null) {
                theme_toggle_icon_login.setImage(new javafx.scene.image.Image(LIGHT_ICON));
            }

            if (theme_toggle_icon_reg != null) {
                theme_toggle_icon_reg.setImage(new javafx.scene.image.Image(LIGHT_ICON));
            }

            ThemeManager.setDarkMode(true);
        }
    }
}
