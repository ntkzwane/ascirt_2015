package za.redbridge.morphevo;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.GeneticError;

import java.lang.Override;
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
import za.redbridge.simulator.khepera.ColourProximitySensor;

import za.redbridge.morphevo.sensor.SensorMorphology;

import java.io.Serializable;

public class MorphPhenotype implements Phenotype, Serializable{
    protected static final double HALF_PI = Math.PI / 2;
    private final MLData input;
    private List<AgentSensor> sensors;
    private SensorMorphology morphology;

    // chasing phenotype stuff \TODO check if needed
    private static final int COOLDOWN = 10;
    private int cooldownCounter = 0;
    private Double2D lastMove = null;

    public MorphPhenotype(MorphChrom morphChrom) {
        // construct the morphology
        SensorMorphology morphology = morphChrom.getSensorMorphology();
        final int numSensors = morphology.getNumSensors();
        sensors = new ArrayList<>(numSensors);
        for(int i = 0; i < numSensors; i++){
            sensors.add(morphology.getSensor(i));
        }
        input = new BasicMLData(numSensors);

        // for the sake of the copy constructor
        this.morphology = morphology;
    }

    public MorphPhenotype(MorphPhenotype other){
        final int numSensors = other.getNumSensors();
        SensorMorphology newMorph = new SensorMorphology(other.getPhenotypeMorphology());
        sensors = new ArrayList<>(numSensors);
        for(int i = 0; i < numSensors; i++){
            sensors.add(newMorph.getSensor(i));
        }
        input = new BasicMLData(numSensors);
    }

    public SensorMorphology getPhenotypeMorphology( ){
        return this.morphology;
    }

    @Override
    public List<AgentSensor> getSensors( ) {
        return sensors;
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        /*if(sensorReadings.size() == 0){
            return new Double2D(1.0,1.0);
        }

        Double2D left = new Double2D(0.5,1.0);
        Double2D forward = new Double2D(1.0,1.0);
        Double2D right = new Double2D(1.0,0.5);
        Double2D random = new Double2D((float)Math.random()*2f - 1f, (float)Math.random()*2f - 1f);

        if(cooldownCounter > 0) {
            cooldownCounter--;
            return lastMove;
        }else {
            cooldownCounter = COOLDOWN;
        }

        double leftReading = sensorReadings.get(0).get(0);
        double forwardReading = sensorReadings.get(1).get(0);
        double rightReading = sensorReadings.get(2).get(0);
        double max = Math.max(leftReading, Math.max(forwardReading, rightReading));
        if(max < 0.0001){
            lastMove = random;
            return random;
        }else if(leftReading == max) {
            lastMove = left;
            return left;
        }else if(rightReading == max) {
            lastMove = right;
            return right;
        }else {
            lastMove = forward;
            return forward;
        }*/
        //return new Double2D(1.0,1.0);
        // System.out.println("MorphPhenotype.step.numreadings: "+sensorReadings.size());
        int nearestSensed = -1;
        float maxSensed = 0.0f;
        for(int i = 0; i < sensorReadings.size(); i++){
            if(sensorReadings.get(i).get(0) > maxSensed){
                nearestSensed = i; //\TODO check that this is a sensor that is 'allowed' to sense resources
            }
        }
        // get bearing and move in direction of the bearing of the sensor
        if(nearestSensed > -1){
            double bearing = sensors.get(nearestSensed).getBearing();
            lastMove = wheelDriveForTargetAngle(bearing);
            return lastMove;
        }else if(lastMove != null){
            return lastMove;
        }else{
            return new Double2D((float)Math.random()*2f - 1f, (float)Math.random()*2f - 1f);
        }
    }

    public int getNumSensors( ){
        return sensors.size( );
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Phenotype clone() {
        return new MorphPhenotype(this);
    }



    /** Method can be overridden to customize proximity sensor */
    protected AgentSensor createProximitySensor(float bearing, float orientation) {
        return new ProximitySensor(bearing, orientation);
    }

    /** Method can be overridden to customize bottom proximity sensor */
    protected AgentSensor createBottomProximitySensor() {
        return new BottomProximitySensor();
    }

    /** Method can be overridden to customize ultrasonic sensor */
    protected AgentSensor createUltrasonicSensor(float bearing, float orientation) {
        return new UltrasonicSensor(bearing, orientation);
    }

    /** Method can be overridden to customize colour proximity sensor */
    protected AgentSensor createColourProximitySensor(float bearing, float orientation){
        return new ColourProximitySensor(bearing, orientation);
    }

/**
     * Get the wheel drive that will steer the agent towards the target angle.
     * @param targetAngle The angle to the target
     * @return the heuristic wheel drive
     */
    protected static Double2D wheelDriveForTargetAngle(double targetAngle) {
        final double left, right;
        if(Math.abs(targetAngle) > Math.PI) targetAngle = Math.PI*Math.signum(targetAngle);
        // Different response for each of four quadrants
        if (targetAngle >= 0) {
            if (targetAngle < HALF_PI) {
                // First
                left = (HALF_PI - targetAngle) / HALF_PI;
                right = 1;
            } else {
                // Second
                left = -(targetAngle - HALF_PI) / HALF_PI;
                right = -1;
            }
        } else {
            if (targetAngle < -HALF_PI) {
                // Third
                left = -1;
                right = (targetAngle + HALF_PI) / HALF_PI;
            } else {
                // Fourth
                left = 1;
                right = (HALF_PI + targetAngle) / HALF_PI;
            }
        }
        return new Double2D(left, right);
    }
}