package gui.elements;

import gui.elements.utils.DrawPositionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static gui.elements.utils.Constants.*;

@Getter
@Setter
@AllArgsConstructor
public class Point extends Node {
    public void render(Graphics g) {
        g.setColor(this.getColor() != null ? this.getColor() : Color.BLACK);
        g.drawOval(getLocation().getX(), getLocation().getY(), POINT_WIDTH, POINT_HEIGHT);
        handleTextDrawing(g);
    }

    private void handleTextDrawing(Graphics g) {
        g.drawString(getValue() + "", getLocation().getX() + (POINT_WIDTH / 2), getLocation().getY() + (POINT_HEIGHT * 2 / 3));
        int nameX = getLocation().getX() + TEXT_SPACING_X;
        int nameY = getLocation().getY() - TEXT_SPACING_Y;
        g.drawString(getName(), nameX, nameY);

        textPosition = DrawPositionUtils.getTextRectangle(g, getName(), nameX, nameY);
    }
}
