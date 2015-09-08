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
import org.apache.commons.math3.complex.Complex;

import java.io.Serializable;

public class MorphChrom implements MLMethod, Serializable{
    private SensorMorphology sensorMorphology;
    private final int MAX_NUM_PROXI_SENSORS = 8;
    private final int MAX_NUM_ULTRA_SENSORS = 4;
    private int numSensors;
    private int NumProxiSensors;
    private int NumUltraSensors; // for stats recording purposes

    private final int BORF = 4; // the number of parameters needed to encode bearing, orientation, range, field of viewss
    private static final double ULTRASONIC_SENSOR_MAX_RANGE = 4.0; // 4 meters
    private static final double ULTRASONIC_SENSOR_MIN_RANGE = 0.2; // 20 centimeters
    private static final double PROXIMITY_SENSOR_RANGE = 0.2; // 20 centimeters
    private static final double PROXIMITY_SENSOR_RANGE_MIN = 0.01; //\TODO just check that this is acceptable
    private final int proxiParamStart = 0;
    private final int ultraParamStart = 40;

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
        // count the total number of sensors, as well as the number of each type of sensor
        countSensors(encoded);

        // create the sensor models
        int sensorModelIter = 0; // incremented each time a sensor is added to the sensor models
        SensorModel[] sensormodels = new SensorModel[numSensors];
        // there will always be a bottom proximity sensor on an agent
        sensormodels[sensorModelIter++] = new SensorModel(SensorType.BOTTOM_PROXIMITY);
        
        // configure all the 'on' proximity sensors
        for(int proxiIter = proxiParamStart; proxiIter < proxiParamStart + MAX_NUM_PROXI_SENSORS; proxiIter++){
            if(encoded[proxiIter] > 0){ // implies that this sensor is on
                // get the position of this sensor's specs
                int paramRegion = (MAX_NUM_PROXI_SENSORS + proxiParamStart) + ((proxiIter - proxiParamStart) * BORF);
                sensormodels[sensorModelIter++] = new SensorModel(
                    SensorType.PROXIMITY,
                    (float) decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) (decodePart(null, "zero_to_pi", encoded[paramRegion+1]) + Math.PI/2), //\TODO: urgent, find out why this angle is sooo wrong
                    (float) 3.0f,//decodePart(null, "range_proxi", encoded[paramRegion+1]),
                    (float) decodePart(null, "zero_to_pi", encoded[paramRegion+1])
                );
            }
        }

        // configure all the 'on' ultrasonic sensors
        for(int ultraIter = ultraParamStart; ultraIter < ultraParamStart + MAX_NUM_ULTRA_SENSORS; ultraIter++){
            if(encoded[ultraIter] > 0){ // implies that this sensor is on
                int paramRegion = (MAX_NUM_ULTRA_SENSORS + ultraParamStart) + ((ultraIter - ultraParamStart) * BORF);
                sensormodels[sensorModelIter++] = new SensorModel(
                    SensorType.ULTRASONIC,
                    (float) decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) (decodePart(null, "zero_to_pi", encoded[paramRegion+1]) + Math.PI/2), //\TODO: urgent, find out why this angle is sooo wrong
                    (float) 4.0f,//decodePart(null, "range_ultra", encoded[paramRegion+1]),
                    (float) decodePart(null, "zero_to_pi", encoded[paramRegion+1])
                );
            }
        }

        // create the sensor morphology
        sensorMorphology = new SensorMorphology(sensormodels);
    }

    /**
     * the genomes are normalized to the range (-1:1)
     * this is decoded into three parts:
     ** sensors of each type that are on/off - < 0 => off. > 0 => on
     ** the angular parameter (bearing/orientation/field of view) - inverse hyperbolic tangent
     ** and the range /TODO fill in the thingthang
     * @param encoded the genome 
     * @param the part of the genome to decode
     * @param the gene of the genome
     */
    public double decodePart(double[] encoded, String part, double gene){
        double numSensors = 0.0; // made a double so that it can be returned by this method
        switch(part){
            case "numSensors":
                // count the number of proximity sensors
                // if gene > 0, then the sensors is on, else it is off
                for(int i = proxiParamStart; i < proxiParamStart + MAX_NUM_PROXI_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                // count the number of ultrasonic sensors
                for(int i = ultraParamStart; i < ultraParamStart + MAX_NUM_ULTRA_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                return numSensors;
            case "numProxi":
                // count the number of proximity sensors
                for(int i = proxiParamStart; i < proxiParamStart + MAX_NUM_PROXI_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                return numSensors;
            case "numUltra":
                // count the number of ultrasonic sensors
                for(int i = ultraParamStart; i < ultraParamStart + MAX_NUM_ULTRA_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                return numSensors;
            case "zero_to_pi":
                // convert the gene to an angle in the range [0 : pi) 
                return atanh(gene)/2;
            case "zero_to_2pi":
                // convert the gene to an angle in the range [0 : 2pi) 
                return atanh(gene);
            case "range_proxi":
                // convert the number in the range (-1:1) to a range valid for the proximity sensor (0:0.2)
                gene = (gene + 1)/10;
                return clamp(PROXIMITY_SENSOR_RANGE_MIN, PROXIMITY_SENSOR_RANGE, gene);
            case "range_ultra":
                // convert the number in the range (-1:1) to a range valid for the ultrasonic sensor (0.2:4.0)
                gene = gene * 2;
                return clamp(ULTRASONIC_SENSOR_MIN_RANGE, ULTRASONIC_SENSOR_MAX_RANGE, gene);
        }
        return 0.0; // should not reach here
    }

    public void countSensors(double[] encoded){
        // add one to the total number of sensors to include the bottom proximity sensor
        numSensors = ((int) Math.round(decodePart(encoded, "numSensors", -1))) + 1;
        NumProxiSensors = (int) decodePart(encoded, "numProxi", -1);
        NumUltraSensors = (int) decodePart(encoded, "numUltra", -1);

    }

    private double atanh(double x){
        // create a complex number using the gene as the real part
        Complex c = new Complex((x + 1.0) / (x - 1.0), 0.0);
        // calculate its logarithm, and return the absolute value of the resulting complex number
        // pi is added to move the range from [-pi:pi] to [0:2pi)
        return c.log().abs() + Math.PI;//0.5*Math.log( (x + 1.0) / (x - 1.0) ) + Math.PI;
    }

    private double clamp(double min, double max, double number){
        if(number < min) return min;
        if(number > max) return max;
        return number;
    }

    public int getNumProxiSensors( ){
        return NumProxiSensors;
    }

    public int getNumUltraSensors( ){
        return NumUltraSensors;
    }
}