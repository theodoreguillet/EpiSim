package episim.view.component;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import java.net.URL;
import java.util.*;

/**
 * Composant représentant la courbe du menu principal tracée à partir des paramètres initiaux
 */
public class ModelChart implements Initializable {
    public static class Chart {
        private final List<Double> xdata;
        private final List<Double> ydata;
        private final String name;
        private final Color color;

        public Chart(List<Double> xdata, List<Double> ydata, String name, Color color) {
            this.xdata = xdata;
            this.ydata = ydata;
            this.name = name;
            this.color = color;
        }
    }

    @FXML
    private LineChart<Number, Number> modelChart;

    private final ObservableList<Chart> data = FXCollections.observableArrayList();


    //trace les 3 courbes à partir des paramètres définis par l'utilisateur
    private void drawFunction(){
        modelChart.getData().clear();

        double maxx = 0;
        double maxy = 0;
        for(var chart : data) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();

            int size = Math.min(chart.xdata.size(), chart.ydata.size());
            for(int i = 0; i < size; i++) {
                maxy = Math.max(maxy, chart.ydata.get(i));
                maxx = Math.max(maxx, chart.xdata.get(i));
                series.getData().add(new XYChart.Data<>(chart.xdata.get(i), chart.ydata.get(i)));
            }

            String rgbColor = String.format("%d, %d, %d",
                    (int) (chart.color.getRed() * 255),
                    (int) (chart.color.getGreen() * 255),
                    (int) (chart.color.getBlue() * 255)
            );

            modelChart.getData().add(series);

            Node node = series.getNode();
            node.setStyle(node.getStyle() + "-fx-stroke: rgba(" + rgbColor + ", 0.6);");
            // node.setStyle(node.getStyle() + "-fx-fill: rgba(" + rgbColor + ", 0.15);");
        }

        var yaxis = (NumberAxis)modelChart.getYAxis();
        yaxis.setUpperBound(Math.ceil(maxy));
        var xaxis = (NumberAxis)modelChart.getXAxis();
        xaxis.setUpperBound(Math.ceil(maxx));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        data.addListener((ListChangeListener.Change<? extends Chart> c) -> {
            this.drawFunction();
        });
        this.drawFunction();
    }

    public ObservableList<Chart> dataProperty(){
        return data;
    }

}
