package za.redbridge.morphevo;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.MLMethod;

import java.lang.Override;
import java.lang.StackTraceElement;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.simulator.sensor.AgentSensor;
import za.redbridge.simulator.khepera.BottomProximitySensor;
import za.redbridge.simulator.khepera.ProximitySensor;
import za.redbridge.simulator.khepera.UltrasonicSensor;

import za.redbridge.morphevo.sensor.SensorMorphology;
import za.redbridge.morphevo.sensor.SensorModel;
import za.redbridge.morphevo.sensor.SensorType;

import java.io.Serializable;

public class MorphChrom implements MLMethod, Serializable{
    private SensorMorphology sensorMorphology;

    public MorphChrom( ){
        sensorMorphology = null;
    }

    public MorphChrom(SensorMorphology sensorMorphology){
        this.sensorMorphology = sensorMorphology;
    }

    public SensorMorphology getSensorMorphology( ){
        return sensorMorphology;
    }

    /**
     * @return The length of an encoded array.
     */
    public int encodedArrayLength(){
        // \TODO alternatively return sensors.size();
//        return chromosome.getNumActiveSensors();
        return sensorMorphology.getNumSensors();
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
        SensorModel[] sensormodels = new SensorModel[4];//\TODO: use one of the encoded params to determine this
        // create sensor models
        sensormodels[0] = new SensorModel(SensorType.BOTTOM_PROXIMITY);
        // sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],(float) encoded[2],(float) encoded[3]);
        sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],  encoded[1],(float) encoded[3]);
        sensormodels[2] = new SensorModel(SensorType.ULTRASONIC,(float) encoded[0],(float) encoded[1],  encoded[1],(float) encoded[3]);
        sensormodels[3] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],  encoded[1],(float) encoded[3]);
        // sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0], 0.0f, 4.0f, 0.1f);
        // sensormodels[1] = new SensorModel(SensorType.ULTRASONIC,(float) encoded[0], 0.0f, 4.0f, 0.1f);
        // construct the morphology
        sensorMorphology = new SensorMorphology(sensormodels);
    }
}