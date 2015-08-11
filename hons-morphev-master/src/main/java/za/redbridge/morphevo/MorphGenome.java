package za.redbridge.morphevo;

import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.genome.DoubleArrayGenome;

public class MorphGenome extends DoubleArrayGenome{

    private MorphPhenotype phenotype;

    public MorphGenome(int size){
        super(size);
        phenotype = new MorphPhenotype( );
    }

    public MorphGenome(MorphGenome other){
        super(other);
        this.phenotype = other.getPhenotype();
    }

    /**
     * Decode the phenotype.
     */
    public void decode() {
//        System.out.println("MorphGenome.decode( ) -- phenotype decoder");
        this.phenotype.decodeFromArray(getData());

    }

    /**
     * @return the phenotype
     */
    public MorphPhenotype getPhenotype() {
        return this.phenotype;
    }

    /**
     * @param phenotype
     *            the phenotype to set
     */
    public void setPhenotype(final MorphPhenotype phenotype) {
        this.phenotype = phenotype;
    }

    public int getNumSensors( ){
        return phenotype.getNumSensors( );
    }
}