/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package za.redbridge.morphevo;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.sort.GenomeComparator;
import org.encog.ml.ea.sort.MaximizeScoreComp;
import org.encog.ml.ea.sort.MinimizeScoreComp;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.genetic.crossover.Splice;
import org.encog.ml.genetic.mutate.MutatePerturb;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.logging.EncogLogging;

import org.encog.ml.genetic.MLEncodableCODEC;
import org.encog.ml.genetic.MLMethodGenomeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a genetic algorithm that allows an MLMethod that is encodable
 * (MLEncodable) to be trained. It works well with both BasicNetwork and
 * FreeformNetwork class, as well as any MLEncodable class.
 *
 * There are essentially two ways you can make use of this class.
 *
 * Either way, you will need a score object. The score object tells the genetic
 * algorithm how well suited a neural network is.
 *
 * If you would like to use genetic algorithms with a training set you should
 * make use TrainingSetScore class. This score object uses a training set to
 * score your neural network.
 *
 * If you would like to be more abstract, and not use a training set, you can
 * create your own implementation of the CalculateScore method. This class can
 * then score the networks any way that you like.
 */
public class MorphologyEvolution extends BasicTraining implements MultiThreadable {

	private StatsRecorder statsRecorder;

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private static final double CONVERGENCE_SCORE = 110;

	private final int PARAM_LENGTH = 5;
	/**
	 * Very simple class that implements a genetic algorithm.
	 *
	 * @author jheaton
	 */
	public class MorphologyEvolutionHelper extends TrainEA {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Construct the helper.
		 *
		 * @param thePopulation
		 *            The population.
		 * @param theScoreFunction
		 *            The score function.
		 */
		public MorphologyEvolutionHelper(final Population thePopulation,
				final CalculateScore theScoreFunction) {
			super(thePopulation, theScoreFunction);
		}
	}

	/**
	 * Simple helper class that implements the required methods to implement a
	 * genetic algorithm.
	 */
	private MorphologyEvolutionHelper genetic;

	/**
	 * Construct a method genetic algorithm.
	 *
	 * @param phenotypeFactory
	 *            The phenotype factory.
	 * @param calculateScore
	 *            The score calculation object.
	 * @param populationSize
	 *            The population size.
	 */
	public MorphologyEvolution(final MethodFactory phenotypeFactory, final CalculateScore calculateScore, final int populationSize) {
		super(TrainingImplementationType.Iterative);

		// Create the population
		final Population population = new BasicPopulation(populationSize, null);
		final Species defaultSpecies = population.createSpecies( );//new BasicSpecies( );
//		defaultSpecies.setPopulation(population);

		for (int i = 0; i < population.getPopulationSize(); i++) {
			final MorphGenome genome = randomGenome();

			genome.setScore(0.0);
			genome.setAdjustedScore(0.0);
			defaultSpecies.getMembers( ).add(genome);
//			defaultSpecies.add(genome);

		}

		defaultSpecies.setLeader(defaultSpecies.getMembers().get(0));

		population.setGenomeFactory(new MorphGenomeFactory(PARAM_LENGTH));
//		population.getSpecies().add(defaultSpecies);

		// create the trainer
		this.genetic = new MorphologyEvolutionHelper(population, calculateScore);
		this.genetic.setCODEC(new MorphCODEC( ));

		GenomeComparator comp = null;
		if (calculateScore.shouldMinimize()) {
			comp = new MinimizeScoreComp();
		} else {
			comp = new MaximizeScoreComp();
		}
		this.genetic.setBestComparator(comp);
		this.genetic.setSelectionComparator(comp);
		
		// create the operators
		final int s = Math.max(defaultSpecies.getMembers().get(0).size() / 5, 1);
		getGenetic().setPopulation(population);

		this.genetic.addOperation(0.9, new Splice(s));
		this.genetic.addOperation(0.1, new MutatePerturb(1.0));

		// create the stats recorder
		statsRecorder = new StatsRecorder(getGenetic(),(ScoreCalculator) getGenetic().getScoreFunction());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return The genetic algorithm implementation.
	 */
	public MorphologyEvolutionHelper getGenetic() {
		return this.genetic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod getMethod() {
		final Genome best = this.genetic.getBestGenome();
		return this.genetic.getCODEC().decode(best);
	}

	@Override
	public int getThreadCount() {
		return this.genetic.getThreadCount();
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public void iteration() {

		EncogLogging.log(EncogLogging.LEVEL_INFO, "Performing Genetic iteration.");
		preIteration();
		setError(getGenetic().getError());

		/*System.out.println("Decoding all genomes");
		for(Genome currGenome : getGenetic().getPopulation().getSpecies().get(0).getMembers()){
			((MorphGenome)currGenome).decode( );
		}*/

//		statsRecorder.recordIterationStats();
		System.out.println("Iterating");
		getGenetic().iteration();

		statsRecorder.recordIterationStats();

		if (getGenetic().getBestGenome().getScore() >= CONVERGENCE_SCORE) {
			log.info("Convergence reached at epoch " + getGenetic().getIteration());
			return;
		}

		setError(getGenetic().getError());
		postIteration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	/**
	 * Set the genetic helper class.
	 * 
	 * @param genetic
	 *            The genetic helper class.
	 */
	public void setGenetic(final MorphologyEvolutionHelper genetic) {
		this.genetic = genetic;
	}

	@Override
	public void setThreadCount(final int numThreads) {
		this.genetic.setThreadCount(numThreads);

	}

	private MorphGenome randomGenome( ) {
		final MorphGenome result = new MorphGenome(PARAM_LENGTH);
		final double organism[] = result.getData();

		for (int i = 0; i < organism.length; i++) {
			organism[i] = Math.PI/2*Math.random();
		}
		return result;
	}
}
