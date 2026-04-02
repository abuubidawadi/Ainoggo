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

public class editProfileLawyerController {

    @FXML
    private ImageView ainoggo_home_logo;

    @FXML
    private Text ainoggo_home_text;

    @FXML
    private TextField bar_reg_no;

    @FXML
    private Hyperlink cancel_edit;

    @FXML
    private TextField consult_fee;

    @FXML
    private BorderPane edit_page;

    @FXML
    private TextField email;

    @FXML
    private TextField exp_yr;

    @FXML
    private StackPane form;

    @FXML
    private HBox header;

    @FXML
    private TextField law_firm;

    @FXML
    private TextField office_adress;

    @FXML
    private TextArea lawyer_bio;

    @FXML
    private TextField name;

    @FXML
    private TextField office_address;

    @FXML
    private Button save_button;

    @FXML
    private ComboBox<String> spec_area;

    @FXML
    private Button theme_toggle_button;

    @FXML
    private ImageView theme_toggle_icon;

    @FXML
    private Button upload_button;

    @FXML
    private ImageView uploaded_img;

    @FXML
    private TextField username;

    private String lawyerUsername;

    private File selectedImageFile;

    private final String LIGHT_CSS = getClass().getResource("editprofile.css").toExternalForm();
    private final String DARK_CSS = getClass().getResource("darkeditprofile.css").toExternalForm();

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();

    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    @FXML
    public void initialize() {
        // Initialize specialization options
        spec_area.getItems().addAll("Family Law", "Criminal Law", "Corporate Law", "Immigration Law",
                "Property / Real Estate", "Civil Litigation", "Tax Law",
                "Labor / Employment", "Intellectual Property", "Other");

        javafx.application.Platform.runLater(() -> {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(36);
            clip.setArcHeight(36);
            clip.widthProperty().bind(uploaded_img.fitWidthProperty());
            clip.heightProperty().bind(uploaded_img.fitHeightProperty());
            uploaded_img.setClip(clip);
            applyCurrentTheme();
        }); 
    }

    public void setLawyerUsername(String username) {
        this.lawyerUsername = username;
        loadCurrentData();
    }

    private void loadCurrentData() {
        String sql = "SELECT * FROM lawyers WHERE username = ?";
        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lawyerUsername);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name.setText(rs.getString("name"));
                username.setText(rs.getString("username"));
                email.setText(rs.getString("email"));
                bar_reg_no.setText(rs.getString("bar_reg_no"));
                exp_yr.setText(rs.getString("exp_years"));
                law_firm.setText(rs.getString("law_firm"));
                office_address.setText(rs.getString("office_address"));
                consult_fee.setText(String.valueOf(rs.getDouble("fee")));
                lawyer_bio.setText(rs.getString("bio"));
                spec_area.setValue(rs.getString("specialization"));

                String photo = rs.getString("photo");
                Image image = loadLawyerImage(photo);
                uploaded_img.setImage(image);
                applyCenterSquareCrop(uploaded_img, image, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @FXML
    void uploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            uploaded_img.setImage(new Image(selectedImageFile.toURI().toString()));
            applyCenterSquareCrop(uploaded_img, uploaded_img.getImage(), 200);
        }
    }

    @FXML
    void saveEdit(ActionEvent event) {
        String photoName = null;
        if (selectedImageFile != null) {
            try {
                File dir = new File("uploaded_photos");
                if (!dir.exists())
                    dir.mkdirs();
                photoName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
                Files.copy(selectedImageFile.toPath(), new File(dir, photoName).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String sql = "UPDATE lawyers SET name=?, email=?, specialization=?, exp_years=?, law_firm=?, office_adress=? fee=?, bio=?"
                + (photoName != null ? ", photo=?" : "") + " WHERE username=?";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.getText());
            ps.setString(2, email.getText());
            ps.setString(3, spec_area.getValue());
            ps.setString(4, exp_yr.getText());
            ps.setString(5, office_address.getText());
            ps.setString(6, law_firm.getText());
            ps.setDouble(7, Double.parseDouble(consult_fee.getText()));
            ps.setString(8, lawyer_bio.getText());
            if (photoName != null) {
                ps.setString(9, photoName);
                ps.setString(10, lawyerUsername);
            } else {
                ps.setString(9, lawyerUsername);
            }
            ps.executeUpdate();
            // showInfo("Profile updated successfully!");
            cancelEdit(event); // Return to dashboard
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cancelEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LawyerDashboard.fxml"));
            Parent root = loader.load();
            LawyerDashboardController controller = loader.getController();
            controller.setLawyerUsername(lawyerUsername);
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