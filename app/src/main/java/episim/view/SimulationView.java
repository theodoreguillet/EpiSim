package episim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import episim.util.StringDoubleConverter;
import episim.view.component.ModelChart;
import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import jfxtras.styles.jmetro.MDL2IconFont;

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
    private LineChart<Number, Number> simulationChart;
    @FXML
    private ModelChart simulationChartController;
    @FXML
    private VBox simulationPopContainer;
    @FXML
    private Pane simulationCanvasContainer;
    @FXML
    private Canvas simulationCanvas;
    @FXML
    private Slider simulationSpeed;
    @FXML
    private Text simulationSpeedText;
    @FXML
    private Button pauseResumeButton;
    @FXML
    private Button saveButton;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            draw();
        }
    };

    @FXML
    public void stopSimulation() {
        timer.stop();
        viewModel.stopSimulation();
    }

    @FXML
    public void togglePauseResume() {
        viewModel.simulationPaused().set(!viewModel.simulationPaused().get());
    }

    @FXML void saveSimulation() {
        viewModel.saveSimulation();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.subscribe(SimulationViewModel.STOP_ANNIMATION, (key, payload) -> {
            timer.stop();
        });

        simulationCanvas.heightProperty().bind(simulationCanvasContainer.heightProperty());
        simulationCanvas.widthProperty().bind(simulationCanvasContainer.widthProperty());
        simulationCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawSimulation();
        });
        simulationCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawSimulation();
        });

        simulationChart.heightProperty().addListener((observable, oldValue, newValue) -> {
            simulationChart.setPrefWidth(4 * newValue.doubleValue());
        });
        simulationChart.prefHeightProperty().bind(simulationChartContainer.heightProperty());
        simulationChart.maxWidthProperty().bind(simulationChartHBox.widthProperty());

        simulationChart.setMinHeight(100);
        simulationChart.setMinWidth(100);
        simulationChart.setPrefWidth(4 * simulationChart.prefHeightProperty().get());

        simulationSpeed.valueProperty().bindBidirectional(viewModel.simulationSpeed());
        simulationSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            simulationSpeedText.setText(Integer.toString(newValue.intValue()));
        });
        simulationSpeedText.setText(Integer.toString((int)simulationSpeed.getValue()));

        viewModel.simulationPaused().addListener((observable, oldValue, newValue) -> {
            handlePausedChanged();
        });
        handlePausedChanged();

        viewModel.simulationChart().addListener((ListChangeListener.Change<? extends ModelChart.Chart> c) -> {
            simulationChartController.dataProperty().setAll(viewModel.simulationChart());
            updateSimlationPop();
        });
        simulationChartController.dataProperty().setAll(viewModel.simulationChart());

        saveButton.setGraphic(new MDL2IconFont("\uE74E"));

        draw();
        timer.start();
    }

    private void handlePausedChanged() {
        if(viewModel.simulationPaused().get()) {
            pauseResumeButton.setGraphic(new MDL2IconFont("\uEDB5")); // play icon
        } else {
            pauseResumeButton.setGraphic(new MDL2IconFont("\uEDB4")); // pause icon
        }
    }

    private void draw() {
        viewModel.updateSimulation();
        drawSimulation();
    }

    private void drawSimulation() {
        var worldBounds = viewModel.getWorldBounds();
        var zones = viewModel.getZones();
        var points = viewModel.getPoints();
        double zoomRatio = 1;

        double height = simulationCanvas.getHeight();
        double width = simulationCanvas.getWidth();
        var ctx = simulationCanvas.getGraphicsContext2D();

        ctx.setTransform(new Affine());
        ctx.clearRect(0, 0, width, height);

        double worldMargin = 10;

        double scaleRatio = Math.min(
                width / (worldBounds.getWidth() + 2 * worldMargin),
                height / (worldBounds.getHeight() + 2 * worldMargin)
        );
        double scale = scaleRatio * zoomRatio;

        ctx.translate((width - worldBounds.getWidth() * scale) / 2, (height - worldBounds.getHeight() * scale) / 2);
        ctx.scale(scale, scale);

        // Draw points
        for(var point : points) {
            fillCircle(ctx, point.pos(), 2, point.color());
            if(point.emitRadius() > 0) {
                // TODO emit animation
                strokeCircle(ctx, point.pos(), point.emitRadius() , 0.2, point.color());
            }
        }

        // Draw zones
        for(var zone : zones) {
            strokeRectBorder(ctx, zone.bounds(), 1, zone.color());
            for(var rect : zone.rects()) {
                strokeRectBorder(ctx,
                        new Rectangle2D(
                                zone.bounds().getMinX() + rect.getMinX(),
                                zone.bounds().getMinY() + rect.getMinY(),
                                rect.getWidth(),
                                rect.getHeight()
                        ),
                        0.5, Color.BLUE
                );
            }
        }
    }

    private void strokeRectBorder(GraphicsContext ctx, Rectangle2D rect, double lw, Color color) {
        strokeRect(
                ctx,
                new Rectangle2D(rect.getMinX() - lw, rect.getMinY() - lw,
                        rect.getWidth() + 2 * lw, rect.getHeight() + 2 * lw),
                lw,
                color
        );
    }

    private void strokeRect(GraphicsContext ctx, Rectangle2D rect, double lw, Color color) {
        ctx.setLineWidth(lw);
        ctx.setStroke(color);
        ctx.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    private void strokeCircle(GraphicsContext ctx, Point2D center, double radius, double lw, Color color) {
        ctx.setLineWidth(lw);
        ctx.setStroke(color);
        ctx.strokeOval(center.getX() - radius/2, center.getY() - radius/2, radius, radius);
    }

    private void fillCircle(GraphicsContext ctx, Point2D center, double radius, Color color) {
        ctx.setFill(color);
        ctx.fillOval(center.getX() - radius/2, center.getY() - radius/2, radius, radius);
    }

    private void updateSimlationPop() {
        simulationPopContainer.getChildren().clear();
        for(var chart : viewModel.simulationChart()) {
            String value = new StringDoubleConverter().toString(chart.getYdata().get(chart.getYdata().size() - 1));
            Text text = new Text(chart.getName() + "\t" + value);
            String rgbColor = String.format("%d, %d, %d",
                    (int) (chart.getColor().getRed() * 255),
                    (int) (chart.getColor().getGreen() * 255),
                    (int) (chart.getColor().getBlue() * 255)
            );
            text.setStyle(text.getStyle() + "-fx-fill: rgba(" + rgbColor + ", 1);");
            simulationPopContainer.getChildren().add(text);
        }
    }
}
