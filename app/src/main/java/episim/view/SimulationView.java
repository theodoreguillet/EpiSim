package episim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulationView implements FxmlView<SimulationViewModel>, Initializable {
    @InjectViewModel
    private SimulationViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
