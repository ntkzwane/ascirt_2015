package za.redbridge.controller.SANE;

import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.genome.ArrayGenome;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jae on 2015/09/10.
 * Genome for hidden layer neuron (SANE).
 */
public class NeuronGenome extends BasicGenome implements ArrayGenome, Serializable
{
    //array of connections
    private Connection[] chromosome;

    //list of fitness neuron has received
    private List<Double> fitnessList= new ArrayList<Double>();

    //list of children
    private List<NeuronGenome> children = new ArrayList<NeuronGenome>();

    //list of children
    private List<NeuronGenome> parents = new ArrayList<NeuronGenome>();


    //constructor - empty chromosome
    public NeuronGenome(int size)
    {
        chromosome = new Connection[size];
    }

    //constructor - receives chromosome
    public NeuronGenome(Connection[] chromosome)
    {
        this.chromosome = chromosome;
    }

    //copy constructor
    public NeuronGenome(NeuronGenome n)
    {
        this.chromosome = new Connection[n.chromosome.length];
        for (int i = 0; i < chromosome.length; i++)
        {
            this.chromosome[i] = new Connection(n.chromosome[i]);
        }
    }

    //replace the existing neuron with a new one - used in survivor selection
    public void replace(NeuronGenome other)
    {
        //reset
        this.clear_children();
        this.clear_fitness();

        //copy the connection chromosme
        for (int i = 0; i < SANE.CHROMOSOME_LENGTH; i++)
        {
            this.chromosome[i].set(other.chromosome[i]);
        }

        //assign parent-child relationship
        for (NeuronGenome parent : other.getParents())
        {
            parent.add_children(this);
        }
    }

    //initialize chromosome randomly
    public void randomInit()
    {
        Random random = new Random();

        //obtain unique random number by shuffling the list of unique numbers
        List<Integer> ints = new ArrayList<Integer>();
        for (int i = 0; i < SANE.IO_COUNT; i++) ints.add(i);
        Collections.shuffle(ints, random);

        //init chromosome
        for (int i = 0; i < chromosome.length; i++)
        {
            chromosome[i] = new Connection(ints.get(i), (random.nextDouble()*2) -1);
        }
    }

    @Override
    //copies connection object
    public void copy(ArrayGenome source, int sourceIndex, int targetIndex)
    {
        //creates copy of the source connection and copies it over
        this.chromosome[targetIndex] = new Connection(((NeuronGenome) source).chromosome[sourceIndex]);
    }

    @Override
    public void swap(int swap1, int swap2)
    {
        Connection temp = new Connection(chromosome[swap1]);
        chromosome[swap1].set(chromosome[swap2]);
        chromosome[swap2].set(temp);
    }

    @Override
    public void copy(Genome source)
    {
        NeuronGenome sourceNeuron = (NeuronGenome)source;
        for (int i = 0; i < chromosome.length; i++)
        {
            chromosome[i].set(sourceNeuron.chromosome[i]);
        }
        setScore(source.getScore());
        setAdjustedScore(source.getAdjustedScore());
    }

    @Override
    public int size()
    {
        return chromosome.length;
    }

    //returns data
    public Connection[] getChromosome()
    {
        return chromosome;
    }

    public void clear_children() {children.clear();}

    public void add_children(NeuronGenome n){children.add(n);}

    public List<NeuronGenome> getChildren(){return children;}

    public void clear_parents() {parents.clear();}

    public void add_parents(NeuronGenome n){parents.add(n);}

    public List<NeuronGenome> getParents(){return parents;}

    public void addScore(double score)
    {
        fitnessList.add(score);
    }

    public void clear_fitness()
    {
        fitnessList.clear();
    }

    public int get_participation()
    {
        return fitnessList.size();
    }
    public void finalizeScore()
    {
        double fitness = 0;

        //neuron hasnt participated in any network
        if (fitnessList.isEmpty())
        {
            fitness = 0;
        }

        //if less than five fitness
        else if (fitnessList.size() <= 5)
        {

            for (double f : fitnessList)
            {
                fitness += f;
            }
            fitness = fitness/fitnessList.size();

        }

        //if more than five fitness only select the best five
        else
        {
            Collections.sort(fitnessList);

            for (int i = fitnessList.size() - 1; i > fitnessList.size() - 6; i--)
            {
                fitness += fitnessList.get(i);
            }

            fitness = fitness/5;
        }

        setScore(fitness);
        setAdjustedScore(fitness);
    }
}
