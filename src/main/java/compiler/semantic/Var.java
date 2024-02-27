package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.util.CPoint;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Var {
    protected final ASTNode node;
    protected final @NotNull String name;
    protected final @NotNull CPoint declarationPosition;

    public Var(@NotNull ASTNode node) {
        this.node = node;
        this.name = node.getValue();
        this.declarationPosition = node.getPosition();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Var variable)) return false;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}