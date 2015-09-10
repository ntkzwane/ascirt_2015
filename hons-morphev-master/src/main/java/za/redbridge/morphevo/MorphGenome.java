package za.redbridge.morphevo;

import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.genome.DoubleArrayGenome;

public class MorphGenome extends DoubleArrayGenome{

    private MorphChrom morphChrom;

    public MorphGenome(int size){
        super(size);
        morphChrom = new MorphChrom( );
    }

    public MorphGenome(MorphGenome other){
        super(other);
        this.morphChrom = other.getChrom(); }

    /**
     * Decode the phenotype.
     */
    public void decode() {
        this.morphChrom.decodeFromArray(getData());
    }

    /**
     * @return the chromosome
     */
    public MorphChrom getChrom( ){
        return this.morphChrom;
    }

    public int getNumSensors( ){
        return morphChrom.encodedArrayLength( );
    }
}