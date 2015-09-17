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
import org.jbox2d.common.MathUtils;

import java.io.Serializable;

public class MorphChrom implements MLMethod, Serializable{
    private static long serialVersionUID =  5956456018923114112L;

    private SensorMorphology sensorMorphology;
    protected static final int MAX_NUM_PROXI_SENSORS = 8;
    protected static final int MAX_NUM_ULTRA_SENSORS = 4;
    protected static final int MAX_NUM_COLOUR_PROXI_SENSORS = 2;

    private int numSensors;
    private int NumProxiSensors;
    private int NumUltraSensors;
    private int NumColourProxiSensors; // for stats recording purposes

    protected static final int BORF = 4; // the number of parameters needed to encode bearing, orientation, range, field of viewss
    private static final double ULTRASONIC_SENSOR_RANGE_MAX = 4.0; // 4 meters
    private static final double ULTRASONIC_SENSOR_RANGE_MIN = 0.2; // 20 centimeters
    private static final double PROXIMITY_SENSOR_RANGE = 0.2; // 20 centimeters
    private static final double PROXIMITY_SENSOR_RANGE_MIN = 0.01; //\TODO just check that this is acceptable
    private static final double COLOUR_PROXIMITY_SENSOR_RANGE = 3.0; // 4 meters
    private static final double COLOUR_PROXIMITY_SENSOR_RANGE_MIN = 0.01; //\TODO just check that this is acceptable
    private static final double MIN_FOV = 0.01; //\TODO: just check that this too is reasonable apparently this cannot be zero
    private final int proxiParamStart = 0;
    private final int ultraParamStart = 40;
    private final int colourProxiParamStart = 60;

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
                addSensor(SensorType.PROXIMITY, sensormodels, encoded, paramRegion, sensorModelIter);
                sensorModelIter++; // included the added sensor
            }
        }

        // configure all the 'on' ultrasonic sensors
        for(int ultraIter = ultraParamStart; ultraIter < ultraParamStart + MAX_NUM_ULTRA_SENSORS; ultraIter++){
            if(encoded[ultraIter] > 0){ // implies that this sensor is on
                // get the position of this sensor's specs
                int paramRegion = (MAX_NUM_ULTRA_SENSORS + ultraParamStart) + ((ultraIter - ultraParamStart) * BORF);
                addSensor(SensorType.ULTRASONIC, sensormodels, encoded, paramRegion, sensorModelIter);
                sensorModelIter++; // included the added sensor
            }
        }

        // configure all the 'on' colour proximity sensors
        for(int colourProxiIter = colourProxiParamStart; colourProxiIter < colourProxiParamStart + MAX_NUM_COLOUR_PROXI_SENSORS; colourProxiIter++){
            if(encoded[colourProxiIter] > 0){ // implies that this sensor is on
                // get the position of this sensor's specs
                int paramRegion = (MAX_NUM_COLOUR_PROXI_SENSORS + colourProxiParamStart) + ((colourProxiIter - colourProxiParamStart) * BORF);
                addSensor(SensorType.COLOUR_PROXIMITY, sensormodels, encoded, paramRegion, sensorModelIter);
                sensorModelIter++; // included the added sensor
            }
        }

        // create the sensor morphology
        sensorMorphology = new SensorMorphology(sensormodels);
    }

    public void addSensor(SensorType sensorType, SensorModel[] sensorModels_, double[] encoded, int paramRegion, int sensorModelIter){
        switch(sensorType){
            case PROXIMITY:
                sensorModels_[sensorModelIter] = new SensorModel(
                    SensorType.PROXIMITY,
                    (float) decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) (decodePart(null, "zero_to_pi", encoded[paramRegion+1])),
                    (float) decodePart(null, "range_proxi", encoded[paramRegion+2]),
                    (float) decodePart(null, "filed_of_view", encoded[paramRegion+3])
                );
                break;
            case ULTRASONIC:
                sensorModels_[sensorModelIter] = new SensorModel(
                    SensorType.ULTRASONIC,
                    (float) decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) (decodePart(null, "zero_to_pi", encoded[paramRegion+1])),
                    (float) decodePart(null, "range_ultra", encoded[paramRegion+2]),
                    (float) decodePart(null, "filed_of_view", encoded[paramRegion+3])
                );
                break;
            case COLOUR_PROXIMITY:
                sensorModels_[sensorModelIter] = new SensorModel(
                    SensorType.COLOUR_PROXIMITY,
                    (float) decodePart(null, "zero_to_2pi", encoded[paramRegion]),
                    (float) (decodePart(null, "zero_to_pi", encoded[paramRegion+1])),
                    (float) decodePart(null, "range_colour_proxi", encoded[paramRegion+2]),
                    (float) decodePart(null, "filed_of_view", encoded[paramRegion+3])
                );
                break;
        }
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
                // count the number of colour proximity sensors
                for(int i = colourProxiParamStart; i < colourProxiParamStart + MAX_NUM_COLOUR_PROXI_SENSORS; i++){
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
            case "numColorProxi":
                // count the number of colour proximity sensors
                for(int i = colourProxiParamStart; i < colourProxiParamStart + MAX_NUM_COLOUR_PROXI_SENSORS; i++){
                    if(encoded[i] > 0){numSensors = numSensors + 1;}
                }
                return numSensors;
            case "zero_to_pi":
                // convert the gene to an angle in the range [0 : pi) 
                return atanh(gene)/2;
            case "zero_to_2pi":
                // convert the gene to an angle in the range [0 : 2pi) 
                return atanh(gene);
            case "filed_of_view":
                // since field of view cannot have a value of zero
                gene = atanh(gene)/2;
                return clamp(MIN_FOV, MathUtils.PI, gene);
            case "range_proxi":
                // convert the number in the range (-1:1) to a range valid for the proximity sensor (0:0.2)
                gene = (gene + 1)/10;
                return clamp(PROXIMITY_SENSOR_RANGE_MIN, PROXIMITY_SENSOR_RANGE, gene);
            case "range_ultra":
                // convert the number in the range (-1:1) to a range valid for the ultrasonic sensor (0.2:4.0)
                gene = (gene + 1) * 2;
                return clamp(ULTRASONIC_SENSOR_RANGE_MIN, ULTRASONIC_SENSOR_RANGE_MAX, gene);
            case "range_colour_proxi":
                // convert the number in the rante (-1:1) to a range valid for the colour proximity sensor (0:3.0)
                gene = (3 * gene + 3) / 2;
                return clamp(COLOUR_PROXIMITY_SENSOR_RANGE_MIN, COLOUR_PROXIMITY_SENSOR_RANGE, gene);
        }
        return 0.0; // should not reach here
    }

    public void countSensors(double[] encoded){
        // add one to the total number of sensors to include the bottom proximity sensor
//        numSensors = ((int) Math.round(decodePart(encoded, "numSensors", -1))) + 1;
        NumProxiSensors = (int) Math.round(decodePart(encoded, "numProxi", -1));
        NumUltraSensors = (int) Math.round(decodePart(encoded, "numUltra", -1));
        NumColourProxiSensors = (int) Math.round(decodePart(encoded, "numColorProxi", -1));
        numSensors = NumProxiSensors + NumUltraSensors + NumColourProxiSensors + 1;

    }

    /**
     * map from the genome space to the phenome space using the inverse tanh (atanh) function defined as
     * atanh = 0.5*ln( (x + 1)/(x - 1) ). Since our domain for x is such that x = [-1,1], the mapping
     * also maps to positive or negative infinity, these are simply the cases when the angle is either
     * 0 or pi respectively
     * @param x the gene to be mapped to phenome space
     * @return the phenome mapping of the gene x
     */
    private double atanh(double x){
        // create a complex number using the argument of the gene's inverse tanch as the real part
        Complex compX = new Complex((x + 1.0) / (x - 1.0), 0.0);

        // calculate its natural logarithm and extract real part of the resulting complex number
        double logX = compX.log().getReal();

        // compute the inverse tanh function
        double atanh = 0.5*logX;

        // now check that the resulting value is not +/- infinity (when the gene is +/-1.0)
        if(atanh == Double.POSITIVE_INFINITY){
            return 2*MathUtils.PI;
        } else if(atanh == Double.NEGATIVE_INFINITY){
            return 0;
        } else { // pi is added to move the range from [-pi:pi] to [0:2pi)
            return atanh + MathUtils.PI;
        }
    }

    private double clamp(double min, double max, double number){
        if(number < min) return min;
        if(number > max) return max; // should not happen
        return number;
    }

    /**
     * get the number of proximity sensors that are acitvated
     * @return the number of proximity sensors
     */
    public int getNumProxiSensors( ){
        return NumProxiSensors;
    }

    /**
     * get the number of ultrasonic sensors that are acitvated
     * @return the number of ulstrasonic sensors
     */
    public int getNumUltraSensors( ){
        return NumUltraSensors;
    }

    /**
     * get the number of colour proximity sensors that are acitvated
     * @return the number of ulstrasonic sensors
     */
    public int getNumColourProxiSensors( ){
        return NumColourProxiSensors;
    }
}