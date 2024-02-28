package compiler.language;

import compiler.parser.ASTNode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public abstract class Assignment extends Instruction {
    protected final @NotNull String name;

    public Assignment(@NotNull ASTNode node) {
        super(node.getPosition());
        this.name = node.getValue().toString();
    }

    @Override
    public String toString() {
        return "Assignment of %s %s"
                .formatted(name, super.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Assignment variable)) return false;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}