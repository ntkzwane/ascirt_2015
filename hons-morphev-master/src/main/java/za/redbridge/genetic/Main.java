package za.redbridge.genetic;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.redbridge.genetic.MorphChromosome;
import za.redbridge.genetic.sensor.SensorMorphology;
import za.redbridge.simulator.config.SimConfig;

import static za.redbridge.genetic.Utils.isBlank;
import static za.redbridge.genetic.Utils.readObjectFromFile;

/**
 * morphology evolution initializer
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final double CONVERGENCE_SCORE = 110;
    public static void main(String[] args) throws IOException{
        Args options = new Args();
        new JCommander(options, args);

        log.info(options.toString());

        // initialize the simulator using a predefined config file
        SimConfig simConfig;
        if (!isBlank(options.configFile)) {
            simConfig = new SimConfig(options.configFile);
        } else {
            simConfig = new SimConfig();
        }

        // Load the morphology
        SensorMorphology morphology = new KheperaIIIMorphology();

        // create the score calculator object
        ScoreCalculator calculateScore =
            new ScoreCalculator(simConfig, options.simulationRuns, morphology);

        // demo the given configuration in the simulator
        if (!isBlank(options.genomePath)) {
//            NEATNetwork network = (NEATNetwork) readObjectFromFile(options.genomePath);
//            calculateScore.demo(network);
            MorphChromosome chromosome = (MorphChromosome) readObjectFromFile(options.genomePath);
//            calculateScore.demo(chromosome);//\TODO finish this
            return;
        }

        // initialize the genetic algorithm method and pupulation
//        final MorphologyEvolution morphevo = new MorphologyEvolution(new MorphGenomeFactory( ), calculateScore, options.populationSize);
        log.debug("Population initialized");


    }

    private static class Args {
        @Parameter(names = "-c", description = "Simulation config file to load")
        private String configFile = "config/mediumSimConfig.yml";

        @Parameter(names = "-i", description = "Number of simulation iterations to train for")
        private int numIterations = 500;

        @Parameter(names = "-p", description = "Initial population size")
        private int populationSize = 100;

        @Parameter(names = "--sim-runs", description = "Number of simulation runs per iteration")
        private int simulationRuns = 5;

        @Parameter(names = "--conn-density", description = "Adjust the initial connection density"
            + " for the population")
//        private double connectionDensity = 0.5; chuck : edit connection density
        private double connectionDensity = 1;
        @Parameter(names = "--demo", description = "Show a GUI demo of a given genome")
        private String genomePath = null;

        @Parameter(names = "--control", description = "Run with the control case")
        private boolean control = false;


        @Parameter(names = "--morphology", description = "For use with the control case, provide"
            + " the path to a serialized MMNEATNetwork to have its morphology used for the"
            + " control case")
        private String morphologyPath = null;

        @Parameter(names = "--population", description = "To resume a previous morphev, provide"
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