import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class lawyerProfileUser {

    @FXML private ImageView lawyer_img;
    @FXML private Text lawyer_name;
    @FXML private Text bio;
    @FXML private Text fee;
    @FXML private Text specialization;
    @FXML private Text law_firm;
    @FXML private Text office_address;
    @FXML private Text bar_reg_no;
    @FXML private Text exp_yr;
    @FXML private Text mail;

   
    public void loadLawyerById(int lawyerId) {

        String sql =
            "SELECT name, email, specialization, law_firm, fee, photo, bio, bar_reg_no, exp_years, office_address " +
            "FROM lawyers WHERE id = ?";

        try (Connection con = database.connectDB();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, lawyerId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    showError("Lawyer not found (id=" + lawyerId + ")");
                    return;
                }

                
                lawyer_name.setText(safe(rs.getString("name")));
                mail.setText(safe(rs.getString("email")));
                specialization.setText(safe(rs.getString("specialization")));
                law_firm.setText(safe(rs.getString("law_firm")));
                office_address.setText(safe(rs.getString("office_address")));
                bar_reg_no.setText(safe(rs.getString("bar_reg_no")));
                exp_yr.setText(safe(rs.getString("exp_years")));
                bio.setText(safe(rs.getString("bio")));

                Object feeObj = rs.getObject("fee");
                fee.setText(feeObj == null ? "-" : String.valueOf(rs.getDouble("fee")));

                String photo = rs.getString("photo");
                lawyer_img.setImage(loadLawyerImage(photo));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load lawyer profile. Check terminal.");
        }
    }

    
    @FXML
    private void logout(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Logout");
        a.setHeaderText(null);
        a.setContentText("Logout clicked!");
        a.showAndWait();
    }

    @FXML
    private void godashboard(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Dashboard");
        a.setHeaderText(null);
        a.setContentText("Dashboard clicked!");
        a.showAndWait();
    }

   
    private Image loadLawyerImage(String photoFromDb) {

       
        if (photoFromDb == null || photoFromDb.trim().isEmpty()) {
            InputStream is = getClass().getResourceAsStream("/images/anonymous.png");
            return new Image(is);
        }

        // only filename
        String photo = photoFromDb.trim().replace("\\", "/");
        if (photo.contains("/")) {
            photo = photo.substring(photo.lastIndexOf("/") + 1);
        }

        // uploaded_photos (project root)
        File f = new File("uploaded_photos/" + photo);
        if (f.exists()) {
            return new Image(f.toURI().toString());
        }

        // fallback resources
        InputStream is2 = getClass().getResourceAsStream("/images/lawyers/" + photo);
        if (is2 != null) {
            return new Image(is2);
        }

        // final fallback
        InputStream is3 = getClass().getResourceAsStream("/images/anonymous.png");
        return new Image(is3);
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}