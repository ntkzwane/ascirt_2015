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

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;

/**
 * A factory that creates MorphGenome objects of a specific size.
 */
public class MorphGenomeFactory implements GenomeFactory {

	/**
	 * The size to create.
	 */
	private int size;

	/**
	 * Construct the genome factory.
	 * @param theSize The size to create genomes of.
	 */
	public MorphGenomeFactory(int theSize) {
		this.size = theSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor() {
		return new MorphGenome(this.size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor(Genome other) {
		// TODO Auto-generated method stub
		return new MorphGenome((MorphGenome) other);
	}
}
