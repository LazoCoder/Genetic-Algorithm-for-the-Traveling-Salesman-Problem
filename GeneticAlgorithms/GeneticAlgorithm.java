package GeneticAlgorithms;

import Display.WindowGraph;
import Display.WindowTSP;
import GeneticObjects.Chromosome;
import GeneticObjects.City;
import GeneticObjects.Population;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * The Genetic Algorithm system. This class brings together the entire process
 * of the Genetic Algorithm.
 */
public class GeneticAlgorithm {

    // Parameters to be set.
    private Population population;
    private Population initialPop;
    private int maxGen;             // The number of generations to run.
    private int k;                  // For tournament selection.
    private int elitismValue;       // Quantity of Elite to carry along each generation.
    private double crossoverRate;   // Odds of crossover occurring.
    private double mutationRate;    // Odds of mutation occurring.
    private boolean forceUniqueness;// If true, population always has all unique members.
    private double localSearchRate; // Odds of local search occurring on entire generation.
    private Random random;
    private CrossoverType crossoverType = CrossoverType.UNIFORM_ORDER;
    private MutationType mutationType = MutationType.INSERTION;

    private boolean finished;

    // Results
    private int averageDistanceOfFirstGeneration;
    private int averageDistanceOfLastGeneration;
    private int bestDistanceOfFirstGeneration;
    private int bestDistanceOfLastGeneration;
    private ArrayList<Integer> averageDistanceOfEachGeneration;
    private ArrayList<Integer> bestDistanceOfEachGeneration;
    private int areaUnderAverageDistances;
    private int areaUnderBestDistances;

    /**
     * Construct the GeneticAlgorithm object with default values.
     */
    public GeneticAlgorithm () {
        initialPop = Population.getRandomPopulation(10, 10, new Random());
        population = initialPop.deepCopy();
        maxGen = 10;
        k = 3;
        elitismValue = 1;
        crossoverRate = 0.95;
        mutationRate = 0.05;
        forceUniqueness = false;
        localSearchRate = 0.0;
        random = new Random();
        crossoverType = CrossoverType.UNIFORM_ORDER;
        mutationType = MutationType.INSERTION;
        finished = false;

        averageDistanceOfEachGeneration = new ArrayList<>();
        bestDistanceOfEachGeneration = new ArrayList<>();
        areaUnderAverageDistances = 0;
        areaUnderBestDistances = 0;
    }

    public void setPopulation (Population population) {
        if (population == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        initialPop = population;
        this.population = initialPop.deepCopy();
        averageDistanceOfFirstGeneration = population.getAverageDistance();
        bestDistanceOfFirstGeneration = population.getMostFit().getDistance();
    }

    public void setMaxGen (int maxGen) {
        if (maxGen < 0) {
            throw new IllegalArgumentException("Parameter cannot be negative.");
        }
        this.maxGen = maxGen;
    }

    public void setK (int k) {
        if (k < 0) {
            throw new IllegalArgumentException("Parameter cannot be negative.");
        }
        this.k = k;
    }

    public void setElitismValue (int elitismValue) {
        if (elitismValue > population.size()) {
            throw new IllegalArgumentException("Elitism value " +
                    "cannot be greater than population size.");
        }
        this.elitismValue = elitismValue;
    }

    public void setCrossoverRate (double crossoverRate) {
        if (crossoverRate < 0 || crossoverRate > 1) {
            throw new IllegalArgumentException("Parameter must be between 1 and 0 inclusive.");
        }
        this.crossoverRate = crossoverRate;
    }

    public void setMutationRate (double mutationRate) {
        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException("Parameter must be between 1 and 0 inclusive.");
        }
        this.mutationRate = mutationRate;
    }

    public void setLocalSearchRate (double localSearchRate) {
        if (localSearchRate < 0 || localSearchRate > 1) {
            throw new IllegalArgumentException("Parameter must be between 1 and 0 inclusive.");
        }
        this.localSearchRate = localSearchRate;
    }

    public void setRandom (Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.random = random;
    }

    public void forceUniqueness (boolean forceUniqueness) {

        int cities = population.getCities().length;
        int popSize = population.size();

        // If the popSize is greater than the factorial of cities, uniqueness not possible.
        // Example: if there are 2 cities but the population size is 100, it is impossible
        // to have all unique values since there are at most 2! = 2 unique possibilities.
        if ((cities == 1 && popSize > 1) ||
                (cities == 2 && popSize > 2) ||
                (cities == 3 && popSize > 6) ||
                (cities == 4 && popSize > 24) ||
                (cities == 5 && popSize > 120) ||
                (cities == 6 && popSize > 720) ||
                (cities == 7 && popSize > 5_040) ||
                (cities == 8 && popSize > 40_320) ||
                (cities == 9 && popSize > 362_880)) {
            throw new IllegalStateException("Cannot force uniqueness when" +
                    " the population size is greater than the factorial" +
                    " of the total number of cities.");
        }

        this.forceUniqueness = forceUniqueness;
    }

    public void setCrossoverType (CrossoverType crossoverType) {
        this.crossoverType = crossoverType;
    }

    public void setMutationType (MutationType mutationType) {
        this.mutationType = mutationType;
    }

    public int getAverageDistanceOfFirstGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return averageDistanceOfFirstGeneration;
    }

    public int getAverageDistanceOfLastGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return averageDistanceOfLastGeneration;
    }

    public int getBestDistanceOfFirstGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return bestDistanceOfFirstGeneration;
    }

    public int getBestDistanceOfLastGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return bestDistanceOfLastGeneration;
    }

    public ArrayList<Integer> getAverageDistanceOfEachGeneration() {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return averageDistanceOfEachGeneration;
    }

    public ArrayList<Integer> getBestDistanceOfEachGeneration() {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return bestDistanceOfEachGeneration;
    }

    public int getAreaUnderAverageDistances () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return areaUnderAverageDistances;
    }

    public int getAreaUnderBestDistances () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return areaUnderBestDistances;
    }

    public void showInWindow () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        WindowTSP win = new WindowTSP(population.getCities());
        win.draw(population.getMostFit());
    }

    public void showGraphInWindow () {
        ArrayList<ArrayList<Integer>> yValues = new ArrayList<>();
        yValues.add(averageDistanceOfEachGeneration);
        yValues.add(bestDistanceOfEachGeneration);
        ArrayList<String> legend = new ArrayList<>();
        legend.add("Average Evaluation of Entire Population");
        legend.add("Evaluation of Fittest Member");
        new WindowGraph(yValues, legend);
    }

    public void run () {
        for (int i = 0; i < maxGen; i++) {
            population = createNextGeneration();
            averageDistanceOfEachGeneration.add(population.getAverageDistance());
            areaUnderAverageDistances += population.getAverageDistance();
            bestDistanceOfEachGeneration.add(population.getMostFit().getDistance());
            areaUnderBestDistances += population.getMostFit().getDistance();
        }
        finished = true;
        averageDistanceOfLastGeneration = population.getAverageDistance();
        bestDistanceOfLastGeneration = population.getMostFit().getDistance();
    }

    /**
     * Displays the fittest Chromosome of each Population to the screen.
     * Also displays a graph that contains the average distance per population.
     */
    public void runWithDebugMode () {
        WindowTSP win = new WindowTSP(population.getCities());
        delay(1000);

        Chromosome mostFitLast = population.getMostFit();
        win.draw(mostFitLast);
        for (int i = 0; i < maxGen; i++) {
            population = createNextGeneration();

            Chromosome mostFit = population.getMostFit();
            if (!mostFit.equals(mostFitLast)) {
                win.draw(mostFit);
            }
            mostFitLast = mostFit;
            averageDistanceOfEachGeneration.add(population.getAverageDistance());
            areaUnderAverageDistances += population.getAverageDistance();
            bestDistanceOfEachGeneration.add(population.getMostFit().getDistance());
            areaUnderBestDistances += population.getMostFit().getDistance();
        }

        finished = true;
        averageDistanceOfLastGeneration = population.getAverageDistance();
        bestDistanceOfLastGeneration = population.getMostFit().getDistance();
    }

    /**
     * The meat of the entire class. Generates the next generation from
     * the current population. Brings together all the other classes in
     * the package; Selection, Crossover & Mutation.
     * @return  the new generation
     */
    private Population createNextGeneration () {

        Population nextGen = new Population(population.size());

        performElitism(nextGen);

        HashSet<Chromosome> chromosomesAdded = new HashSet<>(); // For checking duplicates.

        while (nextGen.size() < population.size()-1) {

            Chromosome p1 = Selection.tournamentSelection(population, k, random);
            Chromosome p2 = Selection.tournamentSelection(population, k, random);

            boolean doCrossover     = (random.nextDouble() <= crossoverRate);
            boolean doMutate1       = (random.nextDouble() <= mutationRate);
            boolean doMutate2       = (random.nextDouble() <= mutationRate);
            boolean doLocalSearch1  = (random.nextDouble() <= localSearchRate);
            boolean doLocalSearch2  = (random.nextDouble() <= localSearchRate);

            if (doCrossover) {
                ArrayList<Chromosome> children = crossover(p1, p2);
                p1 = children.get(0);
                p2 = children.get(1);
            }

            if (doMutate1) p1 = mutate(p1);
            if (doMutate2) p2 = mutate(p2);

            if (doLocalSearch1) p1 = performLocalSearch(p1);
            if (doLocalSearch2) p2 = performLocalSearch(p2);

            if (forceUniqueness) {
                if (!chromosomesAdded.contains(p1)) {
                    chromosomesAdded.add(p1);
                    nextGen.add(p1);
                }

                if (!chromosomesAdded.contains(p2)) {
                    chromosomesAdded.add(p2);
                    nextGen.add(p2);
                }
            } else {
                nextGen.add(p1);
                nextGen.add(p2);
            }

        }

        // If there is one space left, fill it up.
        if (nextGen.size() != population.size()) {
            nextGen.add(Selection.tournamentSelection(population, k, random));
        }

        if (nextGen.size() != population.size()) {
            throw new AssertionError("Next generation population should be full.");
        }

        return nextGen;
    }

    private void performElitism (Population nextGen) {
        PriorityQueue<Chromosome> priorityQueue = new PriorityQueue<>();

        for (Chromosome chromosome : population) {
            priorityQueue.add(chromosome);
        }

        for (int i = 0; i < elitismValue; i++) {

            Chromosome chromosome = priorityQueue.poll();

            if (localSearchRate > 0) {
                chromosome = performLocalSearch(chromosome);
            }

            //if (random.nextDouble() < localSearchRate)
            //    chromosome = performLocalSearch(chromosome);

            nextGen.add(chromosome);
        }
    }

    private Chromosome performLocalSearch (Chromosome chromosome) {

        int bestDistance = chromosome.getDistance();
        City[] array = chromosome.getArray();
        City[] bestArray = array.clone();

        for (int i = 0; i < array.length-1; i++) {
            for (int k = i+1; k < array.length; k++) {

                City[] temp = array.clone();

                // Reverse order from i to k.
                for (int j = i; j <= (i+k)/2; j++) {
                    swap(temp, j, k - (j-i));
                }

                Chromosome c = new Chromosome(temp);

                int distance = c.getDistance();
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestArray = c.getArray();
                }

            }
        }

        return new Chromosome(bestArray);
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

    /**
     * Mutate the Chromosome based on what type is selected.
     * @param chromosome    the Chromosome to mutate
     * @return              the mutated Chromosome
     */
    private Chromosome mutate (Chromosome chromosome) {
        if (mutationType == MutationType.INSERTION) {
            return Mutation.insertion(chromosome, random);
        } else if (mutationType == MutationType.RECIPROCAL_EXCHANGE) {
            return Mutation.reciprocalExchange(chromosome, random);
        } else if (mutationType == MutationType.SCRAMBLE) {
            return Mutation.scrambleMutation(chromosome, random);
        } else { // Default is insertion.
            return Mutation.insertion(chromosome, random);
        }
    }

    /**
     * Perform the selected type of crossover.
     * @param p1            the first parent
     * @param p2            the second parent
     * @return              the children
     */
    private ArrayList<Chromosome> crossover (Chromosome p1, Chromosome p2) {
        ArrayList<Chromosome> children;
        if (crossoverType == CrossoverType.UNIFORM_ORDER) {
            children = Crossover.uniformOrder(p1, p2, random);
        } else if (crossoverType == CrossoverType.ONE_POINT) {
            children = Crossover.onePointCrossover(p1, p2, random);
        } else {
            children = Crossover.orderCrossover(p1, p2, random);
        }
        return children;
    }

    private static void delay (int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public enum MutationType {
        INSERTION,
        RECIPROCAL_EXCHANGE,
        SCRAMBLE
    }

    public enum CrossoverType {
        UNIFORM_ORDER,
        ONE_POINT,
        TWO_POINT
    }

    public void reset () {
        population = initialPop.deepCopy();
        averageDistanceOfEachGeneration = new ArrayList<>();
        bestDistanceOfEachGeneration = new ArrayList<>();
        areaUnderAverageDistances = 0;
        areaUnderBestDistances = 0;
        finished = false;
    }

    public void printProperties () {
        System.out.println("----------Genetic Algorithm Properties----------");
        System.out.println("Number of Cities:   " + population.getMostFit().getArray().length);
        System.out.println("Population Size:    " + population.size());
        System.out.println("Max. Generation:    " + maxGen);
        System.out.println("k Value:            " + k);
        System.out.println("Elitism Value:      " + elitismValue);
        System.out.println("Force Uniqueness:   " + forceUniqueness);
        System.out.println("Local Search Rate:  " + localSearchRate);
        System.out.println("Crossover Type:     " + crossoverType);
        System.out.println("Crossover Rate:     " + (crossoverRate*100) + "%");
        System.out.println("Mutation Type:      " + mutationType);
        System.out.println("Mutation Rate:      " + (mutationRate*100) + "%");
    }

    public void printResults () {

        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }

        System.out.println("-----------Genetic Algorithm Results------------");
        System.out.println("Average Distance of First Generation:  " +
                getAverageDistanceOfFirstGeneration());
        System.out.println("Average Distance of Last Generation:   " +
                getAverageDistanceOfLastGeneration());
        System.out.println("Best Distance of First Generation:     " +
                getBestDistanceOfFirstGeneration());
        System.out.println("Best Distance of Last Generation:      " +
                getBestDistanceOfLastGeneration());
        System.out.println("Area Under Average Distance:           " +
                getAreaUnderAverageDistances());
        System.out.println("Area Under Average Distance:           " +
                getAreaUnderBestDistances());
    }

}