package IO;

import GeneticObjects.City;

import java.io.*;

/**
 * Helper class for reading the provided data sets and converting it to an array of City objects.
 */
public class Import {

    /**
     * Read the specified data set and return an array of City objects based on the set.
     * @param dataSet   the data set to read
     * @return          the City objects contained within the data set
     */
    public static City[] getCities (DataSet dataSet) {

        String dataSetName;
        int startingLine;

        // Determine which data set to load up and set the properties pertaining to it.
        if (dataSet == DataSet.bays29) {
            dataSetName = "bays29.tsp";
            startingLine = 38;
        } else {
            dataSetName = "att48.tsp";
            startingLine = 6;
        }

        String[] lines = read(dataSetName).split("\n");
        String[] words = lines[3].split(" ");
        int numOfCities = Integer.parseInt(words[words.length-1]);
        City[] cities = new City[numOfCities];

        // Read each line and turn it into a City.
        for (int i = startingLine; i < startingLine+numOfCities; i++) {
            String[] line = removeWhiteSpace(lines[i]).trim().split(" ");
            int x = (int)Double.parseDouble(line[1].trim());
            int y = (int)Double.parseDouble(line[2].trim());
            City city = new City(line[0], x, y);
            cities[i-startingLine] = city;
        }

        return cities;
    }

    /**
     * Removes duplicate what spaces in a String.
     * Example: "   2  3  3,2   " becomes " 2 3 3,2 "
     * @param s     the String to parse
     * @return      the String minus the duplicate white spaces
     */
    private static String removeWhiteSpace (String s) {
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == ' ' && s.charAt(i-1) == ' ') {
                if (i != s.length()) {
                    s = s.substring(0, i) + s.substring(i+1, s.length());
                    i--;
                } else {
                    s = s.substring(0, i);
                    i--;
                }
            }
        }
        return s;
    }

    /**
     * Read from a file and load it to a String.
     * @param fileName  the name of the file to read (within the same root as this class)
     * @return          a String with the contents of the file
     */
    private static String read (String fileName) {
        InputStream stream = Import.class.getResourceAsStream(fileName);
        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public enum DataSet {
        att48,
        bays29
    }

}
