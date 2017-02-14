package GeneticAlgorithms;

import GeneticObjects.Population;
import IO.Import.DataSet;

import java.util.Random;

import static GeneticObjects.Population.fromDataSet;

public class Preset {

    private Preset() {}

    public static GeneticAlgorithm getDefaultGA () {

        Random random = new Random();
        long seed = random.nextLong();
        System.out.println("Seed: " + seed);
        Random r = new Random();
        r.setSeed(seed);

        // Parameters.
        int     popSize         = 500;      // Size of the population.
        int     maxGen          = 500;      // Number of generations to run.
        double  crossoverRate   = 0.90;     // Odds that crossover will occur.
        double  mutationRate    = 0.04;     // Odds that mutation will occur.

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.setPopulation(fromDataSet(popSize, DataSet.att48, r));
        //geneticAlgorithm.setPopulation(Population.getRandomPopulation(100, popSize, r));
        geneticAlgorithm.setMaxGen(maxGen);
        geneticAlgorithm.setK(3);
        geneticAlgorithm.setElitismValue(1);
        geneticAlgorithm.setCrossoverRate(crossoverRate);
        geneticAlgorithm.setMutationRate(mutationRate);
        geneticAlgorithm.setRandom(r);
        geneticAlgorithm.forceUniqueness(false);
        geneticAlgorithm.setLocalSearchRate(0.00);
        geneticAlgorithm.setCrossoverType(GeneticAlgorithm.CrossoverType.UNIFORM_ORDER);
        geneticAlgorithm.setMutationType(GeneticAlgorithm.MutationType.INSERTION);

        return geneticAlgorithm;
    }

}
