package Utility;

import GeneticAlgorithms.GeneticAlgorithm;
import GeneticAlgorithms.Preset;

import java.util.Scanner;

public class HeatMap {
    private int maxValue = 0;
    private int minValue = Integer.MAX_VALUE;
    private GeneticAlgorithm geneticAlgorithm;

    private int rows;
    private int columns;
    private int[][] array;
    private boolean crossoverRangeSet;
    private boolean mutationRangeSet;
    private double cStart, cFinish, cIncrement;
    private double mStart, mFinish, mIncrement;
    private int numberOfRuns;
    private int scale;

    private HeatMap () {
        numberOfRuns = 10;
        scale = 10;
    }

    private HeatMap (GeneticAlgorithm geneticAlgorithm) {
        this.geneticAlgorithm = geneticAlgorithm;
        numberOfRuns = 10;
        scale = 10;
        crossoverRangeSet = false;
        mutationRangeSet = false;
    }

    private void setCrossoverRange (double start, double finish, double increment) {
        checkRangeValues(start, finish, increment);

        int theStart = (int)(start * 1000.0);
        int theFinish = (int)(finish * 1000.0);
        int theIncrement = (int)(increment * 1000.0);

        cStart = start;
        cFinish = finish;
        cIncrement = increment;

        columns = (theFinish - theStart) / theIncrement + 1;
        crossoverRangeSet = true;
    }

    private void setMutationRange (double start, double finish, double increment) {
        checkRangeValues(start, finish, increment);

        int theStart = (int)(start * 1000.0);
        int theFinish = (int)(finish * 1000.0);
        int theIncrement = (int)(increment * 1000.0);

        mStart = start;
        mFinish = finish;
        mIncrement = increment;

        rows = (theFinish - theStart) / theIncrement + 1;
        mutationRangeSet = true;
    }

    private void setNumberOfRuns (int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    private void setScale (int scale) {
        if (scale < 0 || scale > 20) {
            throw new IllegalArgumentException("Scale must be between 0 and 20, inclusive.");
        }
        this.scale = scale;
    }

    private void checkRangeValues (double start, double finish, double increment) {
        if (start < 0 || finish < 0 || increment < 0 || start > 1 || finish > 1 || increment > 1) {
            throw new IllegalArgumentException("Values must be between 0 and 1.");
        }
        if (finish < start) {
            throw new IllegalArgumentException("Finish must be greater than start.");
        }
        if (increment > (finish-start)) {
            throw new IllegalArgumentException("Increment cannot be greater " +
                    "than the difference of start and finish.");
        }
    }

    private void run () {
        if (!crossoverRangeSet || !mutationRangeSet) {
            throw new IllegalStateException("Ranges have not been set.");
        }

        System.out.println("Total Runs To Be Done: " + columns * rows * numberOfRuns);
        System.out.println(columns + " columns by " + rows + " rows, executed "
                + numberOfRuns + " times(s) each.");

        System.out.print("Testing Crossover rate from " + cStart + " to " + cFinish + " by " + cIncrement);
        System.out.println(" (" + (int)(cStart*100) + "% to " + (int)(cFinish*100) + "% by " + (int)(cIncrement*100) + "%)");
        System.out.print("Testing Mutation rate from " + mStart + " to " + mFinish + " by " + mIncrement);
        System.out.println(" (" + (int)(mStart*100) + "% to " + (int)(mFinish*100) + "% by " + (int)(mIncrement*100) + "%)");

        estimateTime();
        System.out.println("Running...");

        array = new int[rows][columns];
        loadGeneticAlgorithmResults();
    }

    private void estimateTime () {

        System.out.println("Estimating time...");

        int num = 100;

        long before = System.nanoTime();

        java.util.Random random = new java.util.Random();
        double c, m;

        for (int i = 0; i < num; i++) {
            c = (random.nextInt((int)((cFinish-cStart) * 1000.0)) + cStart*1000.0)/1000.0;
            m = (random.nextInt((int)((mFinish-mStart) * 1000.0)) + mStart*1000.0)/1000.0;
            geneticAlgorithm.reset();
            geneticAlgorithm.setCrossoverRate(c);
            geneticAlgorithm.setMutationRate(m);
            geneticAlgorithm.run();
        }

        double now = ((System.nanoTime() - before) / 1_000_000_000.0)/(double)num;

        int estimatedTime = (int)(now * (rows * columns * numberOfRuns));

        if (estimatedTime > 3600) {
            System.out.println("Estimated time: " + (estimatedTime/3600) + " hour(s).");
        } else if (estimatedTime > 60) {
            System.out.println("Estimated time: " + (estimatedTime/60) + " minute(s).");
        } else {
            System.out.println("Estimated time: " + estimatedTime + " second(s).");
        }

    }

    private void loadGeneticAlgorithmResults () {

        long before = System.nanoTime();
        doLoadGeneticAlgorithmResults();
        long now = System.nanoTime();
        long delta = (now - before) / 1_000_000_000L;

        if (delta > 3600) {
            System.out.println("Time Elapsed: " + (delta/3600) + " hour(s).");
        } else if (delta > 60) {
            System.out.println("Time Elapsed: " + (delta/60) + " minute(s).");
        } else {
            System.out.println("Time Elapsed: " + delta + " second(s).");
        }

    }

    private void doLoadGeneticAlgorithmResults () {

        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[0].length; x++) {

                int avgResult = 0;
                for (int i = 0; i < numberOfRuns; i++) {
                    geneticAlgorithm.reset();
                    geneticAlgorithm.setCrossoverRate(cStart + (x*cIncrement));
                    geneticAlgorithm.setMutationRate(mStart + (y*mIncrement));
                    geneticAlgorithm.run();
                    int result = geneticAlgorithm.getAreaUnderBestDistances();
                    avgResult += result;
                }

                avgResult = avgResult / numberOfRuns;

                if (avgResult > maxValue) {
                    maxValue = avgResult;
                }
                if (avgResult < minValue) {
                    minValue = avgResult;
                }

                array[y][x] = avgResult;
            }
        }
        System.out.println("Completed.");
    }

    private String getBest () {
        StringBuilder sb = new StringBuilder();
        sb.append("Best Value(s) found at:");
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array.length; x++) {
                if (array[y][x] == minValue) {
                    sb.append("\n    Crossover Rate:  " + (cStart + (x*cIncrement)));
                    sb.append("\n     Mutation Rate:  " + (mStart + (y*mIncrement)));
                    sb.append("\n        Coordinate:  (" + x + ", " + y + ")");
                }
            }
        }
        return new String(sb);
    }

    private String getWorst () {
        StringBuilder sb = new StringBuilder();
        sb.append("Worst Value(s) found at:\n");
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array.length; x++) {
                if (array[y][x] == maxValue) {
                    sb.append("   Crossover Rate:  " + (cStart + (x*cIncrement)) + "\n");
                    sb.append("    Mutation Rate:  " + (mStart + (y*mIncrement)) + "\n");
                    sb.append("       Coordinate:  (" + x + ", " + y + ")\n");
                }
            }
        }
        return new String(sb);
    }

    int[][] getResults () {
        return array.clone();
    }

    int getMinValue () {
        return minValue;
    }

    int getMaxValue () {
        return maxValue;
    }

    private void showInWindow () {
        WindowHeatMap windowHeatMap = new WindowHeatMap(this);
        windowHeatMap.setScale(scale);
        windowHeatMap.run();
    }

    private void printResults () {
        for (int[] y : array) {
            for (int x : y) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }

    private static void showInputValuesInWindow (int scale) {
        HeatMap heatMap = new HeatMap();
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < 15; i++) {
            if (i == 0) {
                sc.nextLine();
            } else {
                System.out.println(sc.nextLine());
            }
        }
        String[] s = sc.nextLine().split(" ");
        int rows = Integer.parseInt(s[0]);
        int columns = Integer.parseInt(s[3]);

        heatMap.array = new int[rows][columns];

        // The 24th line is where the Utility values are.
        for (int i = 0; i < 7; i++) {
            System.out.println(sc.nextLine());
        }

        sc.nextLine();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int input = sc.nextInt();
                heatMap.array[r][c] = input;
                if (input > heatMap.maxValue) {
                    heatMap.maxValue = input;
                }
                if (input < heatMap.minValue) {
                    heatMap.minValue = input;
                }
            }
        }

        sc.nextLine();
        while (sc.hasNextLine()) {
            System.out.println(sc.nextLine());
        }

        heatMap.setScale(scale);
        heatMap.showInWindow();
    }

    private static void print (int[][] array) {
        for (int[] y : array) {
            for (int x : y) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }

    private static void outputHeatMap () {
        GeneticAlgorithm geneticAlgorithm = Preset.getDefaultGA();

        geneticAlgorithm.run();
        geneticAlgorithm.printProperties();
        System.out.println("-------------Heat Map Information---------------");
        generateHeatMap(geneticAlgorithm);
    }

    private static void generateHeatMap (GeneticAlgorithm geneticAlgorithm) {
        HeatMap heatMap = new HeatMap(geneticAlgorithm);
        heatMap.setCrossoverRange(0.90, 1.00, 0.02);
        heatMap.setMutationRange(0.00, 0.10, 0.02);
        heatMap.setNumberOfRuns(5);
        heatMap.run();
        heatMap.setScale(4);
        System.out.println("---------------Heat Map Values------------------");
        heatMap.printResults();
        System.out.println("---------------Heat Map Results-----------------");
        System.out.println(heatMap.getBest());
        System.out.print(heatMap.getWorst());
        System.out.println("-------------------Finished--------------------");
    }

    public static void main (String[] args) {

        if (args.length == 0) {
            System.out.println("Mode: Outputting Heat Map.");
            outputHeatMap();
            return;
        }

        if (args.length == 1) {
            System.out.println("Mode: Inputting Heat Map.");
            try {
                int scale = Integer.parseInt(args[0]);
                HeatMap.showInputValuesInWindow(scale);
                return;
            } catch (Exception ex) {
                System.out.println("Invalid parameter.");
                System.out.println("Parameter must be an integer between 0 and 20.");
                return;
            }
        }

        System.out.println("Invalid parameters.");
        System.out.println("Parameter must be an integer between 0 and 20.");
    }
}