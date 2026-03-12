
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.InputStream;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import javafx.scene.Node;


public class LawyerDashboardController {

    @FXML
    private ImageView lawyerImageView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label specializationLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private Label barIdLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private VBox upcomingContainer;

    @FXML
    private VBox previousContainer;

    private String lawyerUsername;

    @FXML
    public void initialize() {
        // ইচ্ছা করে empty রাখা হয়েছে
        // কারণ setLawyerUsername() call হওয়ার পর load হবে
    }

    public void setLawyerUsername(String lawyerUsername) {
        this.lawyerUsername = lawyerUsername;
        loadLawyerProfile();
        loadAppointments();
    }

    private void loadLawyerProfile() {
        String sql = "SELECT name, email, specialization, exp_years, bar_reg_no, office_address, photo "
                   + "FROM lawyers WHERE username = ?";

        try (Connection con = database.connectDB();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lawyerUsername);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nameLabel.setText("Name: " + safe(rs.getString("name")));
                    emailLabel.setText("Email: " + safe(rs.getString("email")));
                    phoneLabel.setText("Phone: -");
                    specializationLabel.setText("Specialization: " + safe(rs.getString("specialization")));
                    experienceLabel.setText("Experience: " + safe(rs.getString("exp_years")) + " Years");
                    barIdLabel.setText("Bar ID: " + safe(rs.getString("bar_reg_no")));
                    addressLabel.setText("Address: " + safe(rs.getString("office_address")));

                    String photo = rs.getString("photo");
                    lawyerImageView.setImage(loadLawyerImage(photo));
                } else {
                    showError("Lawyer profile not found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load lawyer profile.");
        }
    }

@FXML
private void logout(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lawyerlogreg.fxml"));
        Parent root = loader.load();

        LawyerEnterSceneController controller = loader.getController();
        controller.showLoginPage();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 650));
        stage.setTitle("Ainoggo");
        stage.setResizable(false);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        showError("Could not logout.");
    }
}
    private void loadAppointments() {
    upcomingContainer.getChildren().clear();
    previousContainer.getChildren().clear();

    String sql = "SELECT a.id, a.user_username, a.date, a.time, a.status "
               + "FROM appointments a "
               + "JOIN lawyers l ON l.id = a.lawyer_id "
               + "WHERE l.username = ? "
               + "ORDER BY a.date ASC, a.time ASC";

    try (Connection con = database.connectDB();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, lawyerUsername);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int appointmentId = rs.getInt("id");
                String client = rs.getString("user_username");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String status = rs.getString("status");

                boolean isPast = isAppointmentPast(date, time);

                if (isPast) {
                    previousContainer.getChildren().add(0,
                        createPreviousCard(client, date, time, status)
                    );
                } else {
                    upcomingContainer.getChildren().add(
                        createUpcomingCard(appointmentId, client, date, time, status)
                    );
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        showError("Could not load appointments.");
    }
}
private boolean isAppointmentPast(String dateStr, String timeStr) {
    try {
        LocalDate appointmentDate = LocalDate.parse(dateStr);

        LocalTime appointmentTime = parseTime(timeStr);

        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        LocalDateTime now = LocalDateTime.now();

        return appointmentDateTime.isBefore(now);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
private LocalTime parseTime(String timeStr) {
    if (timeStr == null || timeStr.trim().isEmpty()) {
        return LocalTime.MIDNIGHT;
    }

    timeStr = timeStr.trim();

    try {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm"));
    } catch (Exception e1) {
        try {
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e2) {
            try {
                return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"));
            } catch (Exception e3) {
                try {
                    return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("hh:mm a"));
                } catch (Exception e4) {
                    return LocalTime.MIDNIGHT;
                }
            }
        }
    }
}
private VBox createUpcomingCard(int appointmentId, String client, String date, String time, String status) {
    VBox card = new VBox(8);
    card.setStyle(
        "-fx-background-color: #f9f9f9;" +
        "-fx-border-color: #dcdcdc;" +
        "-fx-border-radius: 10;" +
        "-fx-background-radius: 10;" +
        "-fx-padding: 15;"
    );

    Label clientLabel = new Label("Client: " + client);
    Label dateLabel = new Label("Date: " + date);
    Label timeLabel = new Label("Time: " + time);
    Label statusLabel = new Label("Status: " + status);

    clientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    dateLabel.setStyle("-fx-font-size: 14px;");
    timeLabel.setStyle("-fx-font-size: 14px;");
    statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

    card.getChildren().addAll(clientLabel, dateLabel, timeLabel, statusLabel);

    if ("Pending".equalsIgnoreCase(status)) {
        Button acceptBtn = new Button("Accept");
        Button rejectBtn = new Button("Reject");

        acceptBtn.setStyle("-fx-background-color: #111; -fx-text-fill: white; -fx-font-size: 14px;");
        rejectBtn.setStyle("-fx-background-color: #b00020; -fx-text-fill: white; -fx-font-size: 14px;");

        acceptBtn.setOnAction(e -> {
            updateAppointmentStatus(appointmentId, "Accepted");
            loadAppointments();
        });

        rejectBtn.setOnAction(e -> {
            updateAppointmentStatus(appointmentId, "Rejected");
            loadAppointments();
        });

        HBox buttonBox = new HBox(10, acceptBtn, rejectBtn);
        card.getChildren().add(buttonBox);
    }

    return card;
}
  

    private VBox createPreviousCard(String client, String date, String time, String status) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: #f4f4f4;" +
            "-fx-border-color: #dcdcdc;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;"
        );

        Label clientLabel = new Label("Client: " + client);
        Label dateLabel = new Label("Date: " + date);
        Label timeLabel = new Label("Time: " + time);
        Label statusLabel = new Label("Status: " + status);

        clientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        dateLabel.setStyle("-fx-font-size: 14px;");
        timeLabel.setStyle("-fx-font-size: 14px;");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        card.getChildren().addAll(clientLabel, dateLabel, timeLabel, statusLabel);

        return card;
    }

    private void updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (Connection con = database.connectDB();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not update appointment status.");
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

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}