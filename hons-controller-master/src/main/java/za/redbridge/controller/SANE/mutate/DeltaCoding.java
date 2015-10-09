package za.redbridge.controller.SANE.mutate;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.sort.SortGenomesForSpecies;
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

    private EvolutionaryOperator neuron_mutation;

    @Override
    public void init(EvolutionaryAlgorithm evolutionaryAlgorithm)
    {
        owner = evolutionaryAlgorithm;
        neuron_mutation = new NeuronMutate(0.1);
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

    }

    public void performOperation(Random random, List<Genome> blueprints, int parentIndex,List<Genome> neurons, int offspringIndex)
    {
        double elite_percent = 0.1;
        int elite_count = (int) (neurons.size() * elite_percent);

        //modified version of the best neurons
        List<Genome> new_neurons = new ArrayList<>();
        List<Genome> new_blueprints = new ArrayList<>();

        double threshold_score = blueprints.get(elite_count-1).getScore();

        //for each of the elite genomes
        for (int i = 0; i < elite_count; i++)
        {
            //elite neuron genome
            NeuronGenome neuron = (NeuronGenome) neurons.get(i);

            //create 9 copies of its modification
            for (int j = 0; j < 9; j++)
            {
                //create copy of the neuron, so the original neuron is not modified
                NeuronGenome new_neuron = new NeuronGenome(neuron);

            }
        }
    }
}
