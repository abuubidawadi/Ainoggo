
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

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
    private AnchorPane login_page;

    @FXML
    private AnchorPane login_page_form;

    @FXML
    private AnchorPane login_page_header;

    @FXML
    private AnchorPane login_page_img;

    @FXML
    private TextField login_username;

    @FXML
    private PasswordField login_pass;

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
    private AnchorPane reg_page;

    @FXML
    private AnchorPane reg_page_form;

    @FXML
    private AnchorPane reg_page_header;

    @FXML
    private AnchorPane reg_page_img;

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

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

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

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText(null);
                alert.setContentText("You have successfully registered");
                alert.showAndWait();

                reg_username.setText("");
                reg_name.setText("");
                reg_email.setText("");
                reg_pass.setText("");
                reg_conf_pass.setText("");
                reg_checkbox.setSelected(false);

                login_page.setVisible(true);
                reg_page.setVisible(false);

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
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + result.getString("name") + "!");
                alert.showAndWait();

                login_username.setText("");
                login_pass.setText("");

                // ekhane dashboard e jaoar code lekhum
            } else {
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
            login_page.setVisible(true);
            reg_page.setVisible(false);
        } else if (event.getSource() == reg_button_log_page) {
            login_page.setVisible(false);
            reg_page.setVisible(true);
        }
    }

    public void initialize() {
        login_page.setVisible(true);
        reg_page.setVisible(false);
    }

}
