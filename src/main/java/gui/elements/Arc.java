package gui.elements;

import gui.elements.utils.DrawPositionUtils;
import gui.ValueNameComponent;
import lombok.*;

import java.awt.*;

import static gui.elements.utils.Constants.POINT_HEIGHT;
import static gui.elements.utils.Constants.POINT_WIDTH;
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

        double angleRad;
//        TODO better algorithm
        if (this.origin instanceof Point) {
            angleRad = getAngle(this.origin.getLocation(), this.destination.getLocation());
            lineOrigin.setX((int) (lineOrigin.getX() + (POINT_WIDTH / 2 * Math.cos(angleRad))));
            lineOrigin.setY((int) (lineOrigin.getY() + (POINT_HEIGHT / 2 * Math.sin(angleRad))));
        }

        if (this.destination instanceof Point) {
            angleRad = getAngle(this.destination.getLocation(), this.origin.getLocation());
            lineDestination.setY((int) (lineDestination.getY() + (POINT_HEIGHT / 2 * Math.sin(angleRad))));
            lineDestination.setX((int) (lineDestination.getX() + (POINT_WIDTH / 2 * Math.cos(angleRad))));
        }

        g.setColor(Color.BLACK);
        if (destination instanceof Transition && ((Transition) destination).canRun()) {
            g.setColor(destination.getColor());
        }

        g.drawLine(lineOrigin.getX(), lineOrigin.getY(), lineDestination.getX(), lineDestination.getY());
        //TODO draw an actual triangle
        g.fillRect(lineDestination.getX() - 5, lineDestination.getY() - 5, 10, 10);
    }


    private void handleValueDrawing(Graphics g) {
        String name = this.name + ": " + value;
        int x = getArcMiddleX(this.origin.getLocation(), this.destination.getLocation());
        int y = getArcMiddleY(this.origin.getLocation(), this.destination.getLocation());
        g.drawString(name, x, y);

        textPosition = DrawPositionUtils.getTextRectangle(g, name, x, y);
    }


    public double getAngle(Location from, Location to) {
        return Math.atan2(to.getY() - from.getY(), to.getX() - from.getY());
    }
}
