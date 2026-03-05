
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

  
   
    private String loggedUsername;
    private String loggedName;
    private String loggedEmail;

    public void setLoggedUser(String username, String name, String email) {
        this.loggedUsername = username;
        this.loggedName = name;
        this.loggedEmail = email;
    }

   
    @FXML private VBox lawyerListBox;
    @FXML private TextField searchField;
    @FXML private ImageView profileAvatar;

    @FXML
    public void initialize() {
        loadProfileAvatar();
        loadLawyers(null);
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

    // ---------- USER INFO ----------
    sb.append("Full Name: ").append(loggedName == null ? "-" : loggedName).append("\n");
    sb.append("Username: ").append(loggedUsername).append("\n");
    sb.append("Email: ").append(loggedEmail == null ? "-" : loggedEmail).append("\n\n");

    // ---------- APPOINTMENTS ----------
    sb.append("===== My Appointments (Past + Future) =====\n\n");

    String sql =
        "SELECT a.id, a.`date`, a.`time`, l.name AS lawyer_name, l.specialization, l.law_firm " +
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

                int apptId = rs.getInt("id");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String lawyerName = rs.getString("lawyer_name");
                String spec = rs.getString("specialization");
                String firm = rs.getString("law_firm");

                sb.append(count).append(") ")
                  .append(date).append("  ").append(time)
                  .append("  | Lawyer: ").append(lawyerName)
                  .append("  | ").append(spec == null ? "-" : spec)
                  .append("  | ").append(firm == null ? "-" : firm)
                  .append("  | (Appointment ID: ").append(apptId).append(")")
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

    
    javafx.scene.control.TextArea area = new javafx.scene.control.TextArea(sb.toString());
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

    private void loadProfileAvatar() {
        InputStream is = getClass().getResourceAsStream("/images/anonymous.png");
        if (is != null) {
            profileAvatar.setImage(new Image(is));
        }
    }
    @FXML
private void backToAllLawyers() {
    searchField.setText("");   // search box empty
    loadLawyers(null);         // show all again
}

   
    @FXML
    public void searchLawyer() {
        loadLawyers(searchField.getText());
    }

  
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

                    //open profile
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

                    // click => open profile
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

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

   
    
    private void openLawyerProfile(int lawyerId) {
        SelectedLawyer.id = lawyerId;
    try {
        File fxmlFile = new File("src/lawyerProfilefromUser.fxml");
        URL url = fxmlFile.toURI().toURL();

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        //  pass id and load DB data
        lawyerProfileUser controller = loader.getController();
         controller.setLoggedUser(loggedUsername, loggedName, loggedEmail);
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