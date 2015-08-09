package za.redbridge.genetic;

import za.redbridge.genetic.sensor.SensorType;
import za.redbridge.genetic.sensor.SensorMorphology;
import za.redbridge.genetic.sensor.SensorConfiguration;

/**
 * this enum contains information about each sensor type such as the maximum
 * number of sensors of that type that are allowed for each agent, and the
 * curent number of those sensors that belong to an agent
 */
enum SensorStatus{
    BOTTOM_PROXIMITY(1), PROXIMITY(8), ULTRASONIC(4);

    private int typeCount;  // the number of sensors of the current type
    private int typeMax;    // the maximum number of sensors for this type

    /**
     * constructor for the sensor status enums
     * @param max the maximum number of sensors of this type that is allowed
     */
    SensorStatus(int typeMax){
        this.typeMax = typeMax;
        typeCount = 0;
    }

    /**
     * returns the number of sensors of this type that have been added to the
     * chromosome
     * @return the sensor count
     */
    public int getSensorCount( ){
        return this.typeCount;
    }

    public static int getNumActiveSensors( ){
        return BOTTOM_PROXIMITY.typeCount + PROXIMITY.typeCount + ULTRASONIC.typeCount;
    }

    /**
     * add a sensor to the chromosome. a sensor is only added if the maximum
     * sensor has not been reached and if the maximum number of sensors of
     * that type has not been reached
     * @return true if the sensor was succesfully added
     */
    public boolean addSensor( ){
        int maximum = PROXIMITY.typeMax + ULTRASONIC.typeMax + BOTTOM_PROXIMITY.typeCount;
        int total = PROXIMITY.typeCount + ULTRASONIC.typeCount + BOTTOM_PROXIMITY.typeCount; // will always have bottom proximity

        // check if the max number of total sensors or sensors of this type has been added
        if((total + 1) > maximum || (typeCount + 1) > typeMax){
            return false;
        }

        // add the sensor and return true for success
        typeCount = typeCount + 1;
        return true;
    }

   /* *//**
     * check whether this type of sensor can be added. it cannot be added if the current
     * number of it's type will be exceeded after if the sensor is added
     * @return true if the sensor can be added
     *//*
    public boolean addable( ){
        return (typeCount + 1) <= typeMax || ;
    }*///\TODO remove this too if not needed

    /**
     * remove this sensor from the chromosome
     */
    public void removeSensor( ){
        this.typeCount = this.typeCount - 1;
    }
} //\ TODO maybe move this enum to MorphGenome

/**
 * the MorphChromosome object holds information about the configuration of each
 * sensor in the morphology
 */
public class MorphChromosome{

    private static final long serialVersionUID = 6663988435664701217L;

    /**
     * the length of a genome. this dictates the number of parameters a genome
     * will have.
     * parameters: [BEARING, ORIENTATION, RANGE, FOV]
     */
    private static final int GENOME_LENGTH = 4;

    // maximum number of sensors allowed for the agent
    private int maxSensors;

    /**
     * the list of genomes for this generation. each genome describes the configuration
     * of each agent's morphology
     */
    private MorphGenome[] sensorGenomes;

    private SensorStatus[] sensorTypes;

    SensorMorphology sensorMorphology;

    private SensorConfiguration sensorConfiguration;

    /**
     * create a new agent chromosome
     * @param maxSensors the maximum number of sensors that can be added
     * @param sensorMorphology the morphology
     */
    public MorphChromosome(int maxSensors, SensorMorphology sensorMorphology){
        sensorGenomes = new MorphGenome[maxSensors];
        sensorTypes = new SensorStatus[maxSensors];
        this.sensorMorphology = sensorMorphology;
        this.maxSensors = maxSensors;
    }

    public MorphChromosome(MorphChromosome other){
        this.sensorTypes = other.sensorTypes.clone();
        this.sensorMorphology = sensorMorphology.clone();//\TODO write proper clone method for this
        this.maxSensors = other.maxSensors;
        setSensorConfiguration(other.getSensorConfiguration().clone());
    }

    public SensorMorphology getSensorMorphology() {
        return sensorMorphology;
    }

    /**
     * add a sensor (genome) of a given type to the morphology of the agents
     * @param type the type of sensor to be added
     * @return true of the sensor was successfully added
     */
    public boolean addSensor(SensorType type){
        boolean added = false;
        switch(type){
            case BOTTOM_PROXIMITY:
                // check if the sensor can be added
                added = SensorStatus.BOTTOM_PROXIMITY.addSensor( );
                /*if(!SensorStatus.BOTTOM_PROXIMITY.addable( )){
                    return false;
                }*/// \TODO remove this if not needed
                if(!added){
                    return false;
                }
                sensorGenomes[SensorStatus.getNumActiveSensors()] = new MorphGenome(GENOME_LENGTH);
                sensorTypes[SensorStatus.getNumActiveSensors()] = SensorStatus.BOTTOM_PROXIMITY;
                return added;
            case PROXIMITY:
                // check if the sensor can be added
                added = SensorStatus.PROXIMITY.addSensor( );

                if(!added){
                    return false;
                }
                sensorGenomes[SensorStatus.getNumActiveSensors()] = new MorphGenome(GENOME_LENGTH);
                sensorTypes[SensorStatus.getNumActiveSensors()] = SensorStatus.PROXIMITY;
                return added;
            case ULTRASONIC:
                // check if the sensor can be added
                added = SensorStatus.ULTRASONIC.addSensor( );
                if(!added){
                    return false;
                }
                sensorGenomes[SensorStatus.getNumActiveSensors()] = new MorphGenome(GENOME_LENGTH);
                sensorTypes[SensorStatus.getNumActiveSensors()] = SensorStatus.ULTRASONIC;
                return added;
            default:
                return false; // invalid sensor type (should not reach here)
        }
    }

    public SensorStatus getSensorType(int i){
        return sensorTypes[i];
    }

    public double[] getSensorParams(int i){
        return sensorGenomes[i].getData( );
    }

    /**
     * get the number of currently active sensors
     * @return
     */
    public int getNumActiveSensors( ){
        return SensorStatus.getNumActiveSensors( );
    }

    public SensorConfiguration getSensorConfiguration() {
        return sensorConfiguration;
    }

    public void setSensorConfiguration(SensorConfiguration sensorConfiguration) {
        this.sensorConfiguration = sensorConfiguration;
    }
}