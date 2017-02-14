package Utility;

import javax.swing.*;
import java.awt.*;

class WindowHeatMap extends JFrame {

    private int width, height;
    private int rows, columns;
    private int maxValue, minValue;
    private int[][] array;
    private int scale = 1;
    private HeatMap heatMap;

    WindowHeatMap (HeatMap heatMap) {
        this.heatMap = heatMap;
        array = heatMap.getResults();
    }

    void run () {
        maxValue = heatMap.getMaxValue();
        minValue = heatMap.getMinValue();
        setProperties();
        createPanel();
        setWindowProperties();
    }

    void setScale (int scale) {
        if (scale < 0 || scale > 20) {
            throw new IllegalArgumentException("Scale must be between 0 and 20, inclusive.");
        }
        this.scale = scale;
    }

    private void setProperties () {
        rows = array.length;
        columns = array[0].length;
        height = rows * scale;
        width = columns * scale;
    }

    private void debug () {
        System.out.println("Rows:    " + rows);
        System.out.println("Columns: " + columns);
        System.out.println("Scale:   " + scale);
        System.out.println("Height:  " + height);
        System.out.println("Width:   " + width);
    }

    private Panel createPanel () {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        setTitle("Heat Map");
        panel.setPreferredSize(new Dimension(this.width, this.height));
        return panel;
    }

    private void setWindowProperties () {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)((screenSize.getWidth()/2.0)-width/2.0);
        int y = (int)((screenSize.getHeight()/2.0)-height/2.0);
        setLocation(x, y);
        setResizable(false);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class Panel extends JPanel {

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            paintResults(graphics);
        }

        private void paintResults (Graphics graphics) {

            double colorRange = (double)(maxValue-minValue) / 255.0;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    int c =  (int)((array[y][x]-minValue)/colorRange);
                    graphics.setColor(new Color(c, c, c));
                    graphics.fillRect(x* scale, y* scale, scale, scale);
                }
            }

        }

    }
}
