package gui;

import lombok.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static gui.LocationUtils.*;

@Data
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

        g.drawLine(lineOrigin.getX(), lineOrigin.getY(), lineDestination.getX(), lineDestination.getY());
        //TODO draw an actual triangle
//        g.fillRect(lineDestination.getX() - 5, lineDestination.getY() - 5, 10, 10);
    }


    private void handleValueDrawing(Graphics g) {
        String name = this.name + ": " + value;
        int x = getArcMiddleX(this.origin.getLocation(), this.destination.getLocation());
        int y = getArcMiddleY(this.origin.getLocation(), this.destination.getLocation());
        g.drawString(name, x, y);

        textPosition = DrawPositionUtils.getTextRectangle(g, name, x, y);
    }


    public double getAngle(Location target, Location origin) {
        double angle = Math.toDegrees(Math.atan2(target.getY() - origin.getY(), target.getX() - origin.getY()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }
}
