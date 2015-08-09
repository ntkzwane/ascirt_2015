package za.redbridge.genetic;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.MLEncodable;
import org.encog.ml.genetic.GeneticError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.genetic.sensor.SensorMorphology;
import za.redbridge.simulator.phenotype.Phenotype;
import za.redbridge.simulator.sensor.AgentSensor;
import za.redbridge.simulator.khepera.BottomProximitySensor;
import za.redbridge.simulator.khepera.ProximitySensor;
import za.redbridge.simulator.khepera.UltrasonicSensor;

import za.redbridge.genetic.SensorStatus;
import za.redbridge.genetic.sensor.SensorModel;

public class MorphPhenotype implements Phenotype, MLEncodable{
    private final MorphChromosome chromosome;
    private final MLData input;

    private List<AgentSensor> sensors;


    public MorphPhenotype(MorphChromosome chromosome) {
        this.chromosome = chromosome;

        // Initialise sensors
        SensorMorphology morphology = chromosome.getSensorMorphology();
        final int numSensors = morphology.getNumSensors();
        sensors = new ArrayList<>(numSensors);
        for (int i = 0; i < numSensors; i++) {
            sensors.add(morphology.getSensor(i));
        }

        input = new BasicMLData(numSensors);
    }

    @Override
    public List<AgentSensor> getSensors( ) {
        return sensors;
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        final MLData input = this.input;
        for (int i = 0, n = input.size(); i < n; i++) {
            input.setData(i, sensorReadings.get(i).get(0));
        }

//        MLData output = network.compute(input);
//        return new Double2D(output.getData(0) * 2.0 - 1.0, output.getData(1) * 2.0 - 1.0);
        //\TODO compute step
        return new Double2D(1,1);
    }

    @Override
    public Phenotype clone() {
        return new MorphPhenotype(chromosome);
//        return new NEATMPhenotype(network);
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return The length of an encoded array.
     */
    public int encodedArrayLength(){
        // \TODO alternatively return sensors.size();
        return chromosome.getNumActiveSensors();
    }

    /**
     * Encode the object to the specified array.
     * @param encoded The array.
     */
    public void encodeToArray(double[] encoded){
        throw new GeneticError("Encoding of a NEAT network is not supported.");
    }

    /**
     * Decode an array to this object.
     * @param encoded The encoded array.
     */
    public void decodeFromArray(double[] encoded){
        //\TODO iterate through each chromosome's genome and use getData() for each genome
        int numSensors = chromosome.getNumActiveSensors( );

        SensorModel[] sensorModels = new SensorModel[numSensors];
        for (int i = 0; i < numSensors; i++) {
//            sensorModels[i] = inputNeuron.getSensorConfiguration().toSensorModel();
            sensorModels[i] = chromosome.getSensorConfiguration( ).toSensorModel( );
        }

        SensorMorphology morphology = new SensorMorphology(sensorModels);

        for(int i = 0; i < numSensors; i++){
            SensorStatus currSensor = chromosome.getSensorType(i);
            double[] params = chromosome.getSensorParams(i);
            switch (currSensor){
                case BOTTOM_PROXIMITY:
                    sensors.add(new BottomProximitySensor( ));
                    break;
                case PROXIMITY:
                    sensors.add(new ProximitySensor((float) params[0],(float) params[1],(float) params[2],(float) params[3]));
                    break;
                case ULTRASONIC:
                    sensors.add(new UltrasonicSensor((float) params[0],(float) params[1],(float) params[2],(float) params[3]));
                    break;
                default:
                    // invalid sensor type (should not occur)
            }
        }
    }
}