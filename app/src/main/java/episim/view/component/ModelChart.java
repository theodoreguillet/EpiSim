package episim.view.component;


import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.InjectViewModel;
import episim.core.ModelConfig;
import episim.view.ConfigurationScope;
import episim.view.HomeViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.util.*;

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

    private ObservableList<Chart> data = FXCollections.observableArrayList();

    private List<XYChart.Series<Double, Double>> params = new ArrayList<>();


    //trace les 3 courbes à partir des paramètres définis par l'utilisateur
    private void drawFunction(){
        modelChart.getData().clear();

        //Pour chaque Chart
        for(int i=0; i<data.size(); i++){

            XYChart.Series<Double, Double> tempSeries = new XYChart.Series<Double, Double>();
            //Pour chaque point de la Chart
            for(int j=0; j<data.get(i).points.size();j++){
                //on l'ajoute à notre XYSeries temporaire
                tempSeries.getData().add(data.get(i).points.get(j));
            }
            //Puis on ajoute cette série temporaire à notre liste de XYSeries
            params.add(tempSeries);
        }

        //Puis on ajoute tous ces series à la courbe
        for(int k=0; k<params.size(); k++){
            modelChart.getData().add(params.get(k));
        }

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
