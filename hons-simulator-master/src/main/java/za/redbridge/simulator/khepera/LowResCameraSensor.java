package za.redbridge.simulator.khepera;

import za.redbridge.simulator.object.ResourceObject;
import za.redbridge.simulator.object.RobotObject;
import za.redbridge.simulator.object.WallObject;
import za.redbridge.simulator.portrayal.ConePortrayal;
import za.redbridge.simulator.portrayal.Portrayal;
import za.redbridge.simulator.sensor.AgentSensor;
import za.redbridge.simulator.sensor.sensedobjects.SensedObject;

import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by jae on 2015/10/10.
 * Responsible for detecting level of cooperation
 */
public class LowResCameraSensor extends AgentSensor
{
    private static final float LOWRES_SENSOR_RANGE = 3.0f;
    private static final float LOWRES_SENSOR_FOV = 1.0f; // This is a guess

    public static final float RANGE = 3.0f;
    public static final float FIELD_OF_VIEW = 1.0f; // This is a guess

    //constructor
    public LowResCameraSensor(float bearing, float orientation)
    {
        this(bearing, orientation, LOWRES_SENSOR_RANGE, LOWRES_SENSOR_FOV);
    }

    public LowResCameraSensor(float bearing, float orientation, float range, float fieldOfView)
    {
        super(bearing, orientation, range, fieldOfView);
    }

    @Override
    protected void provideObjectReading(List<SensedObject> sensedObjects, List<Double> output)
    {
        //returns the ratio between robots and the mass of resources to determine required level of cooperation
        double numRobots = 0;
        double numRequired = 0;
        if (!sensedObjects.isEmpty())
        {
            for (SensedObject object : sensedObjects)
            {
                if (object.getObject() instanceof RobotObject)
                {
                    numRobots++;
                }
                else if (object.getObject() instanceof ResourceObject)
                {
                    numRequired += ((ResourceObject) object.getObject()).getSize();
                }
            }

            double ratio = numRequired/numRobots;
            if(ratio > 1) ratio = 1;
            output.add(ratio);
        }
        else
        {
            output.add(0.0);
        }
    }

    @Override
    public void readAdditionalConfigs(Map<String, Object> map) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AgentSensor clone()
    {
        return new LowResCameraSensor(bearing, orientation, range, fieldOfView);
    }

    @Override
    public int getReadingSize()
    {
        return 1;
    }

    @Override
    public Map<String, Object> getAdditionalConfigs()
    {
        return null;
    }
    @Override
    protected Portrayal createPortrayal() {
        return new ConePortrayal(range, fieldOfView, new Color(116, 0, 255, 112));
    }
}
