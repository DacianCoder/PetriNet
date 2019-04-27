package gui;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static gui.Constants.*;


@Getter
@Setter
@NoArgsConstructor
public class Transition extends Node {
    private List<Arc> entering = new ArrayList<>();
    private List<Arc> leaving = new ArrayList<>();

    public void render(Graphics g) {
        g.setColor(this.getColor());
        if (this.getColor() != Color.BLACK) {
            g.fillRect(getLocation().getX(), getLocation().getY(), TRANSITION_WIDTH, TRANSITION_HEIGHT);
        } else {
            g.drawRect(getLocation().getX(), getLocation().getY(), TRANSITION_WIDTH, TRANSITION_HEIGHT);

        }
        handleTextDrawing(g);
    }

    private void handleTextDrawing(Graphics g) {
        g.drawString(getName(), getLocation().getX(), getLocation().getY() - TEXT_SPACING_Y);
        int nameY = getLocation().getY() - TEXT_SPACING_Y;
        int nameX = getLocation().getX();

        g.drawString(getName(), nameX, nameY);
        textPosition = DrawPositionUtils.getTextRectangle(g, getName(), nameX, nameY);
    }

    public void addComingArc(Arc arc) {
        entering.add(arc);
    }

    public void addGoingArc(Arc arc) {
        leaving.add(arc);
    }

    public boolean canRun() {
        return !entering.isEmpty() && entering.stream().noneMatch(arc -> arc.getValue() > arc.getOrigin().getValue());
    }

    public void runTransition() {
        if (!canRun()) {
            return;
        }
        entering.forEach(arc -> arc.getOrigin().setValue(arc.getOrigin().getValue() - arc.getValue()));
        leaving.forEach(arc -> arc.getDestination().setValue(arc.getDestination().getValue() + arc.getValue()));
        setColor(Color.BLACK);
    }
}
