import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LawyerEnterSceneController {

    @FXML
    private ImageView ainoggo_home_logo_2nd_page;

    @FXML
    private ImageView ainoggo_home_logo_log;

    @FXML
    private ImageView ainoggo_home_logo_reg;

    @FXML
    private Text ainoggo_home_text_2nd_page;

    @FXML
    private Text ainoggo_home_text_log;

    @FXML
    private Text ainoggo_home_text_reg;

    @FXML
    private TextField bar_reg_no;

    @FXML
    private TextField consult_fee;

    @FXML
    private TextField exp_yr;

    @FXML
    private TextField law_firm;

    @FXML
    private TextArea lawyer_bio;

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
    private PasswordField login_pass;

    @FXML
    private TextField login_username;

    @FXML
    private TextField office_address;

    @FXML
    private Button proceed_button;

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
    private AnchorPane reg_page1;

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
    private AnchorPane second_page_form;

    @FXML
    private AnchorPane second_page_header;

    @FXML
    private ComboBox<String> spec_area;

    @FXML
    private Hyperlink switch_to_client_login1;

    @FXML
    private Hyperlink switch_to_client_reg;

    @FXML
    private Hyperlink switch_to_client_reg2;

    @FXML
    private Button upload_button;

    @FXML
    private ImageView uploaded_img;

    private Alert alert;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private String currentLawyerUsername;
    private String photoPath;

    public void showLoginPage() {
        login_page.setVisible(true);
        reg_page.setVisible(false);
        if (second_page_form != null)
            second_page_form.setVisible(false);
        if (reg_page1 != null)
            reg_page1.setVisible(false);
    }

    public void showRegisterPage() {
        login_page.setVisible(false);
        reg_page.setVisible(true);
        if (second_page_form != null)
            second_page_form.setVisible(false);
        if (reg_page1 != null)
            reg_page1.setVisible(false);
    }

    private void showSecondPage() {
        login_page.setVisible(false);
        reg_page.setVisible(false);
        if (second_page_form != null)
            second_page_form.setVisible(true);
        if (reg_page1 != null)
            reg_page1.setVisible(true); 
    }

    private void showError(String msg) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    void proceed(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Proceed");
        a.setHeaderText(null);
        a.setContentText("Proceed clicked (feature will be added later).");
        a.showAndWait();
    }

    @FXML
    void uploadPhoto(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = chooser.showOpenDialog(
                ((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {

                // Create folder if not exists
                File folder = new File("uploaded_photos");
                if (!folder.exists()) {
                    folder.mkdir();
                }

                // Unique file name
                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();

                File destination = new File(folder, newFileName);

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Save only file name
                photoPath = newFileName;

                uploaded_img.setImage(new Image(destination.toURI().toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNumeric(String s) {
        if (s == null || s.trim().isEmpty())
            return false;
        try {
            Double.parseDouble(s.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    void forgotPassword(ActionEvent event) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Forgot password is not available yet.");
        alert.showAndWait();
    }

    @FXML
    void login(ActionEvent event) {
        String userOrEmail = login_username.getText();
        String pass = login_pass.getText();

        if (userOrEmail == null || userOrEmail.trim().isEmpty()) {
            showError("Please enter your username or email.");
            return;
        }
        if (pass == null || pass.trim().isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        String sql = "SELECT username, password FROM lawyers WHERE (username = ? OR email = ?) LIMIT 1";

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, userOrEmail.trim());
            prepare.setString(2, userOrEmail.trim());

            result = prepare.executeQuery();

            if (result.next()) {
                String dbUsername = result.getString("username");
                String dbPassword = result.getString("password");

                if (pass.equals(dbPassword)) {
                    currentLawyerUsername = dbUsername;
                    showInfo("Login successful!");

                    
                } else {
                    showError("Wrong password.");
                }
            } else {
                showError("Account not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error while logging in.");
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

    @FXML
    void registration(ActionEvent event) {

        // ---- PART A: Register button (basic info) ----
        if (event.getSource() == register_button) {

            String name = reg_name.getText();
            String username = reg_username.getText();
            String email = reg_email.getText();
            String pass = reg_pass.getText();
            String conf = reg_conf_pass.getText();
            int agreed = reg_checkbox.isSelected() ? 1 : 0;

            if (name == null || name.trim().isEmpty()) {
                showError("Please enter your full name.");
                return;
            } else if (username == null || username.trim().isEmpty()) {
                showError("Please enter your username.");
                return;
            } else if (email == null || email.trim().isEmpty()) {
                showError("Please enter your email.");
                return;
            } else if (pass == null || pass.trim().isEmpty()) {
                showError("Please enter your password.");
                return;
            } else if (conf == null || conf.trim().isEmpty()) {
                showError("Please confirm your password.");
                return;
            } else if (!pass.equals(conf)) {
                showError("Passwords do not match.");
                return;
            } else if (agreed == 0) {
                showError("Please agree to the Terms & Conditions.");
                return;
            } else if (pass.length() < 8) {
                showError("Password must be at least 8 characters long.");
                return;
            }

            String checkSql = "SELECT id FROM lawyers WHERE username = ? OR email = ? LIMIT 1";
            String insertSql = "INSERT INTO lawyers (name, username, email, password, agreed) VALUES (?,?,?,?,?)";

            try {
                connect = database.connectDB();

                prepare = connect.prepareStatement(checkSql);
                prepare.setString(1, username.trim());
                prepare.setString(2, email.trim());
                result = prepare.executeQuery();

                if (result.next()) {
                    showError("Username or Email already exists.");
                    return;
                }

                prepare.close();
                prepare = connect.prepareStatement(insertSql);
                prepare.setString(1, name.trim());
                prepare.setString(2, username.trim());
                prepare.setString(3, email.trim());
                prepare.setString(4, pass); // (same as your user flow; later you can hash)
                prepare.setInt(5, agreed);

                int rows = prepare.executeUpdate();
                if (rows > 0) {
                    currentLawyerUsername = username.trim();
                    showInfo("Registered successfully! Please complete your lawyer information.");
                    showSecondPage();
                } else {
                    showError("Registration failed.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showError("Database error while registering.");
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

        if (event.getSource() == proceed_button) {

            if (currentLawyerUsername == null || currentLawyerUsername.isEmpty()) {
                showError("No registered user found. Please register first.");
                return;
            }

            String bar_reg = bar_reg_no.getText();
            String specialization = (spec_area.getValue() == null) ? "" : (String) spec_area.getValue();
            String years = exp_yr.getText();
            String firm = law_firm.getText();
            String address = office_address.getText();
            String feeStr = consult_fee.getText();
            String bio = lawyer_bio.getText();

            if (bar_reg == null || bar_reg.trim().isEmpty()) {
                showError("Please enter your bar registration number.");
                return;
            } else if (specialization.trim().isEmpty()) {
                showError("Please choose your area of specialization.");
                return;
            } else if (!isNumeric(years)) {
                showError("Years of experience must be a number.");
                return;
            } else if (address == null || address.trim().isEmpty()) {
                showError("Please enter your office address.");
                return;
            } else if (!isNumeric(feeStr)) {
                showError("Consultation fee must be a number.");
                return;
            }

            int expYears = (int) Double.parseDouble(years.trim());
            double fee = Double.parseDouble(feeStr.trim());

            String updateSql = "UPDATE lawyers SET bar_reg_no=?, specialization=?, exp_years=?, law_firm=?, office_address=?, fee=?, bio=?, photo=? "
                    +
                    "WHERE username=?";

            try {
                connect = database.connectDB();
                prepare = connect.prepareStatement(updateSql);

                prepare.setString(1, bar_reg.trim());
                prepare.setString(2, specialization);
                prepare.setInt(3, expYears);
                prepare.setString(4, (firm == null) ? "" : firm.trim());
                prepare.setString(5, address.trim());
                prepare.setDouble(6, fee);

                prepare.setString(7, (bio == null) ? "" : bio.trim());

                String photoToSave = (photoPath == null || photoPath.isEmpty())
                        ? "main\\resources\\images\\anonymous.png"
                        : photoPath;
                prepare.setString(8, photoToSave);

                prepare.setString(9, currentLawyerUsername);

                int rows = prepare.executeUpdate();
                if (rows > 0) {
                    showInfo("Lawyer information saved!");
                    showLoginPage();
                } else {
                    showError("Could not save lawyer information.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showError("Database error while saving lawyer information.");
            } finally {
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
    }

    @FXML
    void switchbetweenloginandreg(ActionEvent event) {
        try {
            if (event.getSource() == reg_button_log_page) {
                showRegisterPage();
                return;
            }

            if (event.getSource() == login_button_reg_page) {
                showLoginPage();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error.");
        }
    }

    @FXML
    private void switchToClientLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();

            MainSceneController controller = loader.getController();
            controller.showLoginPage();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToClientRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();

            MainSceneController controller = loader.getController();
            controller.showRegisterPage();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        spec_area.setItems(FXCollections.observableArrayList(
                "Family Law", "Criminal Law", "Corporate Law", "Immigration Law",
                "Property / Real Estate", "Civil Litigation", "Tax Law",
                "Labor / Employment", "Intellectual Property", "Other"));
        showLoginPage();
    }

}
