package episim.core;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class ModelChartGenerator{

    public static class CompData{
        private final List<Double> x;
        private final List<Double> y;

        CompData(List<Double> x, List<Double> y){
            this.x = x;
            this.y = y;
        }

        public List<Double> getX() {
            return Collections.unmodifiableList(x);
        }

        public List<Double> getY() {
            return Collections.unmodifiableList(y);
        }
    }

    //genere les données
    public static ArrayList<CompData> generate(ModelConfig modelconfig, double infc, int duree, int dt){

        ArrayList<CompData> compData = new ArrayList<>(modelconfig.getCompartments().size());

        //on récupère les compartiments
        List<CompartmentConfig> compartments = modelconfig.getCompartments();

        //on initialise les paramètres
        //facteur de naissance
        double eta = modelconfig.getBirth();

        //facteur de décès
        double mu = modelconfig.getDeath();

        for(int i=0;i<compartments.size();i++){
            List<Double> x = new ArrayList<>(duree*dt);
            List<Double> y = new ArrayList<>(duree*dt);

            for(int j=0;j<duree*dt;j++){
                // si on est dans le premier cas
                if(j == 0){
                    if(compartments.get(j).getName() == "S"){
                        x.add(1 - infc/100);
                    }
                    if(compartments.get(j).getName() == "I"){
                        x.add(infc/100);
                    }
                    else{
                        x.add(0.0);
                    }
                    y.add(0.0);
                }
                else{
                    x.add(0.0);
                    y.add(y.get(j-1) + dt);
                }
            }

            compData.add(new CompData(x, y));
        }

        //simulation
        for(int t=1; t<duree*dt; t++){

            for(int i=0; i<compartments.size(); i++){

                if(i==0){
                    compData.get(i).x.set(t, - compartments.get(i).getParam()*compData.get(i).x.get(t-1)  + eta*compData.get(i).x.get(t-1) - mu*compData.get(i).x.get(t-1));
                }
                else if(i==compartments.size()-1){
                    compData.get(i).x.set(t, compartments.get(i-1).getParam()*compData.get(i).x.get(t-1)  - compartments.get(i).getParam()*compData.get(i).x.get(t-1) - mu*compData.get(i).x.get(t-1));
                }
                else{
                    compData.get(i).x.set(t, compartments.get(i-1).getParam()*compData.get(i).x.get(t-1) - mu*compData.get(i).x.get(t-1));
                }
            }
        }

        //Liste de compData
        return compData;
    }

}
