package episim.view.popup;

import de.saxsys.mvvmfx.FluentViewLoader;
import episim.App;
import episim.core.ModelConfig;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public abstract class ModelEditorViewPopup {
    public interface ValidateHandler {
        void handle(ModelConfig config);
    }
    private static Stage stage;
    private static ModelEditorViewModel viewModel;
    private static final EventHandler<Event> filter = ModelEditorViewPopup::eventFilter;
    private static final ObjectProperty<ValidateHandler> onValidate = new SimpleObjectProperty<>(null);

    public static void open(ModelConfig modelConfig) {
        if(stage == null) {
            var loader = FluentViewLoader.fxmlView(ModelEditorView.class).load();
            viewModel = loader.getViewModel();
            stage = new Stage();
            var scene = new Scene(loader.getView());

            App.applyTheme(scene);
            scene.getStylesheets().add("episim/css/modelEditor.css");

            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(scene);

            stage.setAlwaysOnTop(true);
            stage.resizableProperty().set(true);

            stage.setOnCloseRequest(e -> {
                close();
            });
        }

        viewModel.setEditedModel(modelConfig);

        App.getPrimaryStage().addEventFilter(EventType.ROOT, filter);
        stage.show();
        stage.toFront();
    }

    public static void validate(ModelConfig config) {
        if(stage != null) {
            App.getPrimaryStage().removeEventFilter(EventType.ROOT, filter);
            stage.close();

            if(onValidate.get() != null) {
                onValidate.get().handle(config);
            }
        }
    }

    public static void close() {
        validate(null);
    }

    public static ObjectProperty<ValidateHandler> onValidateProperty() {
        return onValidate;
    }

    private static void eventFilter(Event e) {
        if(e.getEventType() != WindowEvent.WINDOW_CLOSE_REQUEST) {
            e.consume();
        }
    }
}
