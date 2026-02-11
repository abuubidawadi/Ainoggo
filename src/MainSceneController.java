
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.Action;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private ImageView reg_page_img;

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

    public void switchbetweenloginandreg(ActionEvent event) {

        if(event.getSource() == login_button_reg_page) {
            login_page.setVisible(true);
            reg_page.setVisible(false);
        }
        else if(event.getSource() == reg_button_log_page) {
            login_page.setVisible(false);
            reg_page.setVisible(true);
        }
    }

}

