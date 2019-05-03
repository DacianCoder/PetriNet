package gui.elements.utils;

import gui.elements.Location;

import java.awt.*;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class Constants {

    public static final int POINT_HEIGHT = 50;
    public static final int POINT_WIDTH = 50;
    public static final int TRANSITION_HEIGHT = 30;
    public static final int TRANSITION_WIDTH = 30;
    public static final int TEXT_SPACING_X = 20;
    public static final int TEXT_SPACING_Y = 12;
    public static final Dimension MAX_NODE_AREA = new Dimension(POINT_HEIGHT, POINT_WIDTH);
    public static final int ARC_DEFAULT_VALUE = 1;

    private static final int ARROW_HEAD = 15;
    private static final double ARROW_ANGLE = PI / 7;

    public static final Location getLeftArrowPoint(Location location, Double angle) {

        double currentAngle = angle - PI;
        double angleLeft = currentAngle + ARROW_ANGLE;
        double x = location.getX() + ARROW_HEAD * cos(angleLeft);
        double y = location.getY() + ARROW_HEAD * sin(angleLeft);

        return new Location((int) x, (int) y);
    }

    public static final Location getRightArrowPoint(Location location, Double angle) {
        double currentAngle = angle - PI;
        double angleLeft = currentAngle - ARROW_ANGLE;
        double x = location.getX() + ARROW_HEAD * cos(angleLeft);
        double y = location.getY() + ARROW_HEAD * sin(angleLeft);

        return new Location((int) x, (int) y);
    }

}
