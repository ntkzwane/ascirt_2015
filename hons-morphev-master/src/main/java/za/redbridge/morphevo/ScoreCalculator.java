package za.redbridge.morphevo;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Override;
import java.lang.StackTraceElement;
import java.lang.System;
import java.util.concurrent.TimeUnit;

import sim.display.Console;

import za.redbridge.morphevo.sensor.SensorMorphology;
import za.redbridge.simulator.Simulation;
import za.redbridge.simulator.SimulationGUI;
import za.redbridge.simulator.config.SimConfig;
import za.redbridge.simulator.factories.HomogeneousRobotFactory;
import za.redbridge.simulator.factories.RobotFactory;
import za.redbridge.simulator.phenotype.Phenotype;

/**
 * coordinates the tests being run for the simulation
 */
public class ScoreCalculator implements CalculateScore{
    private static final Logger log = LoggerFactory.getLogger(ScoreCalculator.class);

    private final SimConfig simConfig;
    private final int simulationRuns;
    private final SensorMorphology sensorMorphology;//\TODO might not need this

    private final DescriptiveStatistics performanceStats = new SynchronizedDescriptiveStatistics();
    private final DescriptiveStatistics scoreStats = new SynchronizedDescriptiveStatistics();
    private final DescriptiveStatistics sensorStats;

    public ScoreCalculator(SimConfig simConfig, int simulationRuns,
                           SensorMorphology sensorMorphology) {
        this.simConfig = simConfig;
        this.simulationRuns = simulationRuns;
        this.sensorMorphology = sensorMorphology;

        // record the sensor stats
        this.sensorStats = new SynchronizedDescriptiveStatistics();
    }

    @Override
    public double calculateScore(MLMethod method){
        long start = System.nanoTime();

        MorphChrom chromosome = (MorphChrom) method;
        RobotFactory robotFactory = new HomogeneousRobotFactory(
            getPhenotypeForChromosome(chromosome),
            simConfig.getRobotMass(),
            simConfig.getRobotRadius(),
            simConfig.getRobotColour(),
            simConfig.getObjectsRobots()
        );

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);
        simulation.setStopOnceCollected(true);
        double fitness = 0;
        for (int i = 0; i < simulationRuns; i++) {
            simulation.run();
            fitness += simulation.getFitness().getTeamFitness();
            fitness += 20 * (1.0 - simulation.getProgressFraction()); // Time bonus
        }

        // Get the fitness and update the total score
        double score = fitness / simulationRuns;
        // add to score stats
        scoreStats.addValue(score);
        // add to sensor stats
        sensorStats.addValue(chromosome.encodedArrayLength( ));

        log.debug("Score calculation completed: " + score);

        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        performanceStats.addValue(duration);

        return score;
    }

    private Phenotype getPhenotypeForChromosome(MorphChrom morphChrom) {
        return new MorphPhenotype((MorphChrom) morphChrom);
    }

    public void demo(MLMethod method) {
        // Create the robot and resource factories
        MorphChrom chromosome = (MorphChrom) method;
        RobotFactory robotFactory = new HomogeneousRobotFactory(
                getPhenotypeForChromosome(chromosome),
                simConfig.getRobotMass(),
                simConfig.getRobotRadius(),
                simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);

        SimulationGUI video = new SimulationGUI(simulation);

        //new console which displays this simulation
        Console console = new Console(video);
        console.setVisible(true);
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }

    public DescriptiveStatistics getPerformanceStatistics() {
        return performanceStats;
    }

    public DescriptiveStatistics getScoreStatistics() {
        return scoreStats;
    }

    public DescriptiveStatistics getSensorStatistics() {
        return sensorStats;
    }
}
