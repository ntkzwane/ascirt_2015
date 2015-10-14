package za.redbridge.controller.SANE;

import org.encog.EncogError;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.species.Species;
import za.redbridge.controller.SANE.mutate.BlueprintMutateSwitchToRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * A worker thread for an Evolutionary Algorithm.
 */
public class BlueprintWorker implements Callable<Object>
{

    /**
     * The species being processed.
     */
    private final Species species;

    /**
     * The parent genomes.
     */
    private final Genome[] parents;

    /**
     * The children genomes.
     */
    private final Genome[] children;

    /**
     * Random number generator.
     */
    private final Random rnd;

    /**
     * The parent object.
     */
    private final BasicSANE train;

    private Genome eliteGenome;

    private boolean elite_mode = false;

    /**
     * Construct the EA worker.
     *
     * @param theTrain   The trainer.
     * @param theSpecies The species.
     */
    public BlueprintWorker(final BasicSANE theTrain, final Species theSpecies)
    {
        this.train = theTrain;
        this.species = theSpecies;
        this.rnd = this.train.getRandomNumberFactory().factor();

        this.parents = new Genome[this.train.getMaxParents()];
        this.children = new Genome[this.train.getMaxChildren()];
    }

    //version of worker to evaluate the elite genome
    public BlueprintWorker(final BasicSANE theTrain, final Species theSpecies,Genome genome)
    {
        this.train = theTrain;
        this.species = theSpecies;
        this.rnd = this.train.getRandomNumberFactory().factor();

        this.parents = new Genome[this.train.getMaxParents()];
        this.children = new Genome[this.train.getMaxChildren()];

        //elite genome
        this.eliteGenome = genome;
        elite_mode = true;
    }

    /**
     * Choose a parent.
     *
     * @return The chosen parent.
     */
    private Genome chooseParent()
    {
        final int idx = this.train.getSelection().performSelection(this.rnd,
                this.species);
        return this.species.getMembers().get(idx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object call()
    {
        boolean success = false;
        int tries = this.train.getMaxOperationErrors();
        do
        {
            try
            {
                //evaluate the elite genome
                if (elite_mode)
                {

                    this.train.calculateScore(eliteGenome);
                    if (!this.train.addChild(eliteGenome))
                    {
                        return null;
                    }
                    success = true;
                }
                //produce offspring and evaluate the offspring
                else
                {
                // choose an evolutionary operation (i.e. crossover or a type of
                // mutation) to use
                //final EvolutionaryOperator opp = this.train.getOperators().pickMaxParents(this.rnd, this.species.getMembers().size
                // ());

                //genetic operators for blueprints
                final EvolutionaryOperator blueprint_crossover = this.train.getBlueprintCrossover();
                final BlueprintMutateSwitchToRandom blueprint_mutate_random = this.train.getBlueprintMutateRandom();
                final EvolutionaryOperator blueprint_mutate_offspring = this.train.getBlueprintMutateOffspring();

                this.children[0] = null;
                this.children[1] = null;

                // prepare for either sexual or asexual reproduction either way,
                // we
                // need at least
                // one parent, which is the first parent.
                //
                // Chose the first parent, there must be at least one genome in
                // this
                // species
                this.parents[0] = chooseParent();

                //choose mating partner
                this.parents[1] = chooseParent();
                while (this.parents[0] == this.parents[1])
                {
                    this.parents[1] = chooseParent();
                }

                // success, perform crossover
                blueprint_crossover.performOperation(this.rnd, this.parents, 0, this.children, 0);


                //perform mutation on the offspring
                for (int i = 0; i < children.length; i++)
                {
                    blueprint_mutate_random.performOperation(this.rnd, this.train.getNeuronPopulation().getSpecies().get(0).getMembers(), 0, this
                            .children, i);
                    blueprint_mutate_offspring.performOperation(this.rnd, this.parents, 0, this.children, i);
                }

                // process the new child
                for (Genome child : this.children)
                {
                    if (child != null)
                    {
                        child.setPopulation(this.parents[0].getPopulation());
                        child.setBirthGeneration(this.train.getIteration());

                        this.train.calculateScore(child);
                        if (!this.train.addChild(child))
                        {
                            return null;
                        }
                        success = true;
                    }
                }
            }
        }
            catch (EARuntimeError e)
            {
                tries--;
                if (tries < 0)
                {
                    throw new EncogError(
                            "Could not perform a successful genetic operaton after "
                                    + this.train.getMaxOperationErrors()
                                    + " tries.");
                }
            }
            catch (final Throwable t)
            {
                if (!this.train.getShouldIgnoreExceptions())
                {
                    this.train.reportError(t);
                }
            }

        } while (!success);
        return null;
    }
}
