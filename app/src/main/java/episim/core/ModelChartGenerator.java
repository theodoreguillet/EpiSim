package episim.core;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public abstract class ModelChartGenerator{

    public static class CompData{
        public ArrayList<Double> x;
        public ArrayList<Double> y;
        public Color color;

        public void CompData(){

        }
    }

    //genere les données
    public static ArrayList<CompData> generate(ModelConfig modelconfig, double infc, double duree, double dt){

        ArrayList<CompData> compData = new ArrayList<>(modelconfig.getCompartments().size()+1);

        //on récupère les compartiments
        List<CompartmentConfig> compartments = modelconfig.getCompartments();

        //on initialise les paramètres
        double eta = modelconfig.getBirth();
        double mu = modelconfig.getDeath();

        double beta = 0;
        double alpha = 0;
        double gamma = 0;

        CompData S = new CompData();
        CompData E = new CompData();
        CompData I = new CompData();
        CompData R = new CompData();

        if(modelconfig.getCompartments().size() == 2){
            beta = compartments.get(0).getParam();
            gamma = compartments.get(1).getParam();
        }

        if(modelconfig.getCompartments().size() == 3){
            beta = compartments.get(0).getParam();
            alpha = compartments.get(1).getParam();
            gamma = compartments.get(2).getParam();
        }

        S.y.set(0, 1 - infc/100);
        E.y.set(0, 0.0); // aucun exposé initialement
        I.y.set(0, infc/100); // pourcentage d'infectés initialement
        R.y.set(0, 0.0); // aucun rétabli initialement

        ArrayList<Double> x = new ArrayList<Double>();
        x.set(0, 0.0);

        //algorithme

        for(int i=1; i<duree; i++){
            x.set(i, (double) i);
            S.y.set(i, dt*(-beta*S.y.get(i-1)*I.y.get(i-1) + eta - mu*S.y.get(i-1)));

            if(modelconfig.getCompartments().size() == 3){
                E.y.set(i, dt*(beta*S.y.get(i-1)*I.y.get(i-1) - alpha*E.y.get(i-1) - mu*E.y.get(i-1)));
            }

            I.y.set(i, alpha*E.y.get(i-1) - I.y.get(i-1)*(gamma + mu));
            R.y.set(i, gamma*I.y.get(i-1) - mu*R.y.get(i-1));
        }

        S.x = x;
        E.x = x;
        I.x = x;
        R.x = x;

        compData.set(0, S);
        compData.set(1, I);
        compData.set(2, R);

        //Liste de compData
        return compData;
    }

}
