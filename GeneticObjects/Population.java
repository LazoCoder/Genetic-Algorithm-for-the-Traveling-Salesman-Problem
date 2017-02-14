package GeneticObjects;

import IO.Import.DataSet;

import java.nio.BufferOverflowException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Represents a Population of chromosomes.
 */
public class Population implements Iterable<Chromosome> {

    private PriorityQueue<Chromosome> chromosomes;
    private int maxSize;

    /**
     * Constructs an empty population with a maximum size.
     * @param maxSize  the maximum size of the Population
     */
    public Population (int maxSize) {
        this.maxSize = maxSize;
        chromosomes = new PriorityQueue<>();
    }

    /**
     * Adds a Chromosome to the Population.
     * @param chromosome        the chromosome to add
     */
    public void add (Chromosome chromosome) {
        if (chromosomes.size() == maxSize) {
            throw new BufferOverflowException();
        }
        chromosomes.add(chromosome);
    }

    public void populate (City[] cities, Random random) {

        if (chromosomes.size() == maxSize) {
            throw new BufferOverflowException();
        }

        // If the popSize is greater than the factorial of cities, uniqueness not possible.
        // Example: if there are 2 cities but the population size is 100, it is impossible
        // to have all unique values since there are at most 2! = 2 unique possibilities.
        if ((cities.length == 1 && maxSize > 1) ||
                (cities.length == 2 && maxSize > 2) ||
                (cities.length == 3 && maxSize > 6) ||
                (cities.length == 4 && maxSize > 24) ||
                (cities.length == 5 && maxSize > 120) ||
                (cities.length == 6 && maxSize > 720) ||
                (cities.length == 7 && maxSize > 5_040) ||
                (cities.length == 8 && maxSize > 40_320) ||
                (cities.length == 9 && maxSize > 362_880)) {
            throw new IllegalStateException("Cannot force uniqueness when" +
                    " the population size is greater than the factorial" +
                    " of the total number of cities.");
        }

        HashSet<Chromosome> hashSet = new HashSet<>();

        while (chromosomes.size() < maxSize) {
            Chromosome chromo = new Chromosome(cities, random);
            if (!hashSet.contains(chromo)) {
                hashSet.add(chromo);
                add(chromo);
            }
        }

    }

    /**
     * Removes all the Chromosomes.
     */
    public void clear () {
        chromosomes.clear();
    }

    /**
     * Get an array of all the Cities.
     * @return  the array of Cities
     */
    public City[] getCities () {
        return chromosomes.peek().getArray().clone();
    }

    /**
     * Get an array of all the Chromosomes.
     * @return  the array of the Chromosomes
     */
    public Chromosome[] getChromosomes () {
        Chromosome[] array = new Chromosome[chromosomes.size()];

        int i = 0;
        for (Chromosome chromo : chromosomes) {
            array[i++] = chromo;
        }

        return array;
    }

    /**
     * Get the size of the Population.
     * @return  the number of all the Chromosomes.
     */
    public int size () {
        return chromosomes.size();
    }

    /**
     * Gets the average distance of all the chromosomes.
     * @return  the mean distance travelled
     */
    public int getAverageDistance () {

        int averageDistance = 0;

        for (Chromosome chromosome : chromosomes) {
            averageDistance += chromosome.getDistance();
        }

        return averageDistance / chromosomes.size();
    }

    public static Population fromDataSet (int popSize, DataSet dataSet, Random r) {
        City[] cities = IO.Import.getCities(dataSet);
        Population population = new Population(popSize);
        population.populate(cities, r);
        return population;
    }

    /**
     * Generate a Population of randomly generate Chromosomes.
     * @param numOfCities   the number of cities
     * @param sizeOfPop     the size of the population
     * @param random        the Random object used for the generation
     * @return              a randomly generated Population
     */
    public static Population getRandomPopulation(int numOfCities, int sizeOfPop, Random random) {
        City[] cities = new City[numOfCities];

        for (int i = 0; i < numOfCities; i++) {
            cities[i] = City.getRandomCity(random);
        }

        Population population = new Population(sizeOfPop);

        for (int i = 0; i < sizeOfPop; i++) {
            population.add(new Chromosome(cities, random));
        }

        return population;
    }

    /**
     * Get the Chromosome that has the path with the least distance.
     * @return  the most fit Chromosome
     */
    public Chromosome getMostFit () {
        return chromosomes.peek();
    }

    public Iterator<Chromosome> iterator () {
        return chromosomes.iterator();
    }

    public Population deepCopy () {
        Population population = new Population(maxSize);
        chromosomes.forEach((chromosome) -> population.add(chromosome));
        return population;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("Population:");

        for (Chromosome chromosome : chromosomes) {
            sb.append("\n");
            sb.append(chromosome);
            sb.append(" Value: ");
            sb.append(chromosome.getDistance());
        }

        return new String(sb);
    }

}
