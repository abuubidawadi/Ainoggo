import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UserDashboardController {

    @FXML private VBox lawyerListBox;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        loadLawyers(null); // first time load all
    }

    private void loadLawyers(String nameQuery) {
        lawyerListBox.getChildren().clear();

        String sql = "SELECT name, email, phone, photo FROM lawyers ";
        if (nameQuery != null && !nameQuery.trim().isEmpty()) {
            sql += "WHERE name LIKE ? ";
        }
        sql += "ORDER BY id LIMIT 20";

        try (Connection con = database.connectDB();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (nameQuery != null && !nameQuery.trim().isEmpty()) {
                ps.setString(1, "%" + nameQuery.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String photo = rs.getString("photo"); // example l1.jpg

                    // CARD UI
                    HBox card = new HBox(20);
                    card.getStyleClass().add("card");

                    ImageView iv = new ImageView();
                    iv.setFitHeight(100);
                    iv.setFitWidth(100);
                    iv.setPreserveRatio(true);

                    // load image from resources: /images/lawyers/<photo>
                    Image img = new Image(getClass().getResourceAsStream("/images/lawyers/" + photo));
                    iv.setImage(img);

                    VBox info = new VBox(6);
                    Label nameLb = new Label(name);
                    nameLb.getStyleClass().add("name");

                    Label emailLb = new Label(email);
                    emailLb.getStyleClass().add("sub");

                    Label phoneLb = new Label(phone);
                    phoneLb.getStyleClass().add("sub");

                    info.getChildren().addAll(nameLb, emailLb, phoneLb);

                    card.getChildren().addAll(iv, info);
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

    @FXML
    public void searchLawyer() {
        loadLawyers(searchField.getText());
    }

    @FXML
    public void showProfile() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("My Profile");
        a.setHeaderText(null);
        a.setContentText("Profile will be shown here later.");
        a.showAndWait();
    }
}
