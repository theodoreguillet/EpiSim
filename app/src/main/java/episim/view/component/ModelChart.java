package episim.view.component;


import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.InjectViewModel;
import episim.core.ModelConfig;
import episim.view.ConfigurationScope;
import episim.view.HomeViewModel;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Composant représentant la courbe du menu principal tracée à partir des paramètres initiaux
 */
public class ModelChart implements Initializable {

    public static class Chart{
        private ObservableObjectValue<Color> color;
        private ObservableList<XYChart.Data<Double, Double>> points;

        public Color getColor() {
            return color.get();
        }

        public ObservableObjectValue<Color> colorProperty() {
            return color;
        }

        public ObservableList<XYChart.Data<Double, Double>> pointsProperty() {
            return points;
        }

        public void setPoints(ObservableList<XYChart.Data<Double, Double>> points) {
            this.points = points;
        }
    }

    @FXML
    private LineChart<Double, Double> modelChart;

    private ObservableList<Chart> data;

    private XYChart.Series<Double, Double> S = new XYChart.Series<Double, Double>();
    private XYChart.Series<Double, Double> I = new XYChart.Series<Double, Double>();
    private XYChart.Series<Double, Double> R = new XYChart.Series<Double, Double>();

    //trace les 3 courbes S,I,R à partir des paramètres définis par l'utilisateur
    private void drawFunction(){
        modelChart.getData().clear();

        int i=0;
        for(var chart: data){
            if(i==0){
                for(var points: chart.points){
                    S.getData().add(points);
                }
            }
            if(i==1){
                for(var points: chart.points){
                    I.getData().add(points);
                }
            }
            if(i==2){
                for(var points: chart.points){
                    R.getData().add(points);
                }
            }
            i++;
        }

        S.setName("Saines");
        I.setName("Infectees");
        R.setName("Retablies");

        modelChart.getData().addAll(S,I,R);
    }

    public void setData(ObservableList<Chart> data){
        this.data = data;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        data.addListener((ListChangeListener.Change<? extends Chart> c) -> {

            for(var chart: c.getAddedSubList()){
                chart.colorProperty().addListener((observable1, oldValue1, newValue1) -> {
                    this.drawFunction();
                });
                chart.pointsProperty().addListener((ListChangeListener.Change<? extends XYChart.Data<Double, Double>> c1) -> {
                    this.drawFunction();
                });
            }
            this.drawFunction();
        });

        this.drawFunction();
    }

    public ObservableList<Chart> getDataProperty(){
        return this.data;
    }

}
