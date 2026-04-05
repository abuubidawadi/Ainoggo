
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;


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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private BorderPane reg_page;

    @FXML
    private BorderPane reg_page1;

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
    private StackPane second_page_form;

    @FXML
    private HBox second_page_header;

    @FXML
    private ComboBox<String> spec_area;

    @FXML
    private Hyperlink switch_to_client_login1;

    @FXML
    private Hyperlink switch_to_client_reg;

    @FXML
    private Hyperlink switch_to_client_reg1;

    @FXML
    private Button theme_toggle_button_login;

    @FXML
    private Button theme_toggle_button_reg;

    @FXML
    private ImageView theme_toggle_icon_login;

    @FXML
    private ImageView theme_toggle_icon_reg;

    @FXML
    private Button theme_toggle_button_reg1;

    @FXML
    private ImageView theme_toggle_icon_reg1;

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

    private void openForgotPasswordDialog(String tableName, String accountLabel) {
    Dialog<ButtonType> dialog = new Dialog<>();

    Stage ownerStage = null;
    if (login_page != null && login_page.getScene() != null && login_page.getScene().getWindow() instanceof Stage) {
        ownerStage = (Stage) login_page.getScene().getWindow();
        dialog.initOwner(ownerStage);
    }

   dialog.setTitle("");
dialog.initStyle(javafx.stage.StageStyle.TRANSPARENT);
dialog.setHeaderText(null);

DialogPane pane = dialog.getDialogPane();
pane.setBackground(null);
pane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

dialog.setOnShown(e -> {
    if (pane.getScene() != null) {
        pane.getScene().setFill(javafx.scene.paint.Color.TRANSPARENT);
    }
});
    ButtonType updateButtonType = new ButtonType("Update Password", ButtonBar.ButtonData.OK_DONE);
    pane.getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

    Label titleLabel = new Label(accountLabel + " Password Reset");
    Label usernameLabel = new Label("Username");
    Label emailLabel = new Label("Email");
    Label newPassLabel = new Label("New Password");

    TextField usernameField = new TextField();
    usernameField.setPromptText("Enter username");

    TextField emailField = new TextField();
    emailField.setPromptText("Enter email");

    PasswordField newPasswordField = new PasswordField();
    newPasswordField.setPromptText("Enter new password");

    GridPane form = new GridPane();
    form.setHgap(12);
    form.setVgap(12);
    form.add(usernameLabel, 0, 0);
    form.add(usernameField, 1, 0);
    form.add(emailLabel, 0, 1);
    form.add(emailField, 1, 1);
    form.add(newPassLabel, 0, 2);
    form.add(newPasswordField, 1, 2);

    VBox wrapper = new VBox(16, titleLabel, form);
    wrapper.setPadding(new Insets(18));
    pane.setContent(wrapper);

    if (ownerStage != null) {
        pane.setPrefWidth(Math.max(380, ownerStage.getWidth() * 0.50));
        pane.setPrefHeight(Math.max(240, ownerStage.getHeight() * 0.50));
    } else {
        pane.setPrefWidth(420);
        pane.setPrefHeight(280);
    }

    boolean dark = ThemeManager.isDarkMode();

    String paneStyle = dark
            ? "-fx-background-color: #111111; -fx-border-color: #3a3a3a; -fx-border-width: 1.5; "
                    + "-fx-background-radius: 16; -fx-border-radius: 16;"
            : "-fx-background-color: #ffffff; -fx-border-color: #d7d7d7; -fx-border-width: 1.5; "
                    + "-fx-background-radius: 16; -fx-border-radius: 16;";

    String titleStyle = dark
            ? "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: 800;"
            : "-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: 800;";

    String labelStyle = dark
            ? "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700;"
            : "-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: 700;";

    String fieldStyle = dark
            ? "-fx-background-color: #1f1f1f; -fx-border-color: #444444; -fx-border-width: 1.5; "
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: white; "
                    + "-fx-prompt-text-fill: #a0a0a0; -fx-padding: 8 12 8 12;"
            : "-fx-background-color: #ffffff; -fx-border-color: #cfcfcf; -fx-border-width: 1.5; "
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; "
                    + "-fx-prompt-text-fill: #8a8a8a; -fx-padding: 8 12 8 12;";

    pane.setStyle(paneStyle);
    titleLabel.setStyle(titleStyle);
    usernameLabel.setStyle(labelStyle);
    emailLabel.setStyle(labelStyle);
    newPassLabel.setStyle(labelStyle);

    usernameField.setStyle(fieldStyle);
    emailField.setStyle(fieldStyle);
    newPasswordField.setStyle(fieldStyle);

    javafx.scene.Node updateBtn = pane.lookupButton(updateButtonType);
    javafx.scene.Node cancelBtn = pane.lookupButton(ButtonType.CANCEL);

    if (updateBtn != null) {
        updateBtn.setStyle(
                dark
                        ? "-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: 700; "
                                + "-fx-background-radius: 8; -fx-padding: 8 16 8 16;"
                        : "-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: 700; "
                                + "-fx-background-radius: 8; -fx-padding: 8 16 8 16;");
    }

    if (cancelBtn != null) {
        cancelBtn.setStyle(
                dark
                        ? "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; "
                                + "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; "
                                + "-fx-padding: 8 16 8 16;"
                        : "-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: black; "
                                + "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; "
                                + "-fx-padding: 8 16 8 16;");
    }

    Optional<ButtonType> result = dialog.showAndWait();

    if (!result.isPresent() || result.get() != updateButtonType) {
        return;
    }

    String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
    String email = emailField.getText() == null ? "" : emailField.getText().trim();
    String newPassword = newPasswordField.getText() == null ? "" : newPasswordField.getText();

    if (username.isEmpty()) {
        showError("Please enter your username.");
        return;
    }

    if (email.isEmpty()) {
        showError("Please enter your email.");
        return;
    }

    if (newPassword.isEmpty()) {
        showError("Please enter your new password.");
        return;
    }

    if (newPassword.length() < 8) {
        showError("New password must be at least 8 characters long.");
        return;
    }

    String checkSql = "SELECT id FROM " + tableName + " WHERE username = ? AND email = ? LIMIT 1";
    String updateSql = "UPDATE " + tableName + " SET password = ? WHERE username = ? AND email = ?";

    try (Connection con = database.connectDB()) {
        if (con == null) {
            showError("Cannot connect to database.");
            return;
        }

        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, username);
            checkPs.setString(2, email);

            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()) {
                    showError("Username and email do not match any account.");
                    return;
                }
            }
        }

        try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
            updatePs.setString(1, newPassword);
            updatePs.setString(2, username);
            updatePs.setString(3, email);

            int updated = updatePs.executeUpdate();

            if (updated > 0) {
                login_username.setText(username);
                login_pass.clear();
                showInfo("Password updated successfully.");
            } else {
                showError("Could not update password.");
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        showError("Database error while updating password.");
    }
}

    public void showLoginPage() {
        login_page.setVisible(true);
        reg_page.setVisible(false);
        if (second_page_form != null) {
            second_page_form.setVisible(false);
            second_page_form.setManaged(false);
        }
        if (reg_page1 != null) {
            reg_page1.setVisible(false);
            reg_page1.setManaged(false);
        }
    }

    public void showRegisterPage() {
        login_page.setVisible(false);
        reg_page.setVisible(true);
        if (second_page_form != null) {
            second_page_form.setVisible(false);
            second_page_form.setManaged(false);
        }
        if (reg_page1 != null) {
            reg_page1.setVisible(false);
            reg_page1.setManaged(false);
        }
    }

    private void showSecondPage() {
        login_page.setVisible(false);
        reg_page.setVisible(false);
        if (second_page_form != null) {
            second_page_form.setVisible(true);
            second_page_form.setManaged(true);
        }
        if (reg_page1 != null) {
            reg_page1.setVisible(true);
            reg_page1.setManaged(true);
        }

        javafx.application.Platform.runLater(this::applyCurrentTheme);
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

    private void openLawyerDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LawyerDashboard.fxml"));
            Parent root = loader.load();

            LawyerDashboardController controller = loader.getController();
            controller.setLawyerUsername(currentLawyerUsername);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root);
            scene.getStylesheets().clear();

            if (ThemeManager.isDarkMode()) {
                scene.getStylesheets().add(getClass().getResource("darklawyerdashboard.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("lawyerdashboard.css").toExternalForm());
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

   
    @FXML
    void uploadPhoto(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = chooser.showOpenDialog(
                ((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                File folder = new File("uploaded_photos");
                if (!folder.exists()) {
                    folder.mkdir();
                }

                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();

                File destination = new File(folder, newFileName);

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                photoPath = newFileName;

                Image uploadedImage = new Image(destination.toURI().toString());
                setCroppedImage(uploadedImage);

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
    openForgotPasswordDialog("lawyers", "Lawyer");
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
                    openLawyerDashboard(event);
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
                prepare.setString(4, pass);
                prepare.setInt(5, agreed);

                int rows = prepare.executeUpdate();
                if (rows > 0) {
                    currentLawyerUsername = username.trim();
                    // showInfo("Registered successfully! Please complete your lawyer
                    // information.");
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

            String updateSql = "UPDATE lawyers SET bar_reg_no=?, specialization=?, exp_years=?, law_firm=?, office_address=?, fee=?, bio=?, photo=? WHERE username=?";

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
                    // showInfo("Lawyer information saved!");
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
            stage.setMinHeight(650);
            stage.setMinWidth(1000);
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
    private void switchToClientRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();

            MainSceneController controller = loader.getController();
            controller.showRegisterPage();

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

            if (ainoggo_home_logo_2nd_page != null) {
                ainoggo_home_logo_2nd_page.setImage(new javafx.scene.image.Image(LOGO_BLACK));
            }

            if (ainoggo_home_text_2nd_page != null) {
                ainoggo_home_text_2nd_page.setStyle("-fx-fill: #000000; -fx-font-size: 24px; -fx-font-weight: 800;");
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

            if (theme_toggle_button_reg1 != null && theme_toggle_icon_reg1 != null) {
                theme_toggle_icon_reg1.setImage(new javafx.scene.image.Image(LIGHT_ICON));
            }

            if (ainoggo_home_logo_2nd_page != null) {
                ainoggo_home_logo_2nd_page.setImage(new javafx.scene.image.Image(LOGO_WHITE));
            }

            if (ainoggo_home_text_2nd_page != null) {
                ainoggo_home_text_2nd_page.setStyle("-fx-fill: white; -fx-font-size: 24px; -fx-font-weight: 800;");
            }

            ThemeManager.setDarkMode(true);
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

            if (ainoggo_home_logo_2nd_page != null) {
                ainoggo_home_logo_2nd_page.setImage(new Image(LOGO_WHITE));
            }
            if (ainoggo_home_text_2nd_page != null) {
                ainoggo_home_text_2nd_page.setStyle("-fx-fill: white; -fx-font-size: 24px; -fx-font-weight: 800;");
            }
            if (theme_toggle_icon_reg1 != null) {
                theme_toggle_icon_reg1.setImage(new Image(LIGHT_ICON));
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
            if (ainoggo_home_logo_2nd_page != null) {
                ainoggo_home_logo_2nd_page.setImage(new Image(LOGO_BLACK));
            }
            if (ainoggo_home_text_2nd_page != null) {
                ainoggo_home_text_2nd_page.setStyle("-fx-fill: #000000; -fx-font-size: 24px; -fx-font-weight: 800;");
            }
            if (theme_toggle_icon_reg1 != null) {
                theme_toggle_icon_reg1.setImage(new Image(DARK_ICON));
            }
        }
    }

    private void setCroppedImage(Image image) {
        if (image == null)
            return;

        double imgW = image.getWidth();
        double imgH = image.getHeight();

        double side = Math.min(imgW, imgH);
        double x = (imgW - side) / 2;
        double y = (imgH - side) / 2;

        uploaded_img.setViewport(new javafx.geometry.Rectangle2D(x, y, side, side));
        uploaded_img.setImage(image);
        uploaded_img.setFitWidth(190);
        uploaded_img.setFitHeight(190);
        uploaded_img.setPreserveRatio(false);
    }

    @FXML
    private void initialize() {
        spec_area.setItems(FXCollections.observableArrayList(
                "Family Law", "Criminal Law", "Corporate Law", "Immigration Law",
                "Property / Real Estate", "Civil Litigation", "Tax Law",
                "Labor / Employment", "Intellectual Property", "Other"));
        showLoginPage();
        String imagePath = getClass().getResource("/images/login_bg_lawyer.jpg").toExternalForm();

        login_page.setStyle(
                "-fx-background-image: url('" + imagePath + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");

        String imagePath2 = getClass().getResource("/images/register_bg_lawyer.jpg").toExternalForm();

        reg_page.setStyle(
                "-fx-background-image: url('" + imagePath2 + "');" +
                        "-fx-background-size: 100% 100%;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");

        Image defaultProfile = new Image(getClass().getResource("/images/anonymous.png").toExternalForm());
        setCroppedImage(defaultProfile);

        javafx.application.Platform.runLater(() -> applyCurrentTheme());
    }

}