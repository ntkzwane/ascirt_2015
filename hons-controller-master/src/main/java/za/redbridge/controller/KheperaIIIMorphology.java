package za.redbridge.controller;

import za.redbridge.controller.NEATM.sensor.SensorModel;
import za.redbridge.controller.NEATM.sensor.SensorMorphology;
import za.redbridge.simulator.khepera.*;

import static za.redbridge.controller.NEATM.sensor.SensorType.*;

/**
 * A horrible adapter class for different representations of morphologies. Creates a
 * {@link SensorMorphology} for a {@link KheperaIIIPhenotype_simple.Configuration}.
 *
 * Created by jamie on 2014/10/06.
 */
public class KheperaIIIMorphology extends SensorMorphology {

    private static final long serialVersionUID = 8121207679231125300L;

    private static final KheperaIIIPhenotype_simple.Configuration DEFAULT_CONFIGURATION = new KheperaIIIPhenotype_simple.Configuration();
    static {
/*        DEFAULT_CONFIGURATION.enableProximitySensorBottom = true;
        DEFAULT_CONFIGURATION.enableProximitySensors10Degrees = true;*/
        DEFAULT_CONFIGURATION.enableProximitySensors40Degrees = true;
       // DEFAULT_CONFIGURATION.enableProximitySensors75Degrees = true;
        DEFAULT_CONFIGURATION.enableProximitySensors140Degrees = true;
        DEFAULT_CONFIGURATION.enableProximitySensor180Degrees = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensor0Degrees = true;
        //DEFAULT_CONFIGURATION.enableUltrasonicSensors40Degrees = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensors90Degrees = true;
        DEFAULT_CONFIGURATION.enableColourProximitySensor=false;
        //DEFAULT_CONFIGURATION.enableColourRangedSensor = true;
        //DEFAULT_CONFIGURATION.enableLowResCameraSensor = true;
    }

    public KheperaIIIMorphology() {
        this(DEFAULT_CONFIGURATION);
    }

    public KheperaIIIMorphology(KheperaIIIPhenotype_simple.Configuration config) {
        super(createSensorModels(config));
    }

    private static SensorModel[] createSensorModels(KheperaIIIPhenotype_simple.Configuration config) {
        int sensorIndex = 0;
        final int sensorCount = config.getNumberOfSensors();
        SensorModel[] sensorModels = new SensorModel[sensorCount];

        if (config.enableProximitySensorBottom) {
            sensorModels[sensorIndex++] = new SensorModel(BOTTOM_PROXIMITY);
        }

        if (config.enableProximitySensors10Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(10), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);

            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-10), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }

        if (config.enableProximitySensors40Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(40), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);

            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-40), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }

        if (config.enableProximitySensors75Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(75), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);

            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-75), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }

        if (config.enableProximitySensors140Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(140), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);

            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(-140),
                    0, ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }

        if (config.enableProximitySensor180Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(PROXIMITY, (float) Math.toRadians(180), 0,
                    ProximitySensor.RANGE, ProximitySensor.FIELD_OF_VIEW);
        }

        if (config.enableUltrasonicSensor0Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, 0, 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }

        if (config.enableUltrasonicSensors40Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(40), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);

            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(-40),
                    0, UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }

        if (config.enableUltrasonicSensors90Degrees) {
            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(90), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);

            sensorModels[sensorIndex++] = new SensorModel(ULTRASONIC, (float) Math.toRadians(-90), 0,
                    UltrasonicSensor.RANGE, UltrasonicSensor.FIELD_OF_VIEW);
        }

        if (config.enableColourRangedSensor)
        {
            sensorModels[sensorIndex++] = new SensorModel(COLOURRANGED, (float) Math.toRadians(15), 0,
                    ColourRangedSensor.RANGE, ColourRangedSensor.FIELD_OF_VIEW);
        }

        if (config.enableLowResCameraSensor)
        {
            sensorModels[sensorIndex] = new SensorModel(LOWRESCAMERA, (float) Math.toRadians(-15), 0,
                    LowResCameraSensor.RANGE, LowResCameraSensor.FIELD_OF_VIEW);
        }

        return sensorModels;
    }
}
