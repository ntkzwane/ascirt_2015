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
 * Responsible for detecting level of cooperation required
 */
public class LowResCameraSensor extends AgentSensor
{
    private static final float LOWRES_SENSOR_RANGE = 3.0f;
    private static final float LOWRES_SENSOR_FOV = 1.5f; // This is a guess

    public static final float RANGE = 3.0f;
    public static final float FIELD_OF_VIEW = 1.5f; // This is a guess

    private static final Paint color = new Color(28, 0, 255, 138);
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

        //number of robots in an area including itself
        double numRobots = 1;

        //amount of cooperation required to move the resource/trash in the area
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

            double ratio;
            if(numRequired == 0 || numRobots > numRequired) ratio = 0;
            else ratio = numRobots/numRequired;

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
        return new ConePortrayal(range, fieldOfView, color);
    }
}
