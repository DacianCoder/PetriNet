package gui.elements.utils;

import java.awt.*;

public class Constants {

    public static final int POINT_HEIGHT = 40;
    public static final int POINT_WIDTH = 40;
    public static final int TRANSITION_HEIGHT = 30;
    public static final int TRANSITION_WIDTH = 15;
    public static final int TEXT_SPACING_X = 12;
    public static final int TEXT_SPACING_Y = 12;
    public static final Dimension MAX_NODE_AREA = new Dimension(POINT_HEIGHT, POINT_WIDTH);
    public static final int ARC_DEFAULT_VALUE= 1;

    private static final int ARROW_HEAD = 10;
    public static final int ARROW_VALUE_ = (int) (ARROW_HEAD / Math.cos(Math.PI / 6));

}
