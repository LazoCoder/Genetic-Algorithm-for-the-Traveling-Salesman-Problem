package GeneticAlgorithms;

import GeneticObjects.Chromosome;
import GeneticObjects.City;

import java.util.Random;

/**
 * Used for mutating the Chromosomes.
 */
class Mutation {

    /**
     * Class cannot be instantiated, as there would be no point, since all
     * the methods are static.
     */
    private Mutation () {}

    /**
     * Selects a city and inserts it into a random place.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    static Chromosome insertion (Chromosome chromosome, Random random) {
        City[] cities = chromosome.getArray();
        int randomIndex = random.nextInt(cities.length);
        int randomDestination = random.nextInt(cities.length);

        if (randomIndex < randomDestination) {
            City temp = cities[randomIndex];
            for (int i = randomIndex; i < randomDestination; i++) {
                cities[i] = cities[i+1];
            }
            cities[randomDestination] = temp;
        } else {
            City temp = cities[randomIndex];
            for (int i = randomIndex; i > randomDestination; i--) {
                cities[i] = cities[i-1];
            }
            cities[randomDestination] = temp;
        }
        return new Chromosome(cities);
    }

    /**
     * Swaps two randomly selected cities.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    static Chromosome reciprocalExchange (Chromosome chromosome, Random random) {
        City[] cities = chromosome.getArray();
        int l = cities.length;
        swap(cities, random.nextInt(l), random.nextInt(l));
        return new Chromosome(cities);
    }

    /**
     * Pick a subset of Cities and randomly re-arrange them.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    static Chromosome scrambleMutation (Chromosome chromosome, Random random) {

        /**
         * The subset Cities include wrapping.
         * Example: if there is a Chromosome with 10 cities and randomIndexStart is 8
         * and randomIndexEnd is 2, that means that the subset will include the cities
         * at indexes 8, 9, 1, and 2.
         */

        City[] cities = chromosome.getArray();
        int randomIndexStart = random.nextInt(cities.length);
        int randomIndexEnd = random.nextInt(cities.length);

        for (int i = randomIndexStart; i%cities.length != randomIndexEnd; i++) {
            int r = random.nextInt(Math.abs(i%cities.length - randomIndexEnd));
            swap(cities, i%cities.length, (i+r)%cities.length);
        }

        return new Chromosome(cities);
    }

    /**
     * Helper method for swapping two Cities in a Chromosome to change the tour.
     * @param array     the array of Cities to do the swap in
     * @param i         the index of the first City
     * @param j         the index of the second City
     */
    private static void swap (City[] array, int i, int j) {
        City temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

}
