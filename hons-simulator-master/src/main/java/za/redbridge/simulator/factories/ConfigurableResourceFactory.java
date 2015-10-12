package za.redbridge.simulator.factories;

import org.jbox2d.dynamics.World;
import za.redbridge.simulator.PlacementArea;
import za.redbridge.simulator.config.Config;
import za.redbridge.simulator.object.ResourceObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by shsu on 2014/08/29.
 */
public class ConfigurableResourceFactory extends Config implements ResourceFactory {

    private static final int DEFAULT_LARGE_QUANTITY = 10;
    private static final int DEFAULT_MEDIUM_QUANTITY = 15;
    private static final int DEFAULT_SMALL_QUANTITY = 15;

    private static final float DEFAULT_SMALL_WIDTH = 0.4f;
    private static final float DEFAULT_SMALL_HEIGHT = 0.4f;
    private static final float DEFAULT_SMALL_MASS = 1.0f;
    private static final int DEFAULT_SMALL_PUSHING_BOTS = 1;
    private static final double DEFAULT_SMALL_VALUE = 1;

    private static final float DEFAULT_MEDIUM_WIDTH = 0.6f;
    private static final float DEFAULT_MEDIUM_HEIGHT = 0.6f;
    private static final float DEFAULT_MEDIUM_MASS = 3.0f;
    private static final int DEFAULT_MEDIUM_PUSHING_BOTS = 2;
    private static final double DEFAULT_MEDIUM_VALUE = 2;

    private static final float DEFAULT_LARGE_WIDTH = 0.8f;
    private static final float DEFAULT_LARGE_HEIGHT = 0.8f;
    private static final float DEFAULT_LARGE_MASS = 5.0f;
    private static final int DEFAULT_LARGE_PUSHING_BOTS = 3;
    private static final double DEFAULT_LARGE_VALUE = 3;

    private ResourceSpec smallResourceSpec;
    private ResourceSpec mediumResourceSpec;
    private ResourceSpec largeResourceSpec;

    private ResourceSpec smallTrashSpec;
    private ResourceSpec mediumTrashSpec;
    private ResourceSpec largeTrashSpec;

    private static final double DEFAULT_TRASH_SMALL_VALUE = -1;
    private static final double DEFAULT_TRASH_MEDIUM_VALUE = -2;
    private static final double DEFAULT_TRASH_LARGE_VALUE = -3;

    public ConfigurableResourceFactory() {
        smallResourceSpec = new ResourceSpec(DEFAULT_SMALL_QUANTITY, DEFAULT_SMALL_WIDTH,
                DEFAULT_SMALL_HEIGHT, DEFAULT_SMALL_MASS, DEFAULT_SMALL_PUSHING_BOTS,
                DEFAULT_SMALL_VALUE);
        
        mediumResourceSpec = new ResourceSpec(DEFAULT_MEDIUM_QUANTITY, DEFAULT_MEDIUM_WIDTH,
                DEFAULT_MEDIUM_HEIGHT, DEFAULT_MEDIUM_MASS, DEFAULT_MEDIUM_PUSHING_BOTS,
                DEFAULT_MEDIUM_VALUE);
        
        largeResourceSpec = new ResourceSpec(DEFAULT_LARGE_QUANTITY, DEFAULT_LARGE_WIDTH,
                DEFAULT_LARGE_HEIGHT, DEFAULT_LARGE_MASS, DEFAULT_LARGE_PUSHING_BOTS,
                DEFAULT_LARGE_VALUE);
    }

    @Override
    public void placeInstances(PlacementArea.ForType<ResourceObject> placementArea, World world) {
        placeInstances(smallResourceSpec, placementArea, world);
        placeInstances(mediumResourceSpec, placementArea, world);
        placeInstances(largeResourceSpec, placementArea, world);
        placeInstances(smallTrashSpec, placementArea, world);
        placeInstances(mediumTrashSpec, placementArea, world);
        placeInstances(largeTrashSpec, placementArea, world);
    }

    private void placeInstances(ResourceSpec spec, PlacementArea.ForType<ResourceObject> placementArea, World world) {
        for (int i = 0; i < spec.quantity; i++) {
            PlacementArea.Space space =
                    placementArea.getRandomRectangularSpace(spec.width, spec.height);

            ResourceObject resource = new ResourceObject(world, space.getPosition(),
                    space.getAngle(), spec.width, spec.height, spec.mass, spec.pushingBots,
                    spec.value);

            placementArea.placeObject(space, resource);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, Object> resourceConfigs, String[] resQuantity) {


        Map<String, Object> smallConfig = (Map<String, Object>) resourceConfigs.get("small");
        Map<String, Object> mediumConfig = (Map<String, Object>) resourceConfigs.get("medium");
        Map<String, Object> largeConfig = (Map<String, Object>) resourceConfigs.get("large");

        smallResourceSpec = readConfig(smallConfig, "resourceConfig:small:", DEFAULT_SMALL_VALUE, Integer.parseInt(resQuantity[0]));
        mediumResourceSpec =
                readConfig(mediumConfig, "resourceConfig:medium:", DEFAULT_MEDIUM_VALUE, Integer.parseInt(resQuantity[1]));
        largeResourceSpec = readConfig(largeConfig, "resourceConfig:large:", DEFAULT_LARGE_VALUE, Integer.parseInt(resQuantity[2]));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure_trash(Map<String, Object> resourceConfigs, String[] trashQuantity) {


        Map<String, Object> smallConfig = (Map<String, Object>) resourceConfigs.get("small");
        Map<String, Object> mediumConfig = (Map<String, Object>) resourceConfigs.get("medium");
        Map<String, Object> largeConfig = (Map<String, Object>) resourceConfigs.get("large");

        smallTrashSpec = readConfig(smallConfig, "resourceConfig:small:", DEFAULT_TRASH_SMALL_VALUE, Integer.parseInt(trashQuantity[0]));
        mediumTrashSpec =
                readConfig(mediumConfig, "resourceConfig:medium:", DEFAULT_TRASH_MEDIUM_VALUE, Integer.parseInt(trashQuantity[1]));
        largeTrashSpec = readConfig(largeConfig, "resourceConfig:large:", DEFAULT_TRASH_LARGE_VALUE, Integer.parseInt(trashQuantity[2]));

    }
    
    private ResourceSpec readConfig(Map<String, Object> config, String path, double value, int q) {
        int quantity = q;
        int pushingBots = 0;
        float width = 0;
        float height = 0;
        float mass = 0;

//        Number quantityField = (Number) config.get("quantity");
//        if (checkFieldPresent(quantityField, path + "quantity")) {
//            quantity = quantityField.intValue();
//        }
        
        Number widthField = (Number) config.get("width");
        if (checkFieldPresent(widthField, path + "width")) {
            width = widthField.floatValue();
        }
        
        Number heightField = (Number) config.get("height");
        if (checkFieldPresent(heightField, path + "height")) {
            height = heightField.floatValue();
        }
        
        Number massField = (Number) config.get("mass");
        if (checkFieldPresent(heightField, path + "mass")) {
            mass = massField.floatValue();
        }
        
        Number pushingBotsField = (Number) config.get("pushingBots");
        if (checkFieldPresent(pushingBotsField, path + "pushingBots")) {
            pushingBots = pushingBotsField.intValue();
        }
        
        return new ResourceSpec(quantity, width, height, mass, pushingBots, value);
    }

    @Override
    public int getNumberOfResources() {
        return smallResourceSpec.quantity + mediumResourceSpec.quantity
                + largeResourceSpec.quantity;
    }

    @Override
    public double getTotalResourceValue(){
        return smallResourceSpec.getTotalValue() + mediumResourceSpec.getTotalValue() +
                largeResourceSpec.getTotalValue();
    }


    private static class ResourceSpec {
        private final int quantity;
        private final float width;
        private final float height;
        private final float mass;
        private final int pushingBots;
        private final double value;

        ResourceSpec(int quantity, float width, float height, float mass, int pushingBots,
                double value) {
            this.quantity = quantity;
            this.width = width;
            this.height = height;
            this.mass = mass;
            this.pushingBots = pushingBots;
            this.value = value;
        }

        double getTotalValue() {
            return quantity * value;
        }
    }
}
