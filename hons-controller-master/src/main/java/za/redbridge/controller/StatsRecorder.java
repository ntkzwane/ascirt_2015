package za.redbridge.controller;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.networks.BasicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.redbridge.controller.SANE.BlueprintGenome;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;



import static za.redbridge.controller.Utils.getLoggingDirectory;
import static za.redbridge.controller.Utils.saveObjectToFile;

/**
 * Class for recording stats each generation.
 *
 * Created by jamie on 2014/09/28.
 */
public class StatsRecorder {

    private static final Logger log = LoggerFactory.getLogger(StatsRecorder.class);

    private final EvolutionaryAlgorithm trainer;
    private final ScoreCalculator calculator;

    //private Genome currentBestGenome;
    private double currentBestScore = 0;
    private Path rootDirectory;
    private Path populationDirectory;
    private Path bestNetworkDirectory;

    private Path performanceStatsFile;
    private Path scoreStatsFile;
    private Path sensorStatsFile;

    public StatsRecorder(EvolutionaryAlgorithm trainer, ScoreCalculator calculator) {
        this.trainer = trainer;
        this.calculator = calculator;

        initFiles();
    }

    private void initFiles() {
        initDirectories();
        initStatsFiles();
    }

    private void initDirectories() {
        rootDirectory = getLoggingDirectory();
        initDirectory(rootDirectory);

        populationDirectory = rootDirectory.resolve("populations");
        initDirectory(populationDirectory);

        bestNetworkDirectory = rootDirectory.resolve("best networks");
        initDirectory(bestNetworkDirectory);
    }

    private static void initDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("Unable to create directories", e);
        }
    }

    private void initStatsFiles() {
        performanceStatsFile = rootDirectory.resolve("performance.csv");
        initStatsFile(performanceStatsFile);

        scoreStatsFile = rootDirectory.resolve("scores.csv");
        initStatsFile(scoreStatsFile);

    }

    private static void initStatsFile(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset())) {
            writer.write("gemeratopm, max, min, mean, standev\n");
        } catch (IOException e) {
            log.error("Unable to initialize stats file", e);
        }
    }

    public void recordIterationStats() {
        int generation = trainer.getIteration();
        log.info("generation " + generation + " complete");

        recordStats(calculator.getPerformanceStatistics(), generation, performanceStatsFile);

        recordStats(calculator.getScoreStatistics(), generation, scoreStatsFile);


        savePopulation((Population) trainer.getPopulation(), generation);

        // Check if new best network and save it if so
        BlueprintGenome newBestGenome = (BlueprintGenome) trainer.getBestGenome();
        if (newBestGenome.getScore() >= currentBestScore) {
            saveGenome(newBestGenome, generation);
            currentBestScore = newBestGenome.getScore();
        }
    }

    private void savePopulation(Population population, int generation) {
        String filename = "generation-" + generation + ".ser";
        Path path = populationDirectory.resolve(filename);
        saveObjectToFile(population, path);
    }

    private void saveGenome(BlueprintGenome genome, int generation) {
        Path directory = bestNetworkDirectory.resolve("generation-" + generation);
        initDirectory(directory);

        String txt;

        log.info("New best genome! generation: " + generation + ", score: "  + genome.getScore());
        txt = String.format("generation: %d, fitness: %f", generation, genome.getScore());

        Path txtPath = directory.resolve("info.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(txtPath, Charset.defaultCharset())) {
            writer.write(txt);
        } catch (IOException e) {
            log.error("Error writing best network info file", e);
        }

        BasicNetwork network = decodeGenome(genome);
        saveObjectToFile(network, directory.resolve("network.ser"));

        //GraphvizEngine.saveGenome(genome, directory.resolve("graph.dot"));
    }

    private void recordStats(DescriptiveStatistics stats, int generation, Path filepath) {
        double max = stats.getMax();
        double min = stats.getMin();
        double mean = stats.getMean();
        double sd = stats.getStandardDeviation();
        stats.clear();

        log.debug("Recording stats - max: " + max + ", mean: " + mean);
        saveStats(filepath, generation, max, min, mean, sd);
    }

    private BasicNetwork decodeGenome(Genome genome) {
        return (BasicNetwork) trainer.getCODEC().decode(genome);
    }

    private static void saveStats(Path path, int generation, double max, double min, double mean,
            double sd) {
        String line = String.format("%d, %f, %f, %f, %f\n", generation, max, min, mean, sd);

        final OpenOption[] options = {
                StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE
        };
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path,
                Charset.defaultCharset(), options))) {
            writer.append(line);
        } catch (IOException e) {
            log.error("Failed to append to log file", e);
        }
    }

}
