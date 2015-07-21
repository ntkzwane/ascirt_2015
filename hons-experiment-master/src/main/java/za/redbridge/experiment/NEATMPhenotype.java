package za.redbridge.experiment;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

import java.lang.Exception;
import java.lang.Throwable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.experiment.NEATM.NEATMNetwork;
import za.redbridge.experiment.NEATM.sensor.SensorMorphology;
import za.redbridge.simulator.phenotype.Phenotype;
import za.redbridge.simulator.sensor.AgentSensor;

import za.redbridge.simulator.phenotype.HeuristicPhenotype;
import za.redbridge.simulator.phenotype.heuristics.HeuristicSchedule;
import za.redbridge.simulator.phenotype.heuristics.Heuristic;
import za.redbridge.simulator.phenotype.heuristics.CollisionAvoidanceHeuristic;
import za.redbridge.simulator.phenotype.heuristics.PickupHeuristic;
import za.redbridge.simulator.sensor.CollisionSensor;
import za.redbridge.simulator.sensor.PickupSensor;
/**
 * Created by jamie on 2014/09/09.
 */
public class NEATMPhenotype implements Phenotype {

    private final NEATMNetwork network;
    private final MLData input;

    private final List<AgentSensor> sensors;

    private final HeuristicSchedule schedule;
    private CollisionSensor collisionSensor;
    private PickupSensor pickupSensor;

    private static final boolean PICKUP_HEURISTIC_ENABLED = true;
    private static final boolean COLLISION_HEURISTIC_ENABLED = true;

    private static final float PICKUP_SENSOR_WIDTH = 0.1f;
    private static final float PICKUP_SENSOR_HEIGHT = 0.2f;

    // Make sure this is > robot radius
    private static final float COLLISION_SENSOR_RADIUS = 0.55f;

    public NEATMPhenotype(NEATMNetwork network) {
        this.network = network;

        // Initialise sensors
        SensorMorphology morphology = network.getSensorMorphology();
        final int numSensors = morphology.getNumSensors();
        sensors = new ArrayList<>(numSensors);
        for (int i = 0; i < numSensors; i++) {
            sensors.add(morphology.getSensor(i));
        }

        input = new BasicMLData(numSensors);

        schedule = new HeuristicSchedule();
        initHeuristics( );
    }

    @Override
    public List<AgentSensor> getSensors() {
        return sensors;
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        final MLData input = this.input;
        for (int i = 0, n = input.size(); i < n; i++) {
            input.setData(i, sensorReadings.get(i).get(0));
        }

        MLData output = network.compute(input);
        return new Double2D(output.getData(0) * 2.0 - 1.0, output.getData(1) * 2.0 - 1.0);
    }

    private void initHeuristics( ) {
        if (COLLISION_HEURISTIC_ENABLED) {
            collisionSensor = new CollisionSensor(COLLISION_SENSOR_RADIUS);
            schedule.addHeuristic(new CollisionAvoidanceHeuristic(collisionSensor, null));
        }
        if (PICKUP_HEURISTIC_ENABLED) {
            pickupSensor = new PickupSensor(PICKUP_SENSOR_WIDTH, PICKUP_SENSOR_HEIGHT);
            schedule.addHeuristic(new PickupHeuristic(pickupSensor, null, null));
        }
    }

    @Override
    public Phenotype clone() {
        return new NEATMPhenotype(network);
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {
        throw new UnsupportedOperationException();
    }
}
