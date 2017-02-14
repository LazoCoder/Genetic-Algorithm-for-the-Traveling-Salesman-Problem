package Display;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Used for graphing values to the screen. Supports multiple lines.
 * <br/>
 * The constructor takes two parameters. The first is an ArrayList of ArrayLists
 * of Integers. This ArrayList holds lines to be plotted to the graph. Each
 * line is a list of values. The second parameter is an ArrayList of Strings
 * which represents the Legend. Both ArrayLists from the parameters must be of
 * the same length. It doesn't make sense to have more items on the Legend than
 * there are Lines on the graph or vice-versa.
 * <br/>
 * Example: The first parameter (the ArrayList of ArrayLists of Integers) may
 * contain the following values: [10, 2, 3, 40], [10, 5, 7, 8], [9, 3, 0, 2].
 * Each block is an ArrayList of Integers and it represents a line on the graph.
 * This one is an ArrayList of Strings may contain ["Distance", "Average", "Total"].
 * This means that the first line is "Distance", the second line is "Average", and
 * the third line is "Total".
 */
public class WindowGraph extends JFrame {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 900 / 16*9;
    private static final int OFFSET = 150;

    private ArrayList<ArrayList<Integer>> yValues;
    private ArrayList<String> legend;

    /**
     * By default, the chart plots each value per pixel. For example if
     * the y value is 10 then it will be plotted 10 pixels above the x-axis.
     * When a value that is entered is greater than the width or height of
     * this window, all the values must be scaled down to fit.
     */
    private double yScale = 1.0;
    private double xScale = 1.0;
    private int maxY = 0;
    private int minY = Integer.MAX_VALUE;

    /**
     * Construct WindowGraph.
     * @param yValues   the values to be plotted
     * @param legend    the the identifier of each set of values,
     *                  example: legend.get(0) is the name of yValues.get(0).
     */
    public WindowGraph (ArrayList<ArrayList<Integer>> yValues, ArrayList<String> legend) {

        for (int i = 1; i < yValues.size(); i++) {
            if (yValues.get(i).size() != yValues.get(i-1).size()) {
                throw new IllegalArgumentException("All lists must" +
                        " contain the same number of elements.");
            }
        }

        if (yValues.size() != legend.size()) {
            throw new IllegalArgumentException("All lists must" +
                    " contain the same number of elements.");
        }

        this.yValues = yValues;
        this.legend = legend;
        setScale(yValues);
        createPanel();
        setWindowProperties();
    }

    /**
     * Sets the scale of graph so that none of the values are drawn
     * outside of the window.
     * @param yValues   the values to plot
     */
    private void setScale (ArrayList<ArrayList<Integer>> yValues) {

        xScale = ((double)yValues.get(0).size()) / ((double)WindowGraph.WIDTH-OFFSET);

        for (ArrayList<Integer> list : yValues) {
            for (Integer y : list) {

                if (y > maxY && y > WindowGraph.HEIGHT - OFFSET) {
                    yScale = ((double) y / ((double) WindowGraph.HEIGHT - OFFSET));
                    maxY = y;
                }

                if (y < minY) {
                    minY = y;
                }

            }
        }
    }

    private Panel createPanel () {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        setTitle("Lazo's Graphing Tool");
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        return panel;
    }

    private void setWindowProperties () {
        setLocation(700, 0);
        setResizable(false);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * All the drawing is done here.
     */
    private class Panel extends JPanel {

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            paintGraph(graphics2D);
        }

        private void paintGraph (Graphics2D graphics2D) {
            paintAxisLines(graphics2D);
            paintTitle(graphics2D);
            paintVerticalLabels(graphics2D);
            paintHorizontalLabels(graphics2D);
            paintPlots(graphics2D);
            paintLegend(graphics2D);
        }

        private void paintAxisLines (Graphics graphics) {
            int o = OFFSET/2;
            graphics.drawLine(o, o, o, WindowGraph.HEIGHT-o);
            graphics.drawLine(o, WindowGraph.HEIGHT-o, WindowGraph.WIDTH-o, WindowGraph.HEIGHT-o);
        }

        private void paintTitle (Graphics2D graphics) {
            String s = "Evaluation per Generation";
            graphics.drawString(s, WindowGraph.WIDTH/2
                            - getFontMetrics(graphics.getFont()).stringWidth(s)/2,
                    (int)(WindowGraph.HEIGHT * 0.05));
        }

        private void paintVerticalLabels (Graphics2D graphics) {
            final int TOTAL_LABELS = 20;
            int startX = OFFSET/2 - 70;
            int startY = OFFSET/2 + 5;
            int step = maxY / TOTAL_LABELS;

            for (int i = 0; i < TOTAL_LABELS; i++) {
                int steps = i * step;
                graphics.drawString(maxY-steps
                        + "", startX, startY + (int)(steps/yScale));
            }

            // Draw dashes.
            for (int i = 0; i < TOTAL_LABELS; i++) {
                int steps = i * step;
                int x = startX;
                int y = startY + (int)(steps/yScale);
                graphics.drawLine(x+65, y-5, x+75, y-5);
            }
        }

        private void paintHorizontalLabels (Graphics2D graphics) {
            final int TOTAL_LABELS = 10;
            int startX = OFFSET/2;
            int startY = WindowGraph.HEIGHT - OFFSET/2;
            int step = yValues.get(0).size() / TOTAL_LABELS;

            for (int i = 0; i < TOTAL_LABELS+1; i++) {
                int steps = i * step;
                String s = (steps) + "";
                graphics.drawString(s, startX + (int)(steps/xScale)
                        - getFontMetrics(graphics.getFont()).stringWidth(s)/2, startY+20);
            }

            // Draw dashes.
            for (int i = 1; i < TOTAL_LABELS+1; i++) {
                int steps = i * step;
                int x = startX + (int)(steps/xScale);
                int y = startY;
                graphics.drawLine(x, y, x, y-10);
            }

            String s = "Generation";
            graphics.drawString(s, WindowGraph.WIDTH/2
                    - getFontMetrics(graphics.getFont()).stringWidth(s)/2,
                    (int)(WindowGraph.HEIGHT * 0.95));
        }

        private void paintPlots (Graphics2D graphics2D) {
            for (int i = 0; i < yValues.size(); i++) {
                graphics2D.setColor(getHue(i, yValues.size()));
                paintPlot(graphics2D, yValues.get(i));
            }
        }

        private void paintPlot (Graphics2D graphics, ArrayList<Integer> yValues) {

            int x = 0;
            int r = 1; // Radius.
            int o = OFFSET/2;

            int x1, y1, x2, y2;

            x1 = (int)(++x/ xScale)+o+r-r;
            y1 = WindowGraph.HEIGHT-o-(int)(yValues.get(0)/ yScale)-r;
            graphics.fillOval(x1, y1,r*2, r*2);

            for (int i = 1; i < yValues.size(); i++) {

                x1 = (int)(++x/ xScale)+o+r-r;
                y1 = WindowGraph.HEIGHT-o-(int)(yValues.get(i)/ yScale)-r;
                x2 = (int)((x-1)/ xScale)+o+r-r;
                y2 = WindowGraph.HEIGHT-o-(int)(yValues.get(i-1)/ yScale)-r;

                graphics.fillOval(x1, y1,r*2, r*2);
                graphics.drawLine(x1+r, y1+r, x2+r, y2+r);
            }
        }

        private void paintLegend (Graphics2D graphics) {
            int startX = (int)((double)WindowGraph.WIDTH * 0.7);
            int startY = OFFSET/5;

            graphics.setColor(Color.BLACK);
            graphics.drawString("Legend:", startX, startY);

            for (int i = 0; i < legend.size(); i++) {
                int steps = 13 * (i+1);
                graphics.setColor(getHue(i, legend.size()));
                graphics.drawString(legend.get(i), startX, startY+steps);
            }
        }

        private Color getHue (int indexOfHue, int totalUniqueHues) {
            float steps = 1.0f / (float)totalUniqueHues;
            return Color.getHSBColor(steps * indexOfHue, 1.0f, 0.7f);
        }


    }

    public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text)
    {
        g2d.translate((float)x,(float)y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text,0,0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float)x,-(float)y);
    }
}
