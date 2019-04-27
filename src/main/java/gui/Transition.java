package gui;

import lombok.Data;

import java.awt.*;

import static gui.Constants.*;


@Data
public class Transition extends Node {
    public void render(Graphics g) {
        g.setColor(this.getColor() != null ? this.getColor() : Color.BLACK);
        g.drawRect(getLocation().getX(), getLocation().getY(), TRANSITION_WIDTH, TRANSITION_HEIGHT);
        handleTextDrawing(g);
    }

    private void handleTextDrawing(Graphics g) {
        g.drawString(getName(), getLocation().getX(), getLocation().getY() - TEXT_SPACING_Y);
        int nameY = getLocation().getY() - TEXT_SPACING_Y;
        int nameX = getLocation().getX();

        g.drawString(getName(), nameX, nameY);
        textPosition = DrawPositionUtils.getTextRectangle(g, getName(), nameX, nameY);

    }
}
