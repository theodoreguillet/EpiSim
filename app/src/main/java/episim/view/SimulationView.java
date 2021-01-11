package episim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulationView implements FxmlView<SimulationViewModel>, Initializable {
    @InjectViewModel
    private SimulationViewModel viewModel;

    @FXML
    private HBox simulationChartHBox;
    @FXML
    private Pane simulationChartContainer;
    @FXML
    private LineChart<Double, Double> simulationChart;
    @FXML
    private Pane simulationCanvasContainer;
    @FXML
    private Canvas simulationCanvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationCanvas.heightProperty().bind(simulationCanvasContainer.heightProperty());
        simulationCanvas.widthProperty().bind(simulationCanvasContainer.widthProperty());
        simulationCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawSimulation();
        });
        simulationCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawSimulation();
        });

        simulationChart.setMinHeight(100);
        simulationChart.setMinWidth(100);
        simulationChart.heightProperty().addListener((observable, oldValue, newValue) -> {
            simulationChart.setPrefWidth(3 * newValue.doubleValue());
        });
        simulationChart.prefHeightProperty().bind(simulationChartContainer.heightProperty());
        simulationChart.maxWidthProperty().bind(simulationChartHBox.widthProperty());

        drawSimulation();
    }

    private void drawSimulation() {
        double height = simulationCanvas.getHeight();
        double width = simulationCanvas.getWidth();
        var ctx = simulationCanvas.getGraphicsContext2D();

        ctx.clearRect(0, 0, width, height);
        ctx.fillRect(0, 0, width, height);
    }
}
