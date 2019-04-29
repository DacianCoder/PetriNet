package logic;

import gui.elements.Node;
import gui.elements.Transition;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class RunNet {
    Matrix matrix;

    public RunNet(Matrix matrix) {
        this.matrix = matrix;
        matrix.getNodes().values().stream()
                .filter(Transition.class::isInstance)
                .map(Transition.class::cast)
                .forEach(t -> {
                    if (t.canRun()) {
                        t.setColor(Color.BLUE);
                    }
                });
    }

    public void validateTransitions() {
        matrix.getNodes().values().stream()
                .filter(Transition.class::isInstance)
                .map(Transition.class::cast)
                .forEach(t -> {
                    t.setColor(Color.BLACK);
                    if (t.canRun()) {
                        t.setColor(Color.BLUE);
                    }
                });
    }

    public void unvalidateTransitions() {
        for (Node t : matrix.getNodes().values()) {
            t.setColor(Color.BLACK);
        }
    }


}
