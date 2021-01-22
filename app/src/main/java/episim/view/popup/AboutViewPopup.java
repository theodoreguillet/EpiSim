package episim.view.popup;

import de.saxsys.mvvmfx.FluentViewLoader;
import episim.App;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Popup de la vue {@link AboutView A propos}
 */
public abstract class AboutViewPopup {
    private static Stage stage;

    public static void open() {
        if(stage == null) {
            var loader = FluentViewLoader.fxmlView(AboutView.class).load();
            stage = new Stage();
            var scene = new Scene(loader.getView());

            App.applyTheme(scene);

            stage.setScene(scene);
            stage.setResizable(false);
        }
        stage.show();
    }

    public static void close() {
        if(stage != null) {
            stage.close();
        }
    }
}
