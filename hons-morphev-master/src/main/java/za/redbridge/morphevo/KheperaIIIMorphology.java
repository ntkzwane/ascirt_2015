package za.redbridge.morphevo;

import za.redbridge.morphevo.sensor.SensorModel;
import za.redbridge.morphevo.sensor.SensorMorphology;
import za.redbridge.simulator.khepera.KheperaIIIPhenotype;
import za.redbridge.simulator.khepera.ProximitySensor;
import za.redbridge.simulator.khepera.UltrasonicSensor;


import static za.redbridge.morphevo.sensor.SensorType.BOTTOM_PROXIMITY;
import static za.redbridge.morphevo.sensor.SensorType.PROXIMITY;
import static za.redbridge.morphevo.sensor.SensorType.ULTRASONIC;
import static za.redbridge.morphevo.sensor.SensorType.COLOUR_PROXIMITY;


/**
 * A horrible adapter class for different representations of morphologies. Creates a
 * {@link SensorMorphology} for a {@link KheperaIIIPhenotype.Configuration}.
 *
 * Created by jamie on 2014/10/06.
 */
public class KheperaIIIMorphology extends SensorMorphology {

    private static final long serialVersionUID = 8121207679231125300L;

    private static final KheperaIIIPhenotype.Configuration DEFAULT_CONFIGURATION =
            new KheperaIIIPhenotype.Configuration();

    static 
    {
        DEFAULT_CONFIGURATION.enableColourProximitySensor180Degrees = true;
        DEFAULT_CONFIGURATION.enableProximitySensor45Degrees = true;
        DEFAULT_CONFIGURATION.enableBottomProximitySensor = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensor60Degrees = true;
        //DEFAULT_CONFIGURATION.enableColourProximitySensor180Degrees = true;
    }

    public KheperaIIIMorphology()
    {
        this(DEFAULT_CONFIGURATION);
    }

    public KheperaIIIMorphology(KheperaIIIPhenotype.Configuration config)
    {
        super(createSensorModels(config));
    }

    private static SensorModel[] createSensorModels(KheperaIIIPhenotype.Configuration config) {
        int sensorIndex = 0;
        final int sensorCount = config.getNumberOfSensors();
        SensorModel[] sensorModels = new SensorModel[sensorCount];

        if (config.enableBottomProximitySensor) 
        {
            sensorModels[sensorIndex++] = new SensorModel(BOTTOM_PROXIMITY);
        }

        // Proximity Sensors
        if (config.enableProximitySensor45Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(45), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor90Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(90), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor135Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(135), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor180Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(180), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor225Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-45), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor270Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-90), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor315Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-135), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }
        if (config.enableProximitySensor360Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(0), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }


      
        // Ultrasonic Sensors
        if (config.enableUltrasonicSensor60Degrees) 
        {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(60), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }

        if (config.enableUltrasonicSensor120Degrees) 
        {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(120), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }
        
        if (config.enableUltrasonicSensor180Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(180), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }
        if (config.enableUltrasonicSensor240Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(-60), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }
        if (config.enableUltrasonicSensor300Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(-120), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }
        if (config.enableUltrasonicSensor360Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(0), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }

        // Colour Proximity Sensors
        if(config.enableColourProximitySensor180Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(COLOUR_PROXIMITY, (float) Math.toRadians(45), 0,
                    ColourProximitySensor.RANGE, ColourProximitySensor.FIELD_OF_VIEW);
        }

        if(config.enableColourProximitySensor360Degrees)
        {
            sensorModels[sensorIndex++] = new SensorModel(COLOUR_PROXIMITY, (float) Math.toRadians(0), 0,
                    ColourProximitySensor.RANGE, ColourProximitySensor.FIELD_OF_VIEW);
        }

        return sensorModels;
    }
}
