package gui;

import lombok.experimental.UtilityClass;

import java.awt.*;
import java.awt.geom.Rectangle2D;

@UtilityClass
public class DrawPositionUtils {
    public static Rectangle getTextRectangle(Graphics g, String name, int nameX, int nameY) {
        Rectangle2D stringRect = g.getFontMetrics().getStringBounds(name, g);
        Rectangle rectangle = new Rectangle();
        rectangle.x = nameX - ((int) stringRect.getWidth() / 2);
        rectangle.height = (int) stringRect.getHeight();
        rectangle.y = nameY - ((int) stringRect.getHeight()) + (int) stringRect.getY();
        rectangle.width = (int) stringRect.getWidth();

        return rectangle;
    }
}
