package gui;


import lombok.Data;

import java.awt.*;

import static gui.Constants.*;

@Data
public class LocationUtils {

    public static Location getPointAbsolutePosition(Location location) {
        return new Location(location.getX() + (POINT_WIDTH / 2), location.getY() + (POINT_HEIGHT / 2));
    }

    public static Location getTransitionAbsolutePosition(Location location) {
        return new Location(location.getX() + (TRANSITION_WIDTH / 2), location.getY() + (TRANSITION_HEIGHT / 2));
    }

    public static boolean isInPointArea(Location point, Location location) {
        return Math.abs(location.getY() - point.getY()) <= POINT_HEIGHT &&
                Math.abs(location.getX() - point.getX()) <= POINT_WIDTH;
    }

    public static boolean isInTransitionArea(Location transition, Location location) {
        return Math.abs(location.getX() - transition.getX()) <= TRANSITION_WIDTH &&
                Math.abs(location.getY() - transition.getY()) <= TRANSITION_HEIGHT;
    }


    public static int getArcMiddleX(Location origin, Location destination) {
        return (origin.getX() + destination.getX()) / 2;
    }

    public static int getArcMiddleY(Location origin, Location destination) {
        return (origin.getY() + destination.getY()) / 2;
    }

    public static boolean isInArcTextArea(Arc arc, Location location) {
        return arc.getTextPosition().intersects(new Rectangle(location.getX() - 5, location.getY() - 5,
                (int) arc.getTextPosition().getWidth() + 10, (int) arc.getTextPosition().getHeight() + 10));
    }

    public static boolean isNodeNameClicked(Node node, Location location) {
        return node.getTextPosition().intersects(new Rectangle(location.getX() - 5, location.getY() - 5,
                (int) node.getTextPosition().getWidth() + 10, (int) node.getTextPosition().getHeight() + 10));
    }
}
