// import java.io.File;
// import java.net.URL;
// import java.io.InputStream;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import javafx.scene.Node;

// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.VBox;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.Priority;


// public class UserDashboardController {
//     private String loggedUsername;
// private String loggedName;
// private String loggedEmail;

// public void setLoggedUser(String username, String name, String email) {
//     this.loggedUsername = username;
//     this.loggedName = name;
//     this.loggedEmail = email;
// }
//  @FXML
//     private void showProfile(ActionEvent event) {
//         Alert a = new Alert(Alert.AlertType.INFORMATION);
//         a.setTitle("My Profile");
//         //a.setHeaderText(null);

//         String msg =
//             "Full Name: " + (loggedName == null ? "-" : loggedName) + "\n" +
//             "Username: " + (loggedUsername == null ? "-" : loggedUsername) + "\n" +
//             "Email: " + (loggedEmail == null ? "-" : loggedEmail);

//         a.setContentText(msg);
//         a.showAndWait();
//     }


//     @FXML private VBox lawyerListBox;
//     @FXML private TextField searchField;
//       @FXML private ImageView profileAvatar; 

//     @FXML
//     public void initialize() {
//             loadProfileAvatar();
//         loadLawyers(null);
//     }

//     // ✅ safe image loader (uploaded_photos > resources lawyers > anonymous)
//     private Image loadLawyerImage(String photoFromDb) {

//         // 1) DB null/empty => anonymous
//         if (photoFromDb == null || photoFromDb.trim().isEmpty()) {
//             return new Image(getClass().getResourceAsStream("/images/anonymous.png"));
//         }

//         // 2) normalize path -> keep only filename
//         String photo = photoFromDb.trim().replace("\\", "/");
//         if (photo.contains("/")) {
//             photo = photo.substring(photo.lastIndexOf("/") + 1);
//         }

//         // 3) uploaded_photos folder (project root)
//         File f = new File("uploaded_photos/" + photo);
//         if (f.exists()) {
//             return new Image(f.toURI().toString());
//         }

//         // 4) resources: /images/lawyers (optional)
//         InputStream is2 = getClass().getResourceAsStream("/images/lawyers/" + photo);
//         if (is2 != null) {
//             return new Image(is2);
//         }

//         // 5) fallback
//         return new Image(getClass().getResourceAsStream("/images/anonymous.png"));
//     }

//     private void loadLawyers(String nameQuery) {
//         lawyerListBox.getChildren().clear();

//         // ✅ Only registered lawyers (username not null)
//         String sql =
//             "SELECT id,name, email, specialization, law_firm, fee, photo " +
//             "FROM lawyers " +
//             "WHERE username IS NOT NULL ";

//         if (nameQuery != null && !nameQuery.trim().isEmpty()) {
//             sql += "AND name LIKE ? ";
//         }

//         sql += "ORDER BY id DESC LIMIT 20";

//         try (Connection con = database.connectDB();
//              PreparedStatement ps = con.prepareStatement(sql)) {

//             if (nameQuery != null && !nameQuery.trim().isEmpty()) {
//                 ps.setString(1, "%" + nameQuery.trim() + "%");
//             }

//             try (ResultSet rs = ps.executeQuery()) {
                
//                 while (rs.next()) {

//     int id = rs.getInt("id");

//     String name = rs.getString("name");
//     String email = rs.getString("email");
//     String specialization = rs.getString("specialization");
//     String lawFirm = rs.getString("law_firm");
//     double fee = rs.getDouble("fee");
//     String photo = rs.getString("photo");

//     HBox card = new HBox(20);
//     card.getStyleClass().add("card");

//     ImageView iv = new ImageView();
//     iv.setFitHeight(100);
//     iv.setFitWidth(100);
//     iv.setPreserveRatio(true);
//     iv.setImage(loadLawyerImage(photo));

//     // ✅ image click => profile page
//     iv.setOnMouseClicked(e -> openLawyerProfile(id));

//     VBox info = new VBox(6);

//     Label nameLb = new Label(name);
//     nameLb.getStyleClass().add("name");

//     Label specLb = new Label("Specialization: " + safe(specialization));
//     specLb.getStyleClass().add("sub");

//     Label firmLb = new Label("Law Firm: " + safe(lawFirm));
//     firmLb.getStyleClass().add("sub");

//     Label emailLb = new Label("Email: " + safe(email));
//     emailLb.getStyleClass().add("sub");

//     // fee null হলে 0 দেখায়, তাই safe format
//     String feeText = (rs.wasNull()) ? "-" : "৳" + fee;
//     Label feeLb = new Label("Fee/hr: " + feeText);
//     feeLb.getStyleClass().add("sub");

//     info.getChildren().addAll(nameLb, specLb, firmLb, emailLb, feeLb);

//     // ✅ right side button
//     Region spacer = new Region();
//     HBox.setHgrow(spacer, Priority.ALWAYS);

//     Button bookBtn = new Button("Book Appointment");
//     bookBtn.getStyleClass().add("bookBtn");
//     bookBtn.setOnAction(e -> openLawyerProfile(id));

//     card.getChildren().addAll(iv, info, spacer, bookBtn);
//     lawyerListBox.getChildren().add(card);
// }
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//             Alert a = new Alert(Alert.AlertType.ERROR);
//             a.setTitle("Error");
//             a.setHeaderText(null);
//             a.setContentText("Could not load lawyers. Check terminal.");
//             a.showAndWait();
//         }
//     }

//     private String safe(String s) {
//         return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
//     }

//     @FXML
//     public void searchLawyer() {
//         loadLawyers(searchField.getText());
//     }
//     private void loadProfileAvatar() {
//     var is = getClass().getResourceAsStream("/images/anonymous.png");
//     if (is != null) {
//         profileAvatar.setImage(new Image(is));
//     }
// }
// private void openLawyerProfile(ActionEvent event) {
//     try {
//         // src folder থেকে load (classpath না)
//         File fxmlFile = new File("src/lawyerProfilefromUser.fxml");
//         URL url = fxmlFile.toURI().toURL();

//         FXMLLoader loader = new FXMLLoader(url);
//         Parent root = loader.load();

//         Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//         stage.setScene(new Scene(root));
//         stage.show();

//     } catch (Exception e) {
//         e.printStackTrace();
//     }
// }



// }
import java.io.File;
import java.io.InputStream;
import java.net.URL;
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
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

public class UserDashboardController {

    // =========================
    // Logged-in user (optional)
    // =========================
    private String loggedUsername;
    private String loggedName;
    private String loggedEmail;

    public void setLoggedUser(String username, String name, String email) {
        this.loggedUsername = username;
        this.loggedName = name;
        this.loggedEmail = email;
    }

    // =========================
    // FXML
    // =========================
    @FXML private VBox lawyerListBox;
    @FXML private TextField searchField;
    @FXML private ImageView profileAvatar;

    @FXML
    public void initialize() {
        loadProfileAvatar();
        loadLawyers(null);
    }

    // =========================
    // My Profile (avatar button)
    // =========================
    @FXML
    private void showProfile(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("My Profile");

        String msg =
            "Full Name: " + (loggedName == null ? "-" : loggedName) + "\n" +
            "Username: " + (loggedUsername == null ? "-" : loggedUsername) + "\n" +
            "Email: " + (loggedEmail == null ? "-" : loggedEmail);

        a.setContentText(msg);
        a.showAndWait();
    }

    private void loadProfileAvatar() {
        InputStream is = getClass().getResourceAsStream("/images/anonymous.png");
        if (is != null) {
            profileAvatar.setImage(new Image(is));
        }
    }

    // =========================
    // Search
    // =========================
    @FXML
    public void searchLawyer() {
        loadLawyers(searchField.getText());
    }

    // =========================
    // Load Lawyers from DB
    // =========================
    private void loadLawyers(String nameQuery) {
        lawyerListBox.getChildren().clear();

        String sql =
            "SELECT id, name, email, specialization, law_firm, fee, photo " +
            "FROM lawyers " +
            "WHERE username IS NOT NULL ";

        boolean hasQuery = (nameQuery != null && !nameQuery.trim().isEmpty());
        if (hasQuery) {
            sql += "AND name LIKE ? ";
        }

        sql += "ORDER BY id DESC LIMIT 20";

        try (Connection con = database.connectDB();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (hasQuery) {
                ps.setString(1, "%" + nameQuery.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    int id = rs.getInt("id");

                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String specialization = rs.getString("specialization");
                    String lawFirm = rs.getString("law_firm");

                    // fee null handle ঠিকভাবে
                    Object feeObj = rs.getObject("fee");
                    String feeText = (feeObj == null) ? "-" : ("৳" + rs.getDouble("fee"));

                    String photo = rs.getString("photo");

                    // ---------- CARD ----------
                    HBox card = new HBox(20);
                    card.getStyleClass().add("card");

                    // ---------- IMAGE ----------
                    ImageView iv = new ImageView();
                    iv.setFitHeight(100);
                    iv.setFitWidth(100);
                    iv.setPreserveRatio(true);
                    iv.setImage(loadLawyerImage(photo));

                    // ✅ click image => open profile
                    iv.setOnMouseClicked(e -> openLawyerProfile(id));

                    // ---------- INFO ----------
                    VBox info = new VBox(6);

                    Label nameLb = new Label(safe(name));
                    nameLb.getStyleClass().add("name");

                    Label specLb = new Label("Specialization: " + safe(specialization));
                    specLb.getStyleClass().add("sub");

                    Label firmLb = new Label("Law Firm: " + safe(lawFirm));
                    firmLb.getStyleClass().add("sub");

                    Label emailLb = new Label("Email: " + safe(email));
                    emailLb.getStyleClass().add("sub");

                    Label feeLb = new Label("Fee/hr: " + feeText);
                    feeLb.getStyleClass().add("sub");

                    info.getChildren().addAll(nameLb, specLb, firmLb, emailLb, feeLb);

                    // ---------- RIGHT SIDE BUTTON ----------
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button bookBtn = new Button("Book Appointment");
                    bookBtn.getStyleClass().add("bookBtn");

                    // ✅ click => open profile
                    bookBtn.setOnAction(e -> openLawyerProfile(id));

                    card.getChildren().addAll(iv, info, spacer, bookBtn);
                    lawyerListBox.getChildren().add(card);
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

    // =========================
    // Image loader
    // uploaded_photos > resources lawyers > anonymous
    // =========================
    private Image loadLawyerImage(String photoFromDb) {

        // 1) DB null/empty => anonymous
        if (photoFromDb == null || photoFromDb.trim().isEmpty()) {
            InputStream is = getClass().getResourceAsStream("/images/anonymous.png");
            return new Image(is);
        }

        // 2) normalize -> only filename
        String photo = photoFromDb.trim().replace("\\", "/");
        if (photo.contains("/")) {
            photo = photo.substring(photo.lastIndexOf("/") + 1);
        }

        // 3) uploaded_photos folder (project root)
        File f = new File("uploaded_photos/" + photo);
        if (f.exists()) {
            return new Image(f.toURI().toString());
        }

        // 4) resources fallback: /images/lawyers/<photo>
        InputStream is2 = getClass().getResourceAsStream("/images/lawyers/" + photo);
        if (is2 != null) {
            return new Image(is2);
        }

        // 5) fallback anonymous
        InputStream is3 = getClass().getResourceAsStream("/images/anonymous.png");
        return new Image(is3);
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    // =========================
    // Open Lawyer Profile Page
    // (src file path load, NO moving files)
    // =========================
    // private void openLawyerProfile(int lawyerId) {
    //     try {
    //         // ✅ src থেকে load
    //         File fxmlFile = new File("src/lawyerProfilefromUser.fxml");
    //         URL url = fxmlFile.toURI().toURL();

    //         FXMLLoader loader = new FXMLLoader(url);
    //         Parent root = loader.load();

    //         // NOTE:
    //         // তোমার lawyerProfileUser controller এখনো data receive করার method দেয়নি,
    //         // তাই profile page শুধু open হবে।
    //         // future এ চাইলে: loader.getController() নিয়ে setData(lawyerId) করা যাবে।

    //         Stage stage = (Stage) lawyerListBox.getScene().getWindow();
    //         stage.setScene(new Scene(root));
    //         stage.show();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         Alert a = new Alert(Alert.AlertType.ERROR);
    //         a.setTitle("Error");
    //         a.setHeaderText(null);
    //         a.setContentText("Could not open lawyer profile. Check terminal.");
    //         a.showAndWait();
    //     }
    // }
    private void openLawyerProfile(int lawyerId) {
    try {
        File fxmlFile = new File("src/lawyerProfilefromUser.fxml");
        URL url = fxmlFile.toURI().toURL();

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        // ✅ NEW: pass id and load DB data
        lawyerProfileUser controller = loader.getController();
        controller.loadLawyerById(lawyerId);

        Stage stage = (Stage) lawyerListBox.getScene().getWindow();
        stage.setScene(new Scene(root));
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