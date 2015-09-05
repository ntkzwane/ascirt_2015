package za.redbridge.morphevo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.genetic.MLMethodGenome;
import org.encog.ml.genetic.MLMethodGenomeFactory;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import za.redbridge.morphevo.sensor.SensorMorphology;
import za.redbridge.simulator.config.SimConfig;

import static za.redbridge.morphevo.Utils.isBlank;
import static za.redbridge.morphevo.Utils.readObjectFromFile;

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

        // initialize the simulator using a predefined config file/set of configurations
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
           MorphChrom chromosome = (MorphChrom) readObjectFromFile(options.genomePath);
           calculateScore.demo(chromosome);
           return;
        }

        // initialize the genetic algorithm method and pupulation
        MorphologyEvolution morphevo = new MorphologyEvolution(new MethodFactory(){
            @Override
            public MLMethod factor() {
                System.out.println("Main.morphevo.factor( )");
                return new MorphChrom( );
            }}, calculateScore, options.populationSize);
        log.debug("Population of size " + options.populationSize + " initialized");

        int epoch = 0;
        for (int i = 0; i < options.numIterations; i++) {
            morphevo.iteration();
            log.info("Training step " + epoch);
            epoch++;
        }

        log.debug("Training complete");
        Encog.getInstance().shutdown();

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
                + "\tDemo network config path: " + genomePath + "\n"
                + "\tRunning with the control case: " + control + "\n"
                + "\tMorphology path: " + morphologyPath + "\n"
                + "\tPopulation path: " + populationPath;
        }
    }
}