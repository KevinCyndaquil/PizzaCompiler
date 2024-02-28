package compiler.language;

import compiler.parser.ASTNode;
import compiler.util.CPoint;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public abstract class Var extends Expression {
    protected final @NotNull String name;

    public Var(@NotNull ASTNode node) {
        super(node.getPosition());
        this.name = node.getValue().toString();
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