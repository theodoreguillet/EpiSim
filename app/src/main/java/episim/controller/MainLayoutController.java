package episim.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainLayoutController {
    @FXML
    BorderPane borderPane;

    public void setContent(Node content) {
        borderPane.setCenter(content);
    }
}
