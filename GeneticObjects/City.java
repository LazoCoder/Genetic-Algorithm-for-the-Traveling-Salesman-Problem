package GeneticObjects;

import java.util.Random;

/**
 * Represents a city in the Traveling Salesman Problem.
 * Immutable.
 */
public class City {

    private String name;
    private int x, y;

    /**
     * Constructs the City.
     * @param name  the name of the city
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    public City (String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName () {
        return name;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    /**
     * Create a City with a random name and random location.
     * @param random    the Random object to be used for the generation
     * @return          a Randomly generated City
     */
    public static City getRandomCity (Random random) {
        String name = getRandomName(random);
        int x = random.nextInt(500);
        int y = random.nextInt(500);
        return new City(name, x, y);
    }

    /**
     * Helper method to generate a random name for the random City generator.
     * @param random    the Random object to be used for the generation
     * @return          random letters
     */
    private static String getRandomName (Random random) {

        // Create an array with random integers.
        int[] name = new int[random.nextInt(5)+3];
        for (int i = 0; i < name.length; i++) {
            name[i] = random.nextInt(26) + 65;
        }

        // Convert the integers in the array to chars.
        // Add each char to StringBuilder.
        StringBuilder sb = new StringBuilder();
        for (int i : name) {
            sb.append((char) i);
        }

        return new String(sb);
    }

    /**
     * Finds the Euclidean distance between two cities.
     * @param city1     the first city
     * @param city2     the second city
     * @return          the distance
     */
    public static double distance (City city1, City city2) {

        int x1 = city1.getX();
        int y1 = city1.getY();

        int x2 = city2.getX();
        int y2 = city2.getY();

        int xDiff = x2 - x1;
        int yDiff = y2 - y1;

        return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (x != city.x) return false;
        if (y != city.y) return false;
        return name.equals(city.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + x + ", " + y + ")";
    }
}
