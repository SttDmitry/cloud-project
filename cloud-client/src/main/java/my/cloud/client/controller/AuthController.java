package my.cloud.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    private Label error;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;

    private MainController controller;

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void tryToAuth() {
        if (loginField.getText() == null || passField.getText() == null) {
            passField.clear();
            error.setVisible(true);
        } else {
            error.setVisible(false);
            controller.tryToAuth(loginField.getText(), passField.getText());
        }
    }
}
