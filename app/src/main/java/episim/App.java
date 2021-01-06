package episim;

import episim.controller.HomeController;
import episim.controller.MainLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class App extends Application {
    public String getGreeting() {
        return "Hello World!";
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLayoutLoader = new FXMLLoader(getClass().getResource("/episim/view/MainLayout.fxml"));
        var scene = new Scene(mainLayoutLoader.load());
        MainLayoutController mainLayout = mainLayoutLoader.getController();

        // Apply metro theme
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.setTitle("Epidemic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/episim/view/Home.fxml"));
        mainLayout.setContent(homeLoader.load());

        HomeController home = homeLoader.getController();
        home.initialize();
    }

    public static void main(String[] args) {
        // var frame = new MainFrame();
        launch(args);
        System.out.println(new App().getGreeting());
    }
}
