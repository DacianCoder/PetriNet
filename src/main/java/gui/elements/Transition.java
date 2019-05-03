package gui.elements;

import gui.elements.utils.DrawPositionUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static gui.elements.utils.Constants.*;


@Getter
@Setter
@NoArgsConstructor
public class Transition extends Node {
    private List<Arc> enteringArcs = new ArrayList<>();
    private List<Arc> leavingArcs = new ArrayList<>();

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
        int nameY = getLocation().getY() - TEXT_SPACING_Y;
        int nameX = getLocation().getX() + TRANSITION_WIDTH / 4;

        g.drawString(getName(), nameX, nameY);
        textPosition = DrawPositionUtils.getTextRectangle(g, getName(), nameX, nameY);
    }

    public void addComingArc(Arc arc) {
        enteringArcs.add(arc);
    }

    public void addGoingArc(Arc arc) {
        leavingArcs.add(arc);
    }

    public boolean canRun() {
        return !enteringArcs.isEmpty() && enteringArcs.stream().noneMatch(arc -> arc.getValue() > arc.getOrigin().getValue());
    }

    public void runTransition() {
        if (!canRun()) {
            return;
        }
        enteringArcs.forEach(arc -> arc.getOrigin().setValue(arc.getOrigin().getValue() - arc.getValue()));
        leavingArcs.forEach(arc -> arc.getDestination().setValue(arc.getDestination().getValue() + arc.getValue()));
        if (!canRun()) {
            setColor(Color.BLACK);
        }
        System.out.println();
    }
}
