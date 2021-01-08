package episim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeView implements FxmlView<HomeViewModel>, Initializable {
    @InjectViewModel
    private HomeViewModel viewModel;

    @FXML
    public void onStartSimulationAction() {
        viewModel.startSimulation();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
