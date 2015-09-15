package za.redbridge.morphevo.mutate;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.genetic.genome.DoubleArrayGenome;
import org.encog.ml.genetic.mutate.MutatePerturb;

public class MorphMutatePerturb extends MutatePerturb{
    /**
     * The amount to perturb by.
     */
    private final double perturbAmount;

    /**
     * Construct a perturb mutation.
     * @param thePerturbAmount The amount to mutate by(percent).
     */
    public MorphMutatePerturb(final double thePerturbAmount) {
        super(thePerturbAmount);
        this.perturbAmount = thePerturbAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex,
            Genome[] offspring, int offspringIndex) {
        DoubleArrayGenome parent = (DoubleArrayGenome)parents[parentIndex];

        offspring[offspringIndex] = parent.getPopulation().getGenomeFactory().factor();
        DoubleArrayGenome child = (DoubleArrayGenome)offspring[offspringIndex];

        for(int i=0;i<parent.size();i++) {
            double value = parent.getData()[i];
            value += value * (perturbAmount - (rnd.nextDouble() * perturbAmount * 2));
            child.getData()[i] = clamp(value);
        }
    }

    public double clamp(double value){
        if(value > 1.0) return 1.0;
        if(value < -1.0) return -1.0;
        return value;
    }
}