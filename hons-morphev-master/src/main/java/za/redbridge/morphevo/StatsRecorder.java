package za.redbridge.morphevo;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static za.redbridge.morphevo.Utils.getLoggingDirectory;
import static za.redbridge.morphevo.Utils.saveObjectToFile;

/**
 * Class for recording stats each epoch.
 *
 * Created by jamie on 2014/09/28.
 */
public class StatsRecorder {

    private static final Logger log = LoggerFactory.getLogger(StatsRecorder.class);

    private final EvolutionaryAlgorithm trainer;
    private final ScoreCalculator calculator;

    private Genome currentBestGenome;

    private Path rootDirectory;
    private Path populationDirectory;
    private Path bestChromosomeDirectory;

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

        bestChromosomeDirectory = rootDirectory.resolve("best chromosome");
        initDirectory(bestChromosomeDirectory);
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

        sensorStatsFile = rootDirectory.resolve("sensors.csv");
        initStatsFile(sensorStatsFile);
    }

    private static void initStatsFile(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset())) {
            writer.write("epoch, max, min, mean, standev\n");
        } catch (IOException e) {
            log.error("Unable to initialize stats file", e);
        }
    }

    public void recordIterationStats() {
        int epoch = trainer.getIteration();
        log.info("Epoch " + epoch + " complete");

        recordStats(calculator.getPerformanceStatistics(), epoch, performanceStatsFile);

        recordStats(calculator.getScoreStatistics(), epoch, scoreStatsFile);

        recordStats(calculator.getSensorStatistics(), epoch, sensorStatsFile);

       savePopulation((Population) trainer.getPopulation(), epoch);

        // Check if new best chromosome and save it if so
        MorphGenome newBestGenome = (MorphGenome) trainer.getBestGenome();
        if (newBestGenome != currentBestGenome) {
            saveGenome(newBestGenome, epoch);
            currentBestGenome = newBestGenome;
        }
    }

    private void savePopulation(Population population, int epoch) {
        String filename = "epoch-" + epoch + ".ser";
        Path path = populationDirectory.resolve(filename);
        saveObjectToFile(population, path);
    }

    private void saveGenome(MorphGenome genome, int epoch) {
        MorphChrom chromosome = decodeGenome(genome);

        Path directory = bestChromosomeDirectory.resolve("epoch-" + epoch);
        initDirectory(directory);

        String txt;
        log.info("New best genome! Epoch: " + epoch + ", score: " + genome.getScore());
        log.info("Sensors: "+ genome.getNumSensors() + ", Proximity: " + 
            chromosome.getNumProxiSensors() + ", Ultrasonic: " + chromosome.getNumUltraSensors());
        txt = String.format("epoch: %d, fitness: %f, sensors: %d, proximity: %d, ultrasonic: %d",
            epoch, genome.getScore(), genome.getNumSensors(), chromosome.getNumUltraSensors(), chromosome.getNumUltraSensors());
        Path txtPath = directory.resolve("info.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(txtPath, Charset.defaultCharset())) {
            writer.write(txt);
        } catch (IOException e) {
            log.error("Error writing best chromosome info file", e);
        }

        // save the current best chromosome object
        saveObjectToFile(chromosome, directory.resolve("chromosome.ser"));
    }

    private void recordStats(DescriptiveStatistics stats, int epoch, Path filepath) {
        double max = stats.getMax();
        double min = stats.getMin();
        double mean = stats.getMean();
        double sd = stats.getStandardDeviation();
        stats.clear();

        log.debug("Recording stats - max: " + max + ", mean: " + mean);
        saveStats(filepath, epoch, max, min, mean, sd);
    }

    private MorphChrom decodeGenome(Genome genome) {
        return (MorphChrom) trainer.getCODEC().decode(genome);
    }

    private static void saveStats(Path path, int epoch, double max, double min, double mean,
            double sd) {
        String line = String.format("%d, %f, %f, %f, %f\n", epoch, max, min, mean, sd);

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
