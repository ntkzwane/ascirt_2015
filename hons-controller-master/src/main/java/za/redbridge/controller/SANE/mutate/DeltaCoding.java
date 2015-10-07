package za.redbridge.controller.SANE.mutate;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import za.redbridge.controller.SANE.BlueprintGenome;
import za.redbridge.controller.SANE.NeuronGenome;
import za.redbridge.controller.SANE.SANE;

import java.util.*;

/**
 * Created by Jae on 2015-10-06.
 * Delta coding to be used if the evolution stagnates for a set generation
 */
public class DeltaCoding implements EvolutionaryOperator
{

    /**
     * The owner.
     */
    private EvolutionaryAlgorithm owner;

    @Override
    public void init(EvolutionaryAlgorithm evolutionaryAlgorithm)
    {
        owner = evolutionaryAlgorithm;
    }

    @Override
    public int offspringProduced()
    {
        return this.owner.getPopulation().getSpecies().get(0).getOffspringCount();
    }

    @Override
    public int parentsNeeded()
    {
        return 2;
    }

    @Override
    public void performOperation(Random random, Genome[] blueprints, int parentIndex,Genome[] neurons, int offspringIndex)
    {
        double elite_percent = 0.1;
        int elite_count = (int) (blueprints.length * elite_percent);

        //modified version of the best neurons
        List<NeuronGenome> new_neurons = new ArrayList<>();
        List<BlueprintGenome> new_blueprints = new ArrayList<>();

        //for each of the elite genomes
        for (int i = 0; i < elite_count; i++)
        {
            //create 9 copies of its modification
            for (int j = 0; j < 9; j++)
            {
                //create copy of the blueprint
                BlueprintGenome blueprint = new BlueprintGenome((BlueprintGenome)blueprints[i]);
                new_blueprints.add(blueprint);

                //for each neurons in the blueprint
                for (int n = 0; n < blueprint.getBlueprint().length; n++)
                {
                    //create copy of the neuron, so the original neuron is not modified
                    NeuronGenome neuron = new NeuronGenome(blueprint.getBlueprint()[n]);

                    //switch reference to new neuron
                    blueprint.getBlueprint()[n] = neuron;

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

                    //weight mutated by multiplying with value between -2 and 2;
                    double new_weight = neuron.getChromosome()[i].getWeight() * ((random.nextDouble() * 4) - 2);

                    //update information
                    neuron.getChromosome()[i].set(new_label, new_weight);

                    //add the new neurons to the list so it can be introduced into the population later
                    new_neurons.add(neuron);
                }
            }
        }

    }
}
