package gui;

import gui.enums.Shape;
import lombok.*;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Node implements ValueNameComponent {
    private Location location;
    private String name;
    private int value = 0;
    private Shape type;
    private Color color=Color.BLACK;
    protected Rectangle textPosition = new Rectangle();

    public abstract void render(Graphics g);
}
