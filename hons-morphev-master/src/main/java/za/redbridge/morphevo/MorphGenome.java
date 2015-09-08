package za.redbridge.morphevo;

import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.genome.DoubleArrayGenome;

public class MorphGenome extends DoubleArrayGenome{

    // private MorphPhenotype phenotype;
    private MorphChrom morphChrom;

    public MorphGenome(int size){
        super(size);
        morphChrom = new MorphChrom( );
        // phenotype = new MorphPhenotype( );
    }

    public MorphGenome(MorphGenome other){
        super(other);
        this.morphChrom = other.getChrom();
        // this.phenotype = other.getPhenotype();
    }

    /**
     * Decode the phenotype.
     */
    public void decode() {
//        System.out.println("MorphGenome.decode( ) -- phenotype decoder");
        // System.out.println(morphChrom == null);
        this.morphChrom.decodeFromArray(getData());
    }

    /**
     * @return the phenotype
     */
    /*public MorphPhenotype getPhenotype() {
        return this.phenotype;
    }*/
    public MorphChrom getChrom( ){
        return this.morphChrom;
    }

    /**
     * @param phenotype
     *            the phenotype to set
     */
    /*public void setPhenotype(final MorphPhenotype phenotype) {
        this.phenotype = phenotype;
    }*/

    public int getNumSensors( ){
        return morphChrom.encodedArrayLength( );
    }
}