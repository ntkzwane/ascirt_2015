package za.redbridge.simulator.phenotype.heuristics;

import java.awt.Color;
import java.util.List;
import java.util.Optional;

import sim.util.Double2D;
import za.redbridge.simulator.object.RobotObject;
import za.redbridge.simulator.sensor.ClosestObjectSensor;
import za.redbridge.simulator.sensor.CollisionSensor;
import za.redbridge.simulator.sensor.Sensor;

import za.redbridge.simulator.object.ResourceObject;

import static za.redbridge.simulator.Utils.jitter;

/**
 * Created by racter on 2014/09/01.
 */
public class CollisionAvoidanceHeuristic extends Heuristic {

    private static final Color COLOR = Color.RED;

    protected final CollisionSensor collisionSensor;

    public CollisionAvoidanceHeuristic(CollisionSensor collisionSensor, RobotObject robot) {
        super(robot);
        this.collisionSensor = collisionSensor;

        setPriority(4);
    }

    @Override
    public Double2D step(List<List<Double>> list) {
        Optional<ClosestObjectSensor.ClosestObject> collision = collisionSensor.sense();

        ResourceObject resource =
            collisionSensor.sense().map(o -> (ResourceObject) o.getObject()).orElse(null);
        if (resource == null || !resource.canBePickedUp()) {
            return collision.map(o -> o.getVectorToObject())
                .map(o -> wheelDriveForTargetPosition(jitter(o.negate(), 0.2f)))
                .orElse(null);
        }
        return null;
    }

    @Override
    Color getColor() {
        return COLOR;
    }

    @Override
    public Sensor getSensor() {
        return collisionSensor;
    }

}
