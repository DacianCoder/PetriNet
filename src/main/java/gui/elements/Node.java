package gui.elements;

import gui.ValueNameComponent;
import gui.elements.utils.Constants;
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
    private Color color = Color.BLACK;
    protected Rectangle textPosition = new Rectangle();
    @Setter(value = AccessLevel.NONE)
    private Rectangle nodeSpace;

    public abstract void render(Graphics g);

    public void setLocation(Location location) {
        this.location = location;
        this.nodeSpace = new Rectangle(location.toPoint(), Constants.MAX_NODE_AREA);
    }


}
