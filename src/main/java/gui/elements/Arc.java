package gui.elements;

import gui.elements.utils.DrawPositionUtils;
import gui.ValueNameComponent;
import lombok.*;

import java.awt.*;

import static gui.elements.utils.Constants.*;
import static gui.elements.utils.LocationUtils.*;

@Getter
@Setter
@NoArgsConstructor
public class Arc implements ValueNameComponent {
    private Node origin;
    private Node destination;
    private String name;
    private int value;
    private Color color;
    private Rectangle textPosition = new Rectangle();


    public void render(Graphics g) {
        handleLineDrawing(g);
        handleValueDrawing(g);
    }

    private void handleLineDrawing(Graphics g) {
        Location lineOrigin = this.origin instanceof Point ?
                getPointAbsolutePosition(this.origin.getLocation()) :
                getTransitionAbsolutePosition(this.origin.getLocation());

        Location lineDestination = this.origin instanceof Point ?
                getTransitionAbsolutePosition(this.destination.getLocation()) :
                getPointAbsolutePosition(this.destination.getLocation());

        Double angleRad = transformNodesOrigins(lineOrigin, lineDestination);

        g.setColor(Color.BLACK);
        if (destination instanceof Transition && ((Transition) destination).canRun()) {
            g.setColor(destination.getColor());
        }

        g.drawLine(lineOrigin.getX(), lineOrigin.getY(), lineDestination.getX(), lineDestination.getY());

        handleArrowDrawing(g, lineDestination, angleRad);
    }

    private Double transformNodesOrigins(Location lineOrigin, Location lineDestination) {
        double angleRad = 0.0;
        if (this.origin instanceof Point) {
            angleRad = getAngle(this.origin.getLocation(), this.destination.getLocation());
            lineOrigin.setX((int) (lineOrigin.getX() + (POINT_WIDTH / 2 * Math.cos(angleRad))));
            lineOrigin.setY((int) (lineOrigin.getY() + (POINT_HEIGHT / 2 * Math.sin(angleRad))));
        }

        if (this.destination instanceof Point) {
            angleRad = getAngle(this.origin.getLocation(), this.destination.getLocation());
            lineDestination.setY((int) (lineDestination.getY() - (POINT_HEIGHT / 2 * Math.sin(angleRad))));
            lineDestination.setX((int) (lineDestination.getX() - (POINT_WIDTH / 2 * Math.cos(angleRad))));
        }

        if (this.origin instanceof Transition) {
            angleRad = getAngle(this.origin.getLocation(), this.destination.getLocation());
            lineOrigin.setX((int) (lineOrigin.getX() + (TRANSITION_WIDTH / 2 * Math.cos(angleRad))));
            lineOrigin.setY((int) (lineOrigin.getY() + (TRANSITION_WIDTH / 2 * Math.sin(angleRad))));
        }

        if (this.destination instanceof Transition) {
            angleRad = getAngle(this.origin.getLocation(), this.destination.getLocation());
            lineDestination.setY((int) (lineDestination.getY() - (TRANSITION_WIDTH / 2 * Math.sin(angleRad))));
            lineDestination.setX((int) (lineDestination.getX() - (TRANSITION_WIDTH / 2 * Math.cos(angleRad))));
        }

        return angleRad;
    }

    private void handleArrowDrawing(Graphics g, Location lineDestination, double angleRad) {
        Polygon polygon = new Polygon();
        polygon.addPoint(lineDestination.getX(), lineDestination.getY());
        Location left = getLeftArrowPoint(lineDestination, angleRad);
        Location right = getRightArrowPoint(lineDestination, angleRad);
        polygon.addPoint(left.getX(), left.getY());
        polygon.addPoint(right.getX(), right.getY());
        g.fillPolygon(polygon);
    }

    private void handleValueDrawing(Graphics g) {
        String name = this.name + ": " + value;
        int x = getArcMiddleX(this.origin.getLocation(), this.destination.getLocation());
        int y = getArcMiddleY(this.origin.getLocation(), this.destination.getLocation());
        g.drawString(name, x, y);

        textPosition = DrawPositionUtils.getTextRectangle(g, name, x, y);
    }

    private double getAngle(Location center, Location point) {
        return Math.atan2(point.getY() - center.getY(), point.getX() - center.getX());
    }
}
