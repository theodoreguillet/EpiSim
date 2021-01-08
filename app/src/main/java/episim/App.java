package episim;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import episim.core.SimulationConfig;
import episim.view.MainView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.List;

public class App extends MvvmfxGuiceApplication {
    public String getGreeting() {
        return "Hello World!";
    }

    @Override
    public void startMvvmfx(Stage primaryStage) throws Exception {
        Parent parent = FluentViewLoader.fxmlView(MainView.class).load().getView();
        var scene = new Scene(parent);

        // Apply metro theme
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.setTitle("Epidemic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
