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
package za.redbridge.genetic;

import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.genome.DoubleArrayGenome;

import za.redbridge.morphev.NEATM.sensor.SensorConfiguration;

/**
 * Implements a genome that allows a feedforward neural network to be trained
 * using a genetic algorithm. The chromosome for a feed forward neural network
 * is the weight and bias matrix.
 */
public class MorphGenome extends DoubleArrayGenome {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = 1L;

    private double[] data;

    /**
     * The phenome.
     */
    private MLEncodable phenotype;

    /**
     * Construct a genome of a specific size.
     * @param size The size.
     */
    public MorphGenome(int size){
        super(size);
    }

    /**
     * Construct a genome based on another genome.
     * @param other
     */
    public MorphGenome(MorphGenome other) {
        super(other);
    }

    /**
     * Construct a genome.
     *
     * @param thePhenotype
     *            The phenotype to use.
     */
    public MorphGenome(final MLEncodable thePhenotype) {
        super(thePhenotype.encodedArrayLength());
        this.phenotype = thePhenotype;
        this.phenotype.encodeToArray(getData());
    }

    /**
     * Decode the phenotype.
     */
    public void decode() {
        this.phenotype.decodeFromArray(getData());
    }

    /**
     * @return the phenotype
     */
    public MLEncodable getPhenotype() {
        return this.phenotype;
    }

    /**
     * @param phenotype
     *            the phenotype to set
     */
    public void setPhenotype(final MLEncodable phenotype) {
        this.phenotype = phenotype;
    }

}
