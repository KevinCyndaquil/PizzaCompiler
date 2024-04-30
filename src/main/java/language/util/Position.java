package language.util;

import org.jetbrains.annotations.NotNull;
import program.Program;

import java.awt.*;

/**
 * Defines a position in any code or Program.
 */
public class Position extends Point {
    public Program program;

    public Position(int x, int y, Program program) {
        super(x, y);
        this.program = program;
    }

    public Position(@NotNull Point point, Program program) {
        this(point.x, point.y, program);
    }

    public Position(Program program) {
        this(0, 0, program);
    }

    public Position create() {
        return new Position(this, program);
    }

    @Override
    public String toString() {
        return "[row=%s; column=%s] in %s"
                .formatted(y + 1, x + 1, program.getPath());
    }
}
