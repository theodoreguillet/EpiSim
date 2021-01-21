package episim.view;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import episim.core.*;
import episim.view.component.ModelChart;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimulationViewModel implements ViewModel {
    public static class Point {
        private final Point2D pos;
        private final Color color;
        private final double emitRadius;

        public Point(Point2D pos, Color color, double emitRadius) {
            this.pos = pos;
            this.color = color;
            this.emitRadius = emitRadius;
        }
        public Point2D pos() { return pos; }
        public Color color() { return color; }
        public double emitRadius() { return emitRadius; }
    }
    public static class Zone {
        private final Rectangle2D bounds;
        private final Color color;
        private final ArrayList<Rectangle2D> rects;

        public Zone(Rectangle2D bounds, Color color, ArrayList<Rectangle2D> rects) {
            this.bounds = bounds;
            this.color = color;
            this.rects = rects;
        }
        public Rectangle2D bounds() { return bounds; }
        public Color color() { return color; }
        public ArrayList<Rectangle2D> rects() { return rects; }
    }

    public static final String STOP_ANNIMATION = "STOP_ANNIMATION";
    public static final String SAVE = "SAVE";

    @InjectScope
    private MainScope mainScope;

    private Simulation simulation;

    private ArrayList<Point> points;
    private ArrayList<Zone> zones;
    private boolean quarantineEnabled;
    private Rectangle2D worldBounds;

    private final DoubleProperty simulationSpeed = new SimpleDoubleProperty(1);
    private final BooleanProperty simulationPaused = new SimpleBooleanProperty(false);

    private final ObservableList<ModelChart.Chart> simulationChart = FXCollections.observableArrayList();

    public void initialize() {
        simulation = new Simulation(mainScope.getSimulationConfig());

        points = new ArrayList<>();
        zones = new ArrayList<>();
        quarantineEnabled = true;
        worldBounds = Rectangle2D.EMPTY;

        simulationSpeed.addListener((observable, oldValue, newValue) -> {
            simulation.setSpeed(newValue.doubleValue());
        });
        simulationPaused.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                simulation.pause();
            } else {
                simulation.resume();
            }
        });
        mainScope.simulationPaused().bindBidirectional(simulationPaused);

        mainScope.subscribe(MainScope.STOP_SIMULATION, (key, payload) -> {
            publish(STOP_ANNIMATION);
            stopSimulation();
        });
        mainScope.subscribe(MainScope.EXPORT_SIMULATION, (key, payload) -> {
            publish(SAVE);
        });

        simulation.start();
        var state = simulation.getState();
        createSimulationWorld(state);
        updateSimulationChart(state);

        simulationSpeed.set(1);
    }

    public void stopSimulation() {
        simulation.stop();
        mainScope.publish(MainScope.MAIN_CONFIG);
    }

    public void saveSimulation(File file) {
        var state = simulation.getState();
        state.stats.save(file);
    }

    public void updateSimulation() {
        var state = simulation.getState();

        updateSimulationPoints(state);

        if(simulationChart.isEmpty() ||
                state.stats.points.size() != simulationChart.get(0).getXdata().size()
        ) {
            updateSimulationChart(state);
        }
    }

    public DoubleProperty simulationSpeed() {
        return simulationSpeed;
    }

    public BooleanProperty simulationPaused() {
        return simulationPaused;
    }

    public ObservableList<ModelChart.Chart> simulationChart() {
        return simulationChart;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    public SimulationState getSimulationState() {
        return simulation.getState();
    }

    public Rectangle2D getZoneSize() {
        return new Rectangle2D(0, 0, Simulation.ZONE_SIZE, Simulation.ZONE_SIZE);
    }

    private void createSimulationWorld(SimulationState state) {
        int nzones = state.zones.size();
        double quarantineSize = Simulation.QUARANTINE_SIZE;
        double zoneSize = Simulation.ZONE_SIZE;

        double quarantineGap = 15;
        double gridGap = 10;
        double gridX = quarantineEnabled ? quarantineSize + quarantineGap : 0;
        double gridY = 0;
        double gridWidth = Math.ceil(Math.sqrt(nzones));
        double gridHeight = Math.ceil(nzones / gridWidth);

        zones = new ArrayList<>(nzones);

        for(int i = 0; i < nzones; i++) {
            int cellX = i % (int) gridWidth;
            int cellY = i / (int) gridWidth;
            Rectangle2D zoneBounds = new Rectangle2D(
                    gridX + cellX * (zoneSize + gridGap),
                    gridY + cellY * (zoneSize + gridGap),
                    zoneSize, zoneSize
            );
            ArrayList<Rectangle2D> rects = new ArrayList<>();
            if(mainScope.getSimulationConfig().isEnableCenterZone()) {
                int centerSize = Simulation.ZONE_CENTER_SIZE;
                int centerX = Simulation.ZONE_CENTER_X;
                int centerY = Simulation.ZONE_CENTER_Y;
                rects.add(new Rectangle2D(centerX, centerY, centerSize, centerSize));
            }
            Zone zone = new Zone(zoneBounds, Color.BLACK, rects);
            zones.add(zone);
        }

        if(quarantineEnabled) {
            Rectangle2D quarantineBounds = new Rectangle2D(
                    0,
                    zones.get(nzones - 1).bounds().getMaxY() - quarantineSize,
                    quarantineSize, quarantineSize
            );
            Zone quarantine = new Zone(quarantineBounds, Color.RED, new ArrayList<>());
            zones.add(quarantine);
        }

        worldBounds = new Rectangle2D(0, 0,
                zones.get((int)gridWidth - 1).bounds().getMaxX(),
                zones.get(nzones - 1).bounds().getMaxY()
        );
    }

    private void updateSimulationPoints(SimulationState state) {
        points = new ArrayList<>();
        for(int i = 0; i < state.zones.size() + 1; i++) {
            boolean isQuarantine = i == state.zones.size();
            ZoneState zoneState = isQuarantine ? state.quarantine : state.zones.get(i);
            Zone zone = zones.get(i);
            points.ensureCapacity(zoneState.individuals.size());
            for(var ind : zoneState.individuals) {
                boolean inCenterZone = !zone.rects.isEmpty() &&
                        zone.rects.get(0).contains(new Point2D(ind.posX, ind.posY));
                double x = zone.bounds().getMinX() + ind.posX;
                double y = zone.bounds().getMinY() + ind.posY;
                CompartmentConfig comp = mainScope.getSimulationConfig().getSelectedModel().getCompartments().get(ind.compartmentId);
                points.add(getSimulationPoint(ind.compartmentId, x, y, !isQuarantine && !inCenterZone));
            }
        }
        for(var ind : state.travelers) {
            Zone zone = ind.zoneId != Simulation.QUARANTINE_ZONE_ID ? zones.get(ind.zoneId) :
                    quarantineEnabled ? zones.get(zones.size() - 1) : null;
            Zone dstZone = ind.dstZoneId != Simulation.QUARANTINE_ZONE_ID ? zones.get(ind.dstZoneId) :
                    quarantineEnabled ? zones.get(zones.size() - 1) : null;
            if(zone == null || dstZone == null) {
                continue;
            }
            double srcX = zone.bounds().getMinX() + ind.posX;
            double srcY = zone.bounds().getMinY() + ind.posY;
            double dstX = dstZone.bounds().getMinX() + ind.dstX;
            double dstY = dstZone.bounds().getMinY() + ind.dstY;
            double x = srcX + ind.ratio * (dstX - srcX);
            double y = srcY + ind.ratio * (dstY - srcY);
            points.add(getSimulationPoint(ind.compartmentId, x, y, false));
        }
    }

    private Point getSimulationPoint(int compartmentId, double x, double y, boolean allowEmit) {
        CompartmentConfig comp = mainScope.getSimulationConfig().getSelectedModel().getCompartments().get(compartmentId);
        return new Point(
                new Point2D(x, y),
                Color.valueOf(comp.getColor()),
                allowEmit && comp.getName().equals(CompartmentConfig.INFECTIOUS)
                        ? mainScope.getSimulationConfig().getInfectionRadius() : 0
        );
    }

    private void updateSimulationChart(SimulationState state) {
        ArrayList<ModelChart.Chart> charts = new ArrayList<>();
        var comps = mainScope.getSimulationConfig().getSelectedModel().getCompartments();
        for(int i = 0; i < comps.size(); i++) {
            var comp = comps.get(i);
            ArrayList<Double> xdata = new ArrayList<>(state.stats.points.size());
            ArrayList<Double> ydata = new ArrayList<>(state.stats.points.size());

            for(var point : state.stats.points) {
                xdata.add(point.time);
                ydata.add(point.populations.get(i));
            }

            charts.add(new ModelChart.Chart(xdata, ydata, comp.getName(), Color.valueOf(comp.getColor())));
        }
        simulationChart.setAll(charts);
    }
}
