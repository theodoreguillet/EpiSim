package episim.view;

import de.saxsys.mvvmfx.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Vue principale, contient les autres vues
 */
public class MainView implements FxmlView<MainViewModel>, Initializable {
    @InjectViewModel
    private MainViewModel viewModel;

    @InjectContext
    private Context mainContext;

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.subscribe(MainViewModel.ACTION_SET_CONTENT, (k, v) -> {
            if(v.length > 0 && v[0] instanceof String) {
                String type = (String)v[0];
                setContent(type);
            }
        });
        setContent(MainViewModel.HOME);
    }

    private void setContent(String type) {
        Class<? extends FxmlView<? extends ViewModel>> contentViewClass = null;
        switch(type) {
            case MainViewModel.HOME:
                contentViewClass = HomeView.class;
                break;
            case MainViewModel.SIMULATION:
                contentViewClass = SimulationView.class;
                break;
        }
        if(contentViewClass != null) {
            Parent content = FluentViewLoader.fxmlView(contentViewClass).context(mainContext).load().getView();
            borderPane.setCenter(content);
        }
    }
}
