package language;

import compiler.parser.ASTNode;
import language.util.CodePosition;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * This is any instruction that could contain specific properties.
 */
@Getter
public abstract class Instruction {
    protected final @NotNull CodePosition declaredAt;
    /**
     * It's the instruction ASTNode's value.
     */
    protected final Object name;

    public Instruction(@NotNull ASTNode instructionNode) {
        this.declaredAt = instructionNode.getPosition();
        this.name = instructionNode.getValue();
    }

    @Override
    public String toString() {
        return "Instruction declared at %s".formatted(declaredAt);
    }
}
