package GeneticAlgorithms;

import GeneticObjects.Chromosome;
import GeneticObjects.Population;

import java.util.ArrayList;
import java.util.Random;

/**
 * Determines which chromosomes will survive and potentially reproduce.
 */
class Selection {

    /**
     * Class cannot be instantiated, as there would be no point, since all
     * the methods are static.
     */
    private Selection () {}

    // There is a 1 in 5 chance that fittest individual is not selected.
    private static final int ODDS_OF_NOT_PICKING_FITTEST = 5;

    /**
     * Picks k Chromosomes at at random and then return the best one.
     * There is a small chance that the best one will not be selected.
     * @param population    the population to selected from
     * @param k             the number of chromosomes to select
     * @param random        the Random object for randomly selecting
     * @return              usually the fittest Chromosome from k randomly selected chromosomes
     */
    static Chromosome tournamentSelection (Population population, int k, Random random) {
        if (k < 1) {
            throw new IllegalArgumentException("K must be greater than 0.");
        }

        Chromosome[] populationAsArray = population.getChromosomes();
        ArrayList<Chromosome> kChromosomes = getKChromosomes(populationAsArray, k, random);
        return getChromosome(kChromosomes, random);
    }

    /**
     * Returns k randomly selected Chromosomes.
     * @param pop       an array of Chromosomes (a population)
     * @param k         the number of Chromosomes to randomly select
     * @param random    the Random object used for picking a random chromosomes
     * @return          k randomly selected chromosomes
     */
    private static ArrayList<Chromosome> getKChromosomes (Chromosome[] pop, int k, Random random) {

        ArrayList<Chromosome> kChromosomes = new ArrayList<>();

        for (int j = 0; j < k; j++) {
            Chromosome chromosome = pop[random.nextInt(pop.length)];
            kChromosomes.add(chromosome);
        }

        return kChromosomes;
    }

    /**
     * Get the best Chromosome in a list of Chromosomes. There is a small chance
     * that a randomly selected Chromosome is picked instead of the best one.
     * @param arrayList     the list of Chromosomes
     * @param random        the Random object used for selecting a random Chromosome if needed
     * @return              usually the best Chromosome
     */
    private static Chromosome getChromosome (ArrayList<Chromosome> arrayList, Random random) {

        Chromosome bestChromosome = getBestChromosome(arrayList);

        // 1 in 5 chance to return a chromosome that is not the best.
        if (random.nextInt(ODDS_OF_NOT_PICKING_FITTEST) == 0 && arrayList.size() != 1) {
            arrayList.remove(bestChromosome);
            return arrayList.get(random.nextInt(arrayList.size()));
        }

        return bestChromosome;
    }

    /**
     * Get the best Chromosome in a list of Chromosomes.
     * @param arrayList     the list to search
     * @return              the best chromosome
     */
    private static Chromosome getBestChromosome (ArrayList<Chromosome> arrayList) {

        Chromosome bestC = null;

        for (Chromosome c : arrayList) {
            if (bestC == null) {
                bestC = c;
            } else if (c.getDistance() < bestC.getDistance()) {
                bestC = c;
            }
        }

        return bestC;
    }

}
