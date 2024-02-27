package compiler.util;

import java.awt.*;

public class CPoint extends Point {

    public CPoint(int x, int y) {
        super(x, y);
    }

    public CPoint(Point point) {
        super(point);
    }

    public CPoint() {
        this(0, 0);
    }

    @Override
    public String toString() {
        return "[row=%s; column=%s]"
                .formatted(y + 1, x + 1);
    }
}
