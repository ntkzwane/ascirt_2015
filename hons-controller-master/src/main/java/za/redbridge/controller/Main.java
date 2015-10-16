package za.redbridge.controller;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.redbridge.controller.NEAT.NEATPopulation;
import za.redbridge.controller.NEAT.NEATUtil;

import java.io.IOException;

import za.redbridge.controller.NEATM.sensor.SensorMorphology;
import za.redbridge.controller.SANE.SANE;
import za.redbridge.controller.SANE.SANEControllerEvolution;
import za.redbridge.simulator.config.SimConfig;
import za.redbridge.simulator.khepera.KheperaIIIPhenotype_simple;


import static za.redbridge.controller.Utils.isBlank;
import static za.redbridge.controller.Utils.readObjectFromFile;

/**
 * Entry point for the controller platform.
 *
 * Created by jamie on 2014/09/09.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final double CONVERGENCE_SCORE = 110;

    private static int thread_count = 1;
    public static boolean NEAT_EVOLUTION;
    public static String RES_CONFIG;
    public static void main(String[] args) throws IOException {
        Args options = new Args();
        new JCommander(options, args);

        log.info(options.toString());

        SimConfig simConfig;
        if (!isBlank(options.configFile)) {
            simConfig = new SimConfig(options.configFile);
        } else {
            simConfig = new SimConfig();
        }

        KheperaIIIPhenotype_simple.Configuration morphology_config = new KheperaIIIPhenotype_simple.Configuration();;
        morphology_config.enableProximitySensors40Degrees = true;
        morphology_config.enableProximitySensors140Degrees = true;
        morphology_config.enableProximitySensor180Degrees = true;
        morphology_config.enableUltrasonicSensor0Degrees = true;
        morphology_config.enableUltrasonicSensors90Degrees = true;
        morphology_config.enableProximitySensorBottom = true;

        morphology_config.enableColourRangedSensor = true;
        morphology_config.enableLowResCameraSensor = true;

        // Load the morphology
        SensorMorphology morphology = new KheperaIIIMorphology(morphology_config);

        System.out.println("Sensors count :" + morphology.getNumSensors());
        NEAT_EVOLUTION = options.control;
        RES_CONFIG = options.environment;

        //NEAT
        if (NEAT_EVOLUTION)
        {
            System.out.println("Running NEAT");
            ScoreCalculator calculateScore =
                    new ScoreCalculator(simConfig, options.simulationRuns, morphology);

            if (!isBlank(options.genomePath)) {
                NEATNetwork network = (NEATNetwork) readObjectFromFile(options.genomePath);
                calculateScore.demo(network);
                return;
            }

            final NEATPopulation population;

            population = new NEATPopulation(morphology.getNumSensors(), 2, options.populationSize);
            population.setInitialConnectionDensity(options.connectionDensity);
            population.reset();

            log.debug("Population initialized : "+options.populationSize);


            TrainEA train;
            train = NEATUtil.constructNEATTrainer(population, calculateScore);
            if (thread_count > 0) {
                train.setThreadCount(thread_count);
            }
            final StatsRecorder statsRecorder = new StatsRecorder(train, calculateScore);
            statsRecorder.recordIterationStats();
            for (int i = train.getIteration(); i < options.numIterations; i++) {
                train.iteration();
                statsRecorder.recordIterationStats();

                if (train.getBestGenome().getScore() >= CONVERGENCE_SCORE) {
                    log.info("Convergence reached at epoch " + train.getIteration());
                    break;
                }
            }
        }
        //SANE
        else
        {
            System.out.println("Running SANE");
            //initialize parameters
            int hiddenSize = 8;
            int outputSize = 2;
            SANE.init(morphology.getNumSensors(), hiddenSize, outputSize);

            System.out.println("Network topology");
            System.out.println("Input size :" + morphology.getNumSensors());
            System.out.println("Hidden size :" + hiddenSize);
            System.out.println("Output size :" + outputSize);
            ScoreCalculator calculateScore =
                    new ScoreCalculator(simConfig, options.simulationRuns, morphology);

            if (!isBlank(options.genomePath))
            {
                //BlueprintGenome gen = (BlueprintGenome)readObjectFromFile(options.genomePath);
                BasicNetwork network = (BasicNetwork) readObjectFromFile(options.genomePath);
                calculateScore.demo(network);
                return;
            }

            int neuron_population_size = 800;
            int blueprint_population_size = 100;

            SANEControllerEvolution sane = new SANEControllerEvolution(new MethodFactory()
            {
                @Override
                public MLMethod
                factor()
                {
                    System.out.println("Stub");
                    return null;
                }
            },
                    calculateScore, neuron_population_size, blueprint_population_size);

            log.debug("Neuron Population of size " + neuron_population_size + " initialized");
            log.debug("Blueprint Population of size " + blueprint_population_size + " initialized");

            if (thread_count > 0) {
                sane.getGenetic().setThreadCount(thread_count);
            }

            final StatsRecorder statsRecorder = new StatsRecorder(sane.getGenetic(), calculateScore);
            statsRecorder.recordIterationStats();

            for (int i = 0; i < options.numIterations; i++)
            {
                sane.iteration();
                statsRecorder.recordIterationStats();

                if (sane.getGenetic().getBestGenome().getScore() >= CONVERGENCE_SCORE) {
                    log.info("Convergence reached at epoch " + sane.getGenetic().getIteration());
                    break;
                }
            }
        }


        log.debug("Training complete");
        Encog.getInstance().shutdown();
    }

    private static class Args {
        @Parameter(names = "-c", description = "Simulation config file to load")
        private String configFile = "config/mediumSimConfig.yml";

        @Parameter(names = "-i", description = "Number of simulation iterations to train for")
        private int numIterations = 250;

        @Parameter(names = "-p", description = "Initial population size")
        private int populationSize = 100;

        @Parameter(names = "--sim-runs", description = "Number of simulation runs per iteration")
        private int simulationRuns = 5;

        @Parameter(names = "--conn-density", description = "Adjust the initial connection density"
                + " for the population")
        private double connectionDensity = 0.5;
        @Parameter(names = "--demo", description = "Show a GUI demo of a given genome")
        private String genomePath = null;

        @Parameter(names = "--control", description = "Run with the control case")
        private boolean control = false;

        @Parameter(names = "--advanced", description = "Run with advanced envrionment and morphology")
        private boolean advanced = true;

        @Parameter(names = "--environment", description = "Run with advanced envrionment and morphology")
        private String environment = "";

        @Parameter(names = "--morphology", description = "For use with the control case, provide"
                + " the path to a serialized MMNEATNetwork to have its morphology used for the"
                + " control case")
        private String morphologyPath = null;

        @Parameter(names = "--population", description = "To resume a previous controller, provide"
                + " the path to a serialized population")
        private String populationPath = null;

        @Override
        public String toString() {
            return "Options: \n"
                    + "\tConfig file path: " + configFile + "\n"
                    + "\tNumber of simulation steps: " + numIterations + "\n"
                    + "\tPopulation size: " + populationSize + "\n"
                    + "\tNumber of simulation tests per iteration: " + simulationRuns + "\n"
                    + "\tInitial connection density: " + connectionDensity + "\n"
                    + "\tDemo network config path: " + genomePath + "\n"
                    + "\tRunning with the control case: " + control + "\n"
                    + "\tMorphology path: " + morphologyPath + "\n"
                    + "\tPopulation path: " + populationPath;
        }
    }
}
