package za.redbridge.morphevo;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.MLMethod;

import java.lang.StackTraceElement;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.simulator.phenotype.Phenotype;
import za.redbridge.simulator.sensor.AgentSensor;
import za.redbridge.simulator.khepera.BottomProximitySensor;
import za.redbridge.simulator.khepera.ProximitySensor;
import za.redbridge.simulator.khepera.UltrasonicSensor;

import za.redbridge.morphevo.sensor.SensorMorphology;
import za.redbridge.morphevo.sensor.SensorModel;
import za.redbridge.morphevo.sensor.SensorType;

import java.io.Serializable;

public class MorphPhenotype implements Phenotype, MLEncodable, MLMethod, Serializable{
//    private final MLData input;
    private List<AgentSensor> sensors;
    final private int myNumSensors = 2;
    MLData input;

    public MorphPhenotype( ) {
        input = new BasicMLData(myNumSensors);// TODO : urgently fix this (should be num sensors, not 2)
    }

    @Override
    public List<AgentSensor> getSensors( ) {
        return sensors;
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        System.out.println("numsensies: "+sensors.size());
        final MLData input = this.input;
        for (int i = 0, n = input.size(); i < n; i++) {
            input.setData(i, sensorReadings.get(i).get(0));
        }

//        MLData output = network.compute(input);
//        return new Double2D(output.getData(0) * 2.0 - 1.0, output.getData(1) * 2.0 - 1.0);
        return new Double2D(1,1);// TODO : urgently fix this
    }

    @Override
    public Phenotype clone() {
        return new MorphPhenotype( );
//        return new NEATMPhenotype(network);
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return The length of an encoded array.
     */
    public int encodedArrayLength(){
        // \TODO alternatively return sensors.size();
//        return chromosome.getNumActiveSensors();
        return sensors.size();
    }

    /**
     * Encode the object to the specified array.
     * @param encoded The array.
     */
    public void encodeToArray(double[] encoded){
        throw new GeneticError("Encoding of my method is not supported.");
    }

    /**
     * Decode an array to this object.
     * @param encoded The encoded array.
     */
    public void decodeFromArray(double[] encoded){
        SensorModel[] sensormodels = new SensorModel[myNumSensors];
        // create sensor models
        sensormodels[0] = new SensorModel(SensorType.BOTTOM_PROXIMITY);
        sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],(float) encoded[2],(float) encoded[3]);

        // construct the morphology
        SensorMorphology morphology = new SensorMorphology(sensormodels);

        final int numSensors = morphology.getNumSensors();
        sensors = new ArrayList<>(numSensors);
        sensors.add(morphology.getSensor(0));
        sensors.add(morphology.getSensor(1));
    }

    public int getNumSensors( ){
        return sensors.size( );
    }
}