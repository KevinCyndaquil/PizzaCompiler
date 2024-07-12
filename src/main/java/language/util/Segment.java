package language.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Segment {
    public final Point a;
    public final Point b;

    public Segment(int x1, int y1, int x2, int y2) {
        a = new Point(x1, y1);
        b = new Point(x2, y2);
    }

    public Segment(@NotNull Point p1, @NotNull Point p2) {
        this(p1.x, p1.y, p2.x, p2.y);
    }

    @Override
    public String toString() {
        return "Segment(A(%s, %s), B(%s, %s))".formatted(a.x, a.y, b.x, b.y);
    }
}
