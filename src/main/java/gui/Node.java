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
    private int value = 1;
    private Shape type;
    private Color color;
    protected Rectangle textPosition = new Rectangle();

    public abstract void render(Graphics g);
}
