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
    private final int MAX_NUM_PROXI_SENSORS = 8;
    private final int MAX_NUM_ULTRA_SENSORS = 4;

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
        /*SensorModel[] sensormodels = new SensorModel[4];//\TODO: use one of the encoded params to determine this
        // create sensor models
        sensormodels[0] = new SensorModel(SensorType.BOTTOM_PROXIMITY);
        // sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],(float) encoded[2],(float) encoded[3]);
        sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],  encoded[1],(float) encoded[3]);
        sensormodels[2] = new SensorModel(SensorType.ULTRASONIC,(float) encoded[0],(float) encoded[1],  encoded[1],(float) encoded[3]);
        sensormodels[3] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0],(float) encoded[1],  encoded[1],(float) encoded[3]);
        // sensormodels[1] = new SensorModel(SensorType.PROXIMITY,(float) encoded[0], 0.0f, 4.0f, 0.1f);
        // sensormodels[1] = new SensorModel(SensorType.ULTRASONIC,(float) encoded[0], 0.0f, 4.0f, 0.1f);
        // construct the morphology
        sensorMorphology = new SensorMorphology(sensormodels);*/

        final int numSensors = (int) decodePart(encoded, "numSensors", -1) + 1; // the one is added to include the bottom proximity sensor
        // create the sensor models
        int sensorModelIter = 0; // incremented each time a sensor is added to the sensor models
        SensorModel[] sensormodels = new SensorModel[numSensors];
        System.out.println(numSensors);
        // there will always be a bottom proximity sensor on an agent
        // System.out.println("MorphChrom.decodeFromArray.numSense "+numSensors);
        sensormodels[sensorModelIter] = new SensorModel(SensorType.BOTTOM_PROXIMITY);
        sensorModelIter++;
        
        /*final int numProx = (int) decodePart(encoded, "numProx", -1);
        final int numUltr = (int) decodePart(encoded, "numUltr", -1);
        // configure all the proximity sensors (if they exist)
        for(int proxiIter = proxiParamStart + MAX_NUM_PROXI_SENSORS; proxiIter < MAX_NUM_PROXI_SENSORS * BORF - 1; proxiIter = proxiIter + 4){
            if(numProx == 0){break;} // do not need to add them since they are not on
            sensormodels[sensorModelIter++] = new SensorModel(
                SensorType.PROXIMITY,
                (float) decodePart(null, "zero_to_2pi", encoded[proxiIter]),
                (float) decodePart(null, "zero_to_2pi", encoded[proxiIter+1]),
                (float) decodePart(null, "range_proxi", encoded[proxiIter+1]),
                (float) decodePart(null, "zero_to_pi", encoded[proxiIter+1])
            );
        }
        for(int ultraIter = ultraParamStart + MAX_NUM_PROXI_SENSORS; ultraIter < MAX_NUM_ULTRA_SENSORS * BORF -1; ultraIter = ultraIter + 4){
            if(numUltr == 0){break;} // do not need to add them since they are not on
            sensormodels[sensorModelIter++] = new SensorModel(
                SensorType.ULTRASONIC,
                (float) decodePart(null, "zero_to_2pi", encoded[ultraIter]),
                (float) decodePart(null, "zero_to_2pi", encoded[ultraIter+1]),
                (float) decodePart(null, "range_ultra", encoded[ultraIter+1]),
                (float) decodePart(null, "zero_to_pi", encoded[ultraIter+1])
            );
        }*/
        // configure all the 'on' proximity sensors
        for(int proxiIter = proxiParamStart; proxiIter < proxiParamStart + MAX_NUM_PROXI_SENSORS; proxiIter++){
            if(encoded[proxiIter] > 0){ // implies that this sensor is on
                int paramRegion = MAX_NUM_PROXI_SENSORS + (proxiIter * BORF);
                // System.out.println("MorphChrom.decodeFromArray.proxi.senseNum "+sensorModelIter);
                sensormodels[sensorModelIter] = new SensorModel(
                    SensorType.PROXIMITY,
                    (float) 0.0f,//decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) decodePart(null, "zero_to_pi", encoded[paramRegion+1]), //\TODO: urgent, find out why this angle is sooo wrong
                    (float) 4.0f,//decodePart(null, "range_proxi", encoded[paramRegion+1]),
                    (float) 0.1f//decodePart(null, "zero_to_pi", encoded[paramRegion+1])
                );
                sensorModelIter++;
            }
            System.out.println("p_iter "+proxiIter+" s_model "+sensorModelIter+" val "+encoded[proxiIter]);
        }

        // configure all the 'on' ultrasonic sensors
        for(int ultraIter = ultraParamStart; ultraIter < ultraParamStart + MAX_NUM_ULTRA_SENSORS; ultraIter++){
            if(encoded[ultraIter] > 0){ // implies that this sensor is on
                int paramRegion = MAX_NUM_ULTRA_SENSORS + (ultraIter * BORF);
                // System.out.println("MorphChrom.decodeFromArray.ultra.senseNum "+sensorModelIter);
                sensormodels[sensorModelIter] = new SensorModel(
                    SensorType.ULTRASONIC,
                    (float) 0.0f,//decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) decodePart(null, "zero_to_pi", encoded[paramRegion+1] + Math.PI/2), //\TODO: urgent, find out why this angle is sooo wrong
                    (float) 4.0f,//decodePart(null, "range_ultra", encoded[paramRegion+1]),
                    (float) 0.1f//decodePart(null, "zero_to_pi", encoded[paramRegion+1])
                );
                sensorModelIter++;
            }
            System.out.println("u_iter "+ultraIter+" s_model "+sensorModelIter+" val "+encoded[ultraIter]);
        }

        sensorMorphology = new SensorMorphology(sensormodels);
    }

    /**
     * the genomes are normalized to the range (-1:1)
     * this is decoded into three parts:
     ** sensors of each type that are on/off - < 0 => off. > 0 => on
     ** the angular parameter (bearing/orientation/field of view) - inverse hyperbolic tangent
     ** and the range /TODO fill in the thingthang
     */
    public double decodePart(double[] encoded, String part, double gene){
        double numSensors = 0.0;
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
            case "numProx":
                // count the number of proximity sensors
                for(int i = proxiParamStart; i < MAX_NUM_PROXI_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                return numSensors;
            case "numUltr":
                // count the number of ultrasonic sensors
                for(int i = ultraParamStart; i < MAX_NUM_ULTRA_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                return numSensors;
            case "zero_to_pi":
                // convert the gene to an angle in the range [0 : pi) 
                return atanh(gene + Math.PI)/2;
            case "zero_to_2pi":
                // convert the gene to an angle in the range [0 : 2*pi) 
                return atanh(gene + Math.PI);
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

    private double atanh(double x){
        return 0.5*Math.log( (x + 1.0) / (x - 1.0) ) + Math.PI;
    }

    private double clamp(double min, double max, double number){
        if(number < min) return min;
        if(number > max) return max;
        return number;
    }
}