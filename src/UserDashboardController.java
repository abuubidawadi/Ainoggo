import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.StackPane;

public class UserDashboardController {

    private String loggedUsername;
    private String loggedName;
    private String loggedEmail;

    @FXML
    private BorderPane rootPane;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> feeFilterCombo;
    @FXML
    private FlowPane lawyerGrid;
    @FXML
    private ImageView sliderImageView;
    @FXML
    private Button indicator1;
    @FXML
    private Button indicator2;
    @FXML
    private Button themeToggleButton;
    @FXML
    private ImageView themeToggleIcon;
    @FXML
    private ImageView searchIcon;
    @FXML
    private Button searchButton;
    @FXML
    private ImageView brandLogo;
    @FXML
    private StackPane sliderContainer;

    private Image currentSliderImage;

    private Timeline sliderTimeline;
    private int sliderIndex = 0;

    private final String DARK_ICON = getClass().getResource("/images/dark.png").toExternalForm();
    private final String LIGHT_ICON = getClass().getResource("/images/white.png").toExternalForm();

    private final String SEARCH_BLACK = getClass().getResource("/images/search_black.png").toExternalForm();
    private final String SEARCH_WHITE = getClass().getResource("/images/search_white.png").toExternalForm();

    private final String LOGO_BLACK = getClass().getResource("/images/logo_black_icon.png").toExternalForm();
    private final String LOGO_WHITE = getClass().getResource("/images/logo_white_icon.png").toExternalForm();

    private final String[] sliderImages = {
            "/images/slider1.png",
            "/images/slider2.png"
    };

    public void setLoggedUser(String username, String name, String email) {
        this.loggedUsername = username;
        this.loggedName = name;
        this.loggedEmail = email;
    }

    @FXML
    public void initialize() {
        setupFeeFilter();
        setupSlider();

        Platform.runLater(() -> {
            applyTheme();
            loadLawyers(null, getSelectedFeeFilter());
        });
    }

    private void setupFeeFilter() {
        feeFilterCombo.getItems().addAll(
                "All Fees",
                "Below Tk. 500",
                "Tk. 500 - Tk. 1000",
                "Tk. 1001 - Tk. 2000",
                "Above Tk. 2000");
        feeFilterCombo.setValue("All Fees");

        feeFilterCombo.setOnAction(e -> loadLawyers(searchField.getText(), getSelectedFeeFilter()));
    }

    private String getSelectedFeeFilter() {
        return feeFilterCombo == null ? "All Fees" : feeFilterCombo.getValue();
    }

    private void setupSlider() {
        sliderImageView.setManaged(false);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(sliderContainer.widthProperty());
        clip.heightProperty().bind(sliderContainer.heightProperty());
        clip.setArcWidth(48);
        clip.setArcHeight(48);
        sliderContainer.setClip(clip);

        sliderContainer.widthProperty().addListener((obs, oldVal, newVal) -> refreshSliderCrop());
        sliderContainer.heightProperty().addListener((obs, oldVal, newVal) -> refreshSliderCrop());

        setSliderImage(sliderImages[0]);
        updateIndicators();

        sliderTimeline = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> nextSlide()));
        sliderTimeline.setCycleCount(Timeline.INDEFINITE);
        sliderTimeline.play();
    }

    private void nextSlide() {
        sliderIndex = (sliderIndex + 1) % sliderImages.length;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(350), sliderImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.55);

        fadeOut.setOnFinished(event -> {
            setSliderImage(sliderImages[sliderIndex]);
            updateIndicators();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), sliderImageView);
            fadeIn.setFromValue(0.55);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void updateIndicators() {
        indicator1.getStyleClass().setAll("slider-indicator");
        indicator2.getStyleClass().setAll("slider-indicator");

        if (sliderIndex == 0) {
            indicator1.getStyleClass().add("active-indicator");
        } else {
            indicator2.getStyleClass().add("active-indicator");
        }
    }

    private void setSliderImage(String resourcePath) {
        try {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is != null) {
                currentSliderImage = new Image(is);
                sliderImageView.setImage(currentSliderImage);
                refreshSliderCrop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshSliderCrop() {
        if (currentSliderImage == null || sliderContainer == null)
            return;

        double width = sliderContainer.getWidth() > 0 ? sliderContainer.getWidth() : 956;
        double height = sliderContainer.getHeight() > 0 ? sliderContainer.getHeight() : 280;

        applyCenterCrop(sliderImageView, currentSliderImage, width, height);
    }

    private void applyCenterCrop(ImageView imageView, Image image, double boxWidth, double boxHeight) {
        if (image == null)
            return;

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

    @FXML
    public void searchLawyer() {
        loadLawyers(searchField.getText(), getSelectedFeeFilter());
    }

    private void loadLawyers(String keyword, String feeFilter) {
        lawyerGrid.getChildren().clear();

        StringBuilder sql = new StringBuilder(
                "SELECT id, name, email, specialization, law_firm, fee, photo " +
                        "FROM lawyers WHERE username IS NOT NULL ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("AND (name LIKE ? OR specialization LIKE ?) ");
        }

        if (feeFilter != null) {
            switch (feeFilter) {
                case "Below Tk. 500":
                    sql.append("AND fee < 500 ");
                    break;
                case "Tk. 500 - Tk. 1000":
                    sql.append("AND fee >= 500 AND fee <= 1000 ");
                    break;
                case "Tk. 1001 - Tk. 2000":
                    sql.append("AND fee >= 1001 AND fee <= 2000 ");
                    break;
                case "Above Tk. 2000":
                    sql.append("AND fee > 2000 ");
                    break;
                default:
                    break;
            }
        }

        sql.append("ORDER BY id DESC");

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean foundAny = false;

                while (rs.next()) {
                    foundAny = true;

                    int id = rs.getInt("id");
                    String name = safe(rs.getString("name"));
                    String specialization = safe(rs.getString("specialization"));
                    Object feeObj = rs.getObject("fee");
                    String feeText = (feeObj == null) ? "Tk. 0" : "Tk. " + String.format("%.0f", rs.getDouble("fee"));
                    String photo = rs.getString("photo");

                    VBox card = createLawyerCard(id, name, specialization, feeText, photo);
                    lawyerGrid.getChildren().add(card);
                }

                if (!foundAny) {
                    VBox emptyBox = new VBox(8);
                    emptyBox.setAlignment(Pos.CENTER);
                    emptyBox.getStyleClass().add("empty-card");

                    Label title = new Label("No lawyers found");
                    title.getStyleClass().add("empty-title");

                    Label sub = new Label("Try another name, category, or fee range.");
                    sub.getStyleClass().add("empty-subtitle");

                    emptyBox.getChildren().addAll(title, sub);
                    lawyerGrid.getChildren().add(emptyBox);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("Could not load lawyers. Check terminal.");
            a.showAndWait();
        }
    }

    private VBox createLawyerCard(int lawyerId, String name, String specialization, String feeText, String photoPath) {
        VBox card = new VBox(12);
        card.getStyleClass().add("lawyer-card");
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        StackPane photoHolder = new StackPane();
        photoHolder.getStyleClass().add("lawyer-photo-holder");
        photoHolder.setPrefSize(248, 248);
        photoHolder.setMinSize(248, 248);
        photoHolder.setMaxSize(248, 248);

        Image image = loadLawyerImage(photoPath);

        ImageView photoView = new ImageView();
        photoView.setFitWidth(248);
        photoView.setFitHeight(248);
        photoView.setSmooth(true);

        if (image != null) {
            photoView.setImage(image);
            applyCenterCrop(photoView, image, 248, 248);
        }

        Rectangle clip = new Rectangle(248, 248);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        photoHolder.setClip(clip);

        photoHolder.getChildren().add(photoView);
        photoHolder.setOnMouseClicked(e -> openLawyerProfile(lawyerId));

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("lawyer-name");

        Label categoryLabel = new Label(specialization);
        categoryLabel.getStyleClass().add("lawyer-category");

        Label feeLabel = new Label("Session Fee: " + feeText);
        feeLabel.getStyleClass().add("lawyer-fee");

        Button bookButton = new Button("Book Appointment");
        bookButton.getStyleClass().add("book-button");
        bookButton.setMaxWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> openLawyerProfile(lawyerId));

        photoView.setOnMouseClicked(e -> openLawyerProfile(lawyerId));

        card.getChildren().addAll(photoHolder, nameLabel, categoryLabel, feeLabel, bookButton);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(180), card);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(180), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        card.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        card.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });

        return card;
    }

    private Image loadLawyerImage(String photoFromDb) {
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        InputStream fallback = getClass().getResourceAsStream("/images/anonymous.png");
        return new Image(fallback);
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    @FXML
    private void showProfile(ActionEvent event) {
        if (loggedUsername == null || loggedUsername.trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("My Profile");
            a.setHeaderText(null);
            a.setContentText("User not found. Please login again.");
            a.showAndWait();
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Full Name: ").append(loggedName == null ? "-" : loggedName).append("\n");
        sb.append("Username: ").append(loggedUsername).append("\n");
        sb.append("Email: ").append(loggedEmail == null ? "-" : loggedEmail).append("\n\n");

        sb.append("===== My Appointments =====\n\n");

        String sql = "SELECT a.id, a.`date`, a.`time`, a.status, l.name AS lawyer_name, l.specialization, l.law_firm " +
                "FROM appointments a " +
                "JOIN lawyers l ON l.id = a.lawyer_id " +
                "WHERE a.user_username = ? " +
                "ORDER BY a.`date` DESC, a.`time` DESC";

        try (Connection con = database.connectDB();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loggedUsername);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;

                while (rs.next()) {
                    count++;

                    sb.append(count).append(") ")
                            .append(rs.getString("date")).append("  ")
                            .append(rs.getString("time")).append(" | Lawyer: ")
                            .append(safe(rs.getString("lawyer_name"))).append(" | ")
                            .append(safe(rs.getString("specialization"))).append(" | ")
                            .append(safe(rs.getString("law_firm"))).append(" | Status: ")
                            .append(safe(rs.getString("status")))
                            .append(" | Appointment ID: ")
                            .append(rs.getInt("id"))
                            .append("\n");
                }

                if (count == 0) {
                    sb.append("No appointments found.\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n[Could not load appointments. Check terminal.]\n");
        }

        TextArea area = new TextArea(sb.toString());
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(520);
        area.setPrefHeight(380);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("My Profile");
        a.setHeaderText(null);
        a.getDialogPane().setContent(area);
        a.showAndWait();
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 650);
            scene.getStylesheets().clear();

            if (ThemeManager.isDarkMode()) {
                scene.getStylesheets().add(getClass().getResource("darklogindesign.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("loginDesign.css").toExternalForm());
            }

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ainoggo");
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("Could not logout. Check terminal.");
            a.showAndWait();
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
            scene.getStylesheets().add(getClass().getResource("darkdashboard.css").toExternalForm());

            if (themeToggleIcon != null) {
                themeToggleIcon.setImage(new Image(LIGHT_ICON));
            }
            if (searchIcon != null) {
                searchIcon.setImage(new Image(SEARCH_WHITE));
            }
            if (brandLogo != null) {
                brandLogo.setImage(new Image(LOGO_WHITE));
            }

        } else {
            scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());

            if (themeToggleIcon != null) {
                themeToggleIcon.setImage(new Image(DARK_ICON));
            }
            if (searchIcon != null) {
                searchIcon.setImage(new Image(SEARCH_BLACK));
            }
            if (brandLogo != null) {
                brandLogo.setImage(new Image(LOGO_BLACK));
            }
        }
    }

    private void openLawyerProfile(int lawyerId) {
        SelectedLawyer.id = lawyerId;

        try {
            URL url = new File("src/lawyerProfilefromUser.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            lawyerProfileUser controller = loader.getController();
            controller.setLoggedUser(loggedUsername, loggedName, loggedEmail);
            controller.loadLawyerById(lawyerId);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 650);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("Could not open lawyer profile. Check terminal.");
            a.showAndWait();
        }
    }
}