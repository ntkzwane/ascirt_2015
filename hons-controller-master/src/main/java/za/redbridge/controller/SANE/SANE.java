package za.redbridge.controller.SANE;

import java.util.Random;

/**
 * Created by Jae on 2015-08-23.
 * SANE algorithms implementation
 */
public class SANE
{
    static final int POPULATION_SIZE = 20;

    //dimension of the neural network
    public static int INPUT_SIZE;
    public static int OUTPUT_SIZE;
    public static int HIDDEN_SIZE;
    public static int IO_COUNT;
    public static int network_size;
    public static int CHROMOSOME_LENGTH;

    //initialize parameters
    public static void init(int inputSize, int hiddenSize, int outputSize)
    {
        INPUT_SIZE = inputSize;
        HIDDEN_SIZE = hiddenSize;
        OUTPUT_SIZE = outputSize;
        IO_COUNT = INPUT_SIZE + OUTPUT_SIZE;
        network_size = IO_COUNT + HIDDEN_SIZE;
        CHROMOSOME_LENGTH = IO_COUNT*3/4;
    }

    static Random random = new Random();
    static float neuron_mutation_rate = 0.01f;
    static float blueprint_mutation_rate_random = 0.01f;
    static float blueprint_mutation_rate_offspring = 0.5f;

    public SANE()
    {
        random = new Random();
    }

}
