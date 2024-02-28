package compiler.language;

import compiler.util.CPoint;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Instruction {
    protected final @NotNull CPoint declaredAt;

    public Instruction(@NotNull CPoint declaredAt) {
        this.declaredAt = declaredAt;
    }

    @Override
    public String toString() {
        return "declared at %s".formatted(declaredAt);
    }
}
