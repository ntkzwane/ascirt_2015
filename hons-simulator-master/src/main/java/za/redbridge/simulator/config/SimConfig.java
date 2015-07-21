package za.redbridge.simulator.config;

import org.yaml.snakeyaml.Yaml;

import java.awt.Color;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import za.redbridge.simulator.factories.ConfigurableResourceFactory;
import za.redbridge.simulator.factories.ResourceFactory;

import javax.swing.*;

public class SimConfig extends Config {

    private static final long DEFAULT_SIMULATION_SEED = System.currentTimeMillis();
    private static final int DEFAULT_SIMULATION_ITERATIONS = 10000;
    private static final int DEFAULT_ENVIRONMENT_WIDTH = 20;
    private static final int DEFAULT_ENVIRONMENT_HEIGHT = 20;
    private static final int DEFAULT_TARGET_AREA_THICKNESS = (int)(DEFAULT_ENVIRONMENT_HEIGHT * 0.2);
    private static final Direction DEFAULT_TARGET_AREA_PLACEMENT = Direction.SOUTH;
    private static final int DEFAULT_OBJECTS_ROBOTS = 10;

    private static final float DEFAULT_ROBOT_MASS = 0.7f;
    private static final float DEFAULT_ROBOT_RADIUS = 0.15f;
    private static final Color DEFAULT_ROBOT_COLOUR = new Color(255,0,0);


    private static final ResourceFactory DEFAULT_RESOURCE_FACTORY = new ConfigurableResourceFactory();
    private static final String DEFAULT_ROBOT_FACTORY =
        "za.redbridge.simulator.factories.HomogeneousRobotFactory";

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    private long simulationSeed;
    private final int simulationIterations;

    private final int environmentWidth;
    private final int environmentHeight;

    private final int objectsRobots;
    private final float robotMass;
    private final float robotRadius;
    private final Color robotColour;

    private final Direction targetAreaPlacement;
    private final int targetAreaThickness;

    private ResourceFactory resourceFactory;
    private String robotFactoryName;

    public static String agentLocation = "";

    //default config
    public SimConfig() {
        this(DEFAULT_SIMULATION_SEED, DEFAULT_SIMULATION_ITERATIONS, DEFAULT_ENVIRONMENT_WIDTH,
            DEFAULT_ENVIRONMENT_HEIGHT, DEFAULT_TARGET_AREA_PLACEMENT,
            DEFAULT_TARGET_AREA_THICKNESS, DEFAULT_OBJECTS_ROBOTS, DEFAULT_ROBOT_MASS,
            DEFAULT_ROBOT_RADIUS, DEFAULT_ROBOT_COLOUR, DEFAULT_RESOURCE_FACTORY,
            DEFAULT_ROBOT_FACTORY);
    }

    public SimConfig(long simulationSeed, int simulationIterations, int environmentWidth,
                     int environmentHeight, Direction targetAreaPlacement, int targetAreaThickness,
                     int objectsRobots, float robotMass, float robotRadius, Color robotColour,
                     ResourceFactory resourceFactory, String robotFactoryName) {

        this.simulationSeed = simulationSeed;
        this.simulationIterations = simulationIterations;

        this.environmentWidth = environmentWidth;
        this.environmentHeight = environmentHeight;

        this.targetAreaPlacement = targetAreaPlacement;
        this.targetAreaThickness = targetAreaThickness;

        this.objectsRobots = objectsRobots;
        this.robotMass = robotMass;
        this.robotRadius = robotRadius;
        this.robotColour = robotColour;

        this.resourceFactory = resourceFactory;
        this.robotFactoryName = robotFactoryName;
    }

    @SuppressWarnings("unchecked")
    public SimConfig(String filepath) throws IOException{
        Yaml yaml = new Yaml();
        Map<String, Object> config = null;
        try (Reader reader = Files.newBufferedReader(Paths.get(filepath))) {
            config = (Map<String, Object>) yaml.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This is fairly horrible
        long seed = DEFAULT_SIMULATION_SEED;
        int iterations = DEFAULT_SIMULATION_ITERATIONS;
        int width = DEFAULT_ENVIRONMENT_WIDTH;
        int height = DEFAULT_ENVIRONMENT_HEIGHT;
        Direction placement = DEFAULT_TARGET_AREA_PLACEMENT;
        int thickness = DEFAULT_TARGET_AREA_THICKNESS;
        int robots = DEFAULT_OBJECTS_ROBOTS;

        float rMass = DEFAULT_ROBOT_MASS;
        float rRadius = DEFAULT_ROBOT_RADIUS;
        Color robotColour = DEFAULT_ROBOT_COLOUR;

        ResourceFactory resFactory = DEFAULT_RESOURCE_FACTORY;
        String robotFactory = DEFAULT_ROBOT_FACTORY;

        //get inputs from user
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        // Load simulation
        Map simulation = (Map) config.get("simulation");
        if (checkFieldPresent(simulation, "simulation")) {
            Number seedField = (Number) simulation.get("seed");
            if (checkFieldPresent(seedField, "simulation:seed")) {
                seed = seedField.longValue();
            }
            Integer iterationsField = (Integer) simulation.get("iterations");
            if (checkFieldPresent(iterationsField, "simulation:iterations")) {
                iterations = iterationsField;
            }
        }

        // Environment
        Map environment = (Map) config.get("environment");
        if (checkFieldPresent(environment, "environment")) {
            Integer widthField = (Integer) environment.get("width");
            if (checkFieldPresent(widthField, "environment:width")) {
                width = widthField;
            }
            Integer heightField = (Integer) environment.get("height");
            if (checkFieldPresent(heightField, "environment:height")) {
                height = heightField;
            }
        }

        // Target area
        Map targetArea = (Map) config.get("targetArea");
        if (checkFieldPresent(targetArea, "targetArea")) {
            String placementField = (String) targetArea.get("placement");
            if (checkFieldPresent(placementField, "targetArea:placement")) {
                placement = Direction.valueOf(placementField.toUpperCase());
            }
            Integer thicknessField = (Integer) targetArea.get("thickness");
            if (checkFieldPresent(thicknessField, "targetArea:thickness")) {
                thickness = thicknessField;
            }
        }

        // Robots
        Map bots = (Map) config.get("robots");
        if (checkFieldPresent(bots, "robots")) {


            boolean valid = false;
            while(!valid)
            {
                System.out.println("Enter number of robots (2 - 20)");
                robots = Integer.parseInt(inputReader.readLine());
                if(robots > 20 || robots <2)
                {
                    System.out.println("Please enter a number in range!");
                }
                else valid = true;
            }
//            Integer robotsField = (Integer) bots.get("numRobots");
//            if (checkFieldPresent(robotsField, "robots:numRobots")) {
//                robots = robotsField;
//            }
            Double botRadius = (Double) bots.get("radius");
            if (checkFieldPresent(botRadius, "robots:radius")) {
                rRadius = botRadius.floatValue();
            }
            Double botMass = (Double) bots.get("mass");
            if (checkFieldPresent(botMass, "robots:mass")) {
                rMass = botMass.floatValue();
            }

            String rgbvalues = (String) bots.get("colour");
            if (checkFieldPresent(rgbvalues, "robots:colour")) {

                String[] rgb = rgbvalues.split(",");
                robotColour = new Color(Integer.parseInt(rgb[0].trim()),
                    Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()));
            }

        }

        //factories
        Map factories = (Map) config.get("factories");
        if (checkFieldPresent(factories, "factories")) {

            String rFactory = (String) factories.get("resourceFactory");
            if (checkFieldPresent(resFactory, "factories:resourceFactory")) {

                try {
                    Class f = Class.forName(rFactory);
                    Object o = f.newInstance();

                    if (!(o instanceof ResourceFactory)) {
                        throw new InvalidClassException("");
                    }

                    System.out.println("Enter resource quantity separated by spaces (small medium large)");
                    String[] resQuantity = inputReader.readLine().split(" ");

                    resFactory = (ResourceFactory) o;
                    Map resources = (Map) config.get("resources");
                    resFactory.configure(resources,resQuantity);
                } catch (ClassNotFoundException c) {
                    System.out.println("Invalid class name specified in SimConfig: " + rFactory + ". Using default resource factory.");
                    c.printStackTrace();
                } catch (InvalidClassException i) {
                    System.out.println("Invalid resource factory specified: " + rFactory + ". Using default resource factory.");
                    i.printStackTrace();
                } catch (InstantiationException | IllegalAccessException ins) {
                    ins.printStackTrace();
                }
            }

            String robFactory = (String) factories.get("robotFactory");
            if (checkFieldPresent(robFactory, "factories:robotFactory")) {
                robotFactory = robFactory;
            }

            // select the starting position for the robots
            boolean valid = false;
            System.out.println("Enter agent start location NW, NE, SE, SW or leave blank for random starting points.");
            while(!valid) {
                agentLocation = inputReader.readLine().toLowerCase();
                String aLoc = agentLocation.toLowerCase();
                if(aLoc.equals("nw") || aLoc.equals("ne") || aLoc.equals("se") || aLoc.equals("sw") || aLoc.equals("")){
                    valid = true;
                    agentLocation = aLoc;
                }else{System.out.println("Enter a valid agent start location");}
            }

        }

        inputReader.close();
        this.simulationSeed = seed;
        this.simulationIterations = iterations;
        this.environmentWidth = width;
        this.environmentHeight = height;
        this.targetAreaPlacement = placement;
        this.targetAreaThickness = thickness;
        this.objectsRobots = robots;
        this.robotMass = rMass;
        this.robotRadius = rRadius;
        this.robotColour = robotColour;
        this.resourceFactory = resFactory;
        this.robotFactoryName = robotFactory;
    }



    public long getSimulationSeed() {
        return simulationSeed;
    }

    public void setSimulationSeed(long seed) {
        this.simulationSeed = seed;
    }

    public int getSimulationIterations() {
        return simulationIterations;
    }

    public int getEnvironmentWidth() {
        return environmentWidth;
    }

    public int getEnvironmentHeight() {
        return environmentHeight;
    }

    public Direction getTargetAreaPlacement() {
        return targetAreaPlacement;
    }

    public int getTargetAreaThickness() {
        return targetAreaThickness;
    }

    public int getObjectsRobots() {
        return objectsRobots;
    }

    public Color getRobotColour() { return robotColour; }

    public float getRobotMass() { return robotMass; }

    public float getRobotRadius() { return robotRadius; }

    public ResourceFactory getResourceFactory() { return resourceFactory; }

}
