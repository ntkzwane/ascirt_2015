package za.redbridge.controller.SANE.mutate;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import za.redbridge.controller.SANE.NeuronGenome;
import za.redbridge.controller.SANE.SANE;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by jae on 2015/09/11.
 * Mutates the label and weights of connections in neuron genomes
 */
public class NeuronMutate implements EvolutionaryOperator
{

    /**
     * The owner.
     */
    private EvolutionaryAlgorithm owner;

    //chance of mutation occurring
    private double mutation_rate;

    public NeuronMutate(double d)
    {
        mutation_rate = d;
    }

    @Override
    public void init(EvolutionaryAlgorithm evolutionaryAlgorithm)
    {
        owner = evolutionaryAlgorithm;
    }

    @Override
    public int offspringProduced()
    {
        return 1;
    }

    @Override
    public int parentsNeeded()
    {
        return 1;
    }

    @Override
    public void performOperation(Random random, Genome[] parents, int parentIndex,
                                 Genome[] offspring, int offspringIndex)
    {
        //candidate neuron for mutation
        NeuronGenome neuron = (NeuronGenome)offspring[offspringIndex];

        //perform mutation for each connection gene
        for (int i = 0; i < SANE.CHROMOSOME_LENGTH; i++)
        {
            if(random.nextDouble() <= mutation_rate)
            {
                //obtain unique random number by shuffling the list of unique numbers
                Set<Integer> ints = new HashSet<Integer>();

                //add all the labels in the chromosome
                for (int k = 0; k < SANE.CHROMOSOME_LENGTH; k++)
                {
                    if (k != i) ints.add(neuron.getChromosome()[k].getLabel());
                }

                //mutate connection label
                int new_label = random.nextInt(SANE.IO_COUNT);
                while (ints.contains(new_label))
                {
                    new_label = random.nextInt(SANE.IO_COUNT);
                }

                double num = random.nextDouble();
                double new_weight;
                if (num + neuron.getChromosome()[i].getWeight() > 1 || num + neuron.getChromosome()[i].getWeight() < -1)
                {
                    new_weight = num - neuron.getChromosome()[i].getWeight();
                }
                else new_weight = num + neuron.getChromosome()[i].getWeight();

                //update information
                neuron.getChromosome()[i].set(new_label, new_weight);
            }
        }
    }
}
