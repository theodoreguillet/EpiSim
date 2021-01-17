package episim.view.component;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Composant représentant une règle sanitaire avec un pourcentage de respect et un délais d'application
 */
public class EpidemicRule implements Initializable {
    @FXML
    private Text respectText;
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

    /**
     * La propriété du pourcentage de respect de la règle sanitaire
     * @return La propriété du pourcentage de respect
     */
    public DoubleProperty respectProperty() {
        return respectController.valueProperty();
    }

    /**
     * La propriété du délais d'application de la règle sanitaire
     * @return La propriété du délais d'application
     */
    public DoubleProperty delayProperty() {
        return delayController.valueProperty();
    }

    /**
     * Définie le texte présentant le respect de la règle sanitaire
     * @param text Le texte présentant le respect de la règle sanitaire
     */
    public void setRespectText(String text) {
        respectText.setText(text);
    }

    /**
     * Définie les bornes du pourcentage de respect de la règle sanitaire
     * @param min La valeur minimum
     * @param max La valeur maximum
     * @param step L'écart entre deux valeurs consécutives
     */
    public void setRespectRange(double min, double max, double step) {
        respectController.setMin(min);
        respectController.setMax(max);
        respectController.setStep(step);
    }
}
