package episim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HomeController {
    @FXML
    TextField testTextField;

    @FXML
    Label testLabel;

    @FXML
    private void printTestText(ActionEvent event) {
        event.consume(); // Stop event propagation
        System.out.println(testTextField.getText());
    }

    @FXML
    private void initialize() {
        testLabel.textProperty().bind(testTextField.textProperty());
    }
}
