package episim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class HomeController {

    @FXML
    ToggleButton sirToggleButton;

    @FXML
    ToggleButton seirToggleButton;

    @FXML
    ToggleButton seirEvolutiveToggleButton;

    @FXML
    ToggleButton visualisationToggleButton;

    @FXML
    Button startButton;

    @FXML
    Label betaTransmissionLabel;

    @FXML
    Slider betaTransmissionSlider;

    @FXML
    Label gammaRecoveryLabel;

    @FXML
    Slider gammaRecoverySlider;

    @FXML
    Slider infectedSlider;

    @FXML
    Label infectedLabel;

    @FXML
    Label alphaExposedLabel;

    @FXML
    Slider alphaExposedSlider;

    @FXML
    Label etaBirthLabel;

    @FXML
    Slider etaBirthSlider;

    @FXML
    Label muDeathLabel;

    @FXML
    Slider muDeathSlider;

    @FXML
    CheckBox maskWearingCheckBox;

    @FXML
    CheckBox confinementCheckBox;

    @FXML
    CheckBox quarantineCheckBox;

    @FXML
    CheckBox vaccinationCheckBox;

    @FXML
    Label selectionMsgLabel;

    @FXML
    public void startSimulation(ActionEvent event) {
        event.consume(); // Stop event propagation
    }

    @FXML
    public void initialize() {
        ToggleGroup modelGroup = new ToggleGroup();

        startButton.setStyle("-fx-background-color: linear-gradient(#7CD437, #6AB72E);");

        modelGroup.getToggles().addAll(sirToggleButton, seirToggleButton, seirEvolutiveToggleButton);

        modelGroup.selectedToggleProperty().addListener(
                new ChangeListener<Toggle>() {
                    public void changed(ObservableValue<? extends Toggle> ov, final Toggle toggle, final Toggle new_toggle) {
                        String toggleBtn = ((ToggleButton)new_toggle).getText();
                        //style du bouton du toggle group selectionné
                        ((ToggleButton)new_toggle).setStyle("-fx-border-color: yellow;" +
                                                            "-fx-border-width: 1em;" +
                                                            "-fx-background-color: gray;" +
                                                            "-fx-color: black;");

                        //conditions pour faire apparaitre les différents sliders
                        if(toggleBtn == seirEvolutiveToggleButton.getText()){
                            alphaExposedLabel.setVisible(true);
                            alphaExposedSlider.setVisible(true);

                            etaBirthLabel.setVisible(true);
                            etaBirthSlider.setVisible(true);

                            muDeathLabel.setVisible(true);
                            muDeathSlider.setVisible(true);
                        }

                        if(toggleBtn == seirToggleButton.getText()){
                            alphaExposedLabel.setVisible(true);
                            alphaExposedSlider.setVisible(true);

                            etaBirthLabel.setVisible(false);
                            etaBirthSlider.setVisible(false);

                            muDeathLabel.setVisible(false);
                            muDeathSlider.setVisible(false);
                        }

                        if(toggleBtn == sirToggleButton.getText()){
                            alphaExposedLabel.setVisible(false);
                            alphaExposedSlider.setVisible(false);

                            etaBirthLabel.setVisible(false);
                            etaBirthSlider.setVisible(false);

                            muDeathLabel.setVisible(false);
                            muDeathSlider.setVisible(false);
                        }

                        //Pour chaque bouton du ToggleGroup, si ce n'est pas celui selectionne, on reset le style
                        modelGroup.getToggles().stream().map((toggleb) -> (ToggleButton)toggleb).forEach((button) -> {
                            if(button.getText() != toggleBtn){
                                button.setStyle(null);
                            }
                        });

                        selectionMsgLabel.setText("Your selection: " + toggleBtn);
                    }
                }
        );


        sirToggleButton.setSelected(true);

    }
}
