package episim.view.component;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class EpidemicRule implements Initializable {
    @FXML
    private SpinnerSlider respectController;
    @FXML
    private SpinnerSlider delayController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        respectController.setMin(0);
        respectController.setMax(100);
        respectController.setStep(1);
        delayController.setMin(0);
        delayController.setMax(100);
        delayController.setStep(1);
    }

    public DoubleProperty respectProperty() {
        return respectController.valueProperty();
    }

    public DoubleProperty delayProperty() {
        return delayController.valueProperty();
    }
}
