package za.redbridge.morphevo.mutate;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.genetic.genome.DoubleArrayGenome;

public class MorphMutateReplace implements EvolutionaryOperator{
    /**
     * mutate the genome by replacing a randomly chosen gene with a random value
     * within the gene bounds
     * {@inheritDoc}
     */
    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex,
                                 Genome[] offspring, int offspringIndex) {
        DoubleArrayGenome parent = (DoubleArrayGenome)parents[parentIndex];

        offspring[offspringIndex] = parent.getPopulation().getGenomeFactory().factor();
        DoubleArrayGenome child = (DoubleArrayGenome)offspring[offspringIndex];

        int mutatePosition = rnd.nextInt(parent.size());

        child.getData()[mutatePosition] = rnd.nextDouble() * 2 - 1;
    }

    /**
     * @return The number of offspring produced, which is 1 for this mutation.
     */
    @Override
    public int offspringProduced() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int parentsNeeded() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(EvolutionaryAlgorithm theOwner) {
        // not needed
    }
}