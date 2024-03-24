package language.util;

import java.awt.*;

public class Position extends Point {

    public Position(int x, int y) {
        super(x, y);
    }

    public Position(Point point) {
        super(point);
    }

    public Position() {
        this(0, 0);
    }

    @Override
    public String toString() {
        return "[row=%s; column=%s]"
                .formatted(y + 1, x + 1);
    }
}
