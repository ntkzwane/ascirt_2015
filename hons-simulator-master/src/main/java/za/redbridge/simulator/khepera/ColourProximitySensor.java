package za.redbridge.simulator.khepera;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import java.awt.Color;
import java.awt.Paint;
import za.redbridge.simulator.portrayal.ConePortrayal;
import za.redbridge.simulator.portrayal.Portrayal;

import za.redbridge.simulator.object.ResourceObject;
import za.redbridge.simulator.object.RobotObject;
import za.redbridge.simulator.object.WallObject;

import za.redbridge.simulator.sensor.AgentSensor;
import za.redbridge.simulator.sensor.sensedobjects.SensedObject;

/**
 * A rough estimation of the ultrasonic sensor used in the Khepera III robot: the Midas 400ST/R100
 * Ultrasonic sensors are characterised by a large range but with poor tracking of objects that
 * are close to the agent. They also have a relatively wide Field of View.
 * Created by jamie on 2014/09/23.
 */
public class ColourProximitySensor extends AgentSensor {

    private static final int readingSize = 1;

    private static final float COLOUR_PROXIMITY_RANGE = 4.0f; // 4 meters
    private static final float COLOUR_PROXIMITY_FOV = 1.22f; // 35 degrees

    public static final float RANGE = 4.0f; // 4 meters
    public static final float FIELD_OF_VIEW = 1.22f; // 35 degrees

    public ColourProximitySensor(float bearing, float orientation) {
        this(bearing, orientation, COLOUR_PROXIMITY_RANGE, COLOUR_PROXIMITY_FOV);
    }

    public ColourProximitySensor(float bearing, float orientation, float range, float fieldOfView) {
        super(bearing, orientation, range, fieldOfView);
    }

    @Override
    public AgentSensor clone() {
        return new ColourProximitySensor(bearing, orientation, range, fieldOfView);
    }

    @Override
    protected void provideObjectReading(List<SensedObject> objects, List<Double> output) {
        output.add(0.0);
        output.add(0.0);
        output.add(0.0);

        if (!objects.isEmpty()) {
            SensedObject closest = objects.get(0);
            double reading = 1 - Math.min(closest.getDistance() / range, 1.0);
            if(closest.getObject() instanceof RobotObject) output.set(0, reading);
            else output.set(0, 0.0);
            if(closest.getObject() instanceof ResourceObject) output.set(1, reading);
            else output.set(1, 0.0);
            if(closest.getObject() instanceof WallObject) output.set(2, reading);
            else output.set(2, 0.0);
        }
    }

    @Override
    protected Paint getPaint() {
        List<Double> readings = getPreviousReadings();
        if (readings.size() < 3) {
            return super.getPaint();
        }
        return new Color(readings.get(0).floatValue(), readings.get(1).floatValue(),
            readings.get(2).floatValue(), 0.5f);
    }

    protected double readingCurve(double fraction) {
        // Sigmoid proximity response
        final double offset = 0.5;
        return 1 / (1 + Math.exp(fraction + offset));
    }

    @Override
    public void readAdditionalConfigs(Map<String, Object> map) throws ParseException {
        additionalConfigs = map;
    }

    @Override
    public int getReadingSize() { return readingSize; }

    @Override
    public Map<String,Object> getAdditionalConfigs() { return additionalConfigs; }

}
