package Display;

import GeneticObjects.Chromosome;
import GeneticObjects.City;

import javax.swing.*;
import java.awt.*;

/**
 * Draws the cities to the screen, as well as a path.
 */
public class WindowTSP extends JFrame {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600 / 16*9;
    private static final int OFFSET = 40;
    private static final int CITY_SIZE = 6;

    private Panel panel;
    private City[] cities;
    private Chromosome chromosome;
    private int maxX, maxY;
    private double scaleX, scaleY;

    /**
     * Construct the WindowTSP and draw the cities to the screen.
     * @param cities    the cities to draw to the screen
     */
    public WindowTSP(City[] cities) {
        this.cities = cities;
        setScale();
        panel = createPanel();
        setWindowProperties();
    }

    /**
     * Draw a path through the city.
     * @param chromosome    the Chromosome containing the path
     */
    public void draw (Chromosome chromosome) {
        this.chromosome = chromosome;
        panel.repaint();
    }

    private Panel createPanel () {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        return panel;
    }

    private void setWindowProperties () {
        int sWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2;
        int sHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2;
        int x = sWidth - (WIDTH / 2);
        int y = sHeight - (HEIGHT / 2);
        setLocation(x, y);
        setResizable(false);
        pack();
        setTitle("Traveling Salesman Problem");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Sets the scale for the drawing so that all the cities
     * are drawn inside the window.
     */
    private void setScale () {
        for (City c : cities) {
            if (c.getX() > maxX) {
                maxX = c.getX();
            }
            if (c.getY() > maxY) {
                maxY = c.getY();
            }
        }
        scaleX = ((double)maxX) / ((double)WIDTH- OFFSET);
        scaleY = ((double)maxY) / ((double)HEIGHT- OFFSET);
    }

    /**
     * All the drawing is done here.
     */
    private class Panel extends JPanel {

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            paintTravelingSalesman((Graphics2D)graphics);
        }

        private void paintTravelingSalesman (Graphics2D graphics) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintCityNames(graphics);
            if (chromosome != null) {
                paintChromosome(graphics);
            }
            paintCities(graphics);
        }

        private void paintChromosome (Graphics2D graphics) {

            graphics.setColor(Color.darkGray);
            City[] array = chromosome.getArray();

            for (int i = 1; i < array.length; i++) {
                int x1 = (int)(array[i-1].getX() / scaleX + OFFSET / 2);
                int y1 = (int)(array[i-1].getY() / scaleY + OFFSET / 2);
                int x2 = (int)(array[i].getX() / scaleX + OFFSET / 2);
                int y2 = (int)(array[i].getY() / scaleY + OFFSET / 2);
                graphics.drawLine(x1, y1, x2, y2);
            }

            int x1 = (int)(array[0].getX() / scaleX + OFFSET / 2);
            int y1 = (int)(array[0].getY() / scaleY + OFFSET / 2);
            int x2 = (int)(array[array.length-1].getX() / scaleX + OFFSET / 2);
            int y2 = (int)(array[array.length-1].getY() / scaleY + OFFSET / 2);
            graphics.drawLine(x1, y1, x2, y2);

        }

        private void paintCities (Graphics2D graphics) {
            graphics.setColor(Color.darkGray);
            for (City c : cities) {
                int x = (int)((c.getX()) / scaleX - CITY_SIZE/2 + OFFSET / 2);
                int y = (int)((c.getY()) / scaleY - CITY_SIZE/2 + OFFSET / 2);
                graphics.fillOval(x, y, CITY_SIZE, CITY_SIZE);
            }
        }

        private void paintCityNames (Graphics2D graphics) {
            graphics.setColor(new Color(200, 200, 200));
            for (City c : cities) {
                int x = (int)((c.getX()) / scaleX - CITY_SIZE/2 + OFFSET/2);
                int y = (int)((c.getY()) / scaleY - CITY_SIZE/2 + OFFSET/2);
                graphics.fillOval(x, y, CITY_SIZE, CITY_SIZE);
                int fontOffset = getFontMetrics(graphics.getFont()).stringWidth(c.getName())/2-2;
                graphics.drawString(c.getName(), x-fontOffset, y-3);
            }
        }
    }

}
