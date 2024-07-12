package language.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.file.Path;

/**
 * Defines a position on source code.
 */
public class CodePosition extends Point {
    public Path sourceCodePath;

    public CodePosition(int x, int y, Path sourceCodePath) {
        super(x, y);
        this.sourceCodePath = sourceCodePath;
    }

    public CodePosition(Path sourceCodePath) {
        this(0, 0, sourceCodePath);
    }

    private CodePosition(@NotNull Point point, Path sourceCodePath) {
        this(point.x, point.y, sourceCodePath);
    }

    /**
     * @return A new object with the same position and source code path. Otherwise,
     * does a copy of this object.
     */
    public CodePosition create() {
        return new CodePosition(this, sourceCodePath);
    }

    @Override
    public String toString() {
        return "[row=%s; column=%s] in %s"
                .formatted(y + 1, x + 1, sourceCodePath);
    }
}
