package Utility;

import Display.WindowGraph;
import GeneticAlgorithms.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;

/**
 * Runs a Genetic Algorithm several times and graphs the average of the results.
 */
public class AveragingTool {

    private GeneticAlgorithm geneticAlgorithm;
    private int numOfTimesToRun;
    private ArrayList<ArrayList<Integer>> allValues;
    private ArrayList<String> legend = new ArrayList<>();

    public AveragingTool (GeneticAlgorithm geneticAlgorithm, int numOfTimesToRun) {
        this.geneticAlgorithm = geneticAlgorithm;
        this.numOfTimesToRun = numOfTimesToRun;
        allValues = new ArrayList<>();
        legend = new ArrayList<>();
    }

    public void run () {

        ArrayList<Integer> valuesForAverage = new ArrayList<>(); // Average dist. of each gen.
        ArrayList<Integer> valuesForBest = new ArrayList<>();    // Best dist. of each gen.

        for (int i = 0; i < numOfTimesToRun; i++) {
            geneticAlgorithm.reset();
            geneticAlgorithm.run();

            ArrayList<Integer> avgDist = geneticAlgorithm.getAverageDistanceOfEachGeneration();
            ArrayList<Integer> bestDist = geneticAlgorithm.getBestDistanceOfEachGeneration();

            if (valuesForAverage.size() == 0) {
                for (int j = 0; j < avgDist.size(); j++) {
                    valuesForAverage.add(0);
                }
            }

            if (valuesForBest.size() == 0) {
                for (int j = 0; j < bestDist.size(); j++) {
                    valuesForBest.add(0);
                }
            }

            for (int j = 0; j < avgDist.size(); j++) {
                valuesForAverage.set(j, valuesForAverage.get(j) + avgDist.get(j));
                valuesForBest.set(j, valuesForBest.get(j) + bestDist.get(j));
            }

        }

        for (int i = 0; i < valuesForAverage.size(); i++) {
            valuesForAverage.set(i, valuesForAverage.get(i) / numOfTimesToRun);
            valuesForBest.set(i, valuesForBest.get(i) / numOfTimesToRun);
        }

        legend.add("Eval. of Pop.");
        legend.add("Eval. of Fittest");
        allValues.add(valuesForAverage);
        allValues.add(valuesForBest);

        int avgFinalSolution = valuesForBest.get(valuesForBest.size()-1);
        System.out.println("Average Final Solution: " + avgFinalSolution);
    }

    private int idx = 0;
    public void addItemToLegend (String item) {
        legend.set(idx, item + " - " + legend.get(idx++));
    }

    public void display () {
        if (legend.size() != allValues.size()) {
            System.out.println(allValues.size());
            System.out.println(legend.size());
            throw new IllegalStateException("Size of legend needs to be " +
                    "the same as the number of lines.");
        }
        new WindowGraph(allValues, legend);
    }

}
