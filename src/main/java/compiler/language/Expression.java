package compiler.language;

import compiler.util.CPoint;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Expression {
    protected final @NotNull CPoint declarationPosition;

    public Expression(@NotNull CPoint declarationPosition) {
        this.declarationPosition = declarationPosition;
    }
}
