package gui.elements;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.Point;

@Getter
@Setter
@AllArgsConstructor
public class Location  {

    private int x;
    private int y;

    public Point toPoint() {
        return new Point(x, y);
    }

}
