package compiler.language;

import compiler.parser.ASTNode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class Make extends Instruction {
    private final Instruction makeInstruction;

    public Make(@NotNull ASTNode node,
                @NotNull Instruction makeInstruction) {
        super(node.getPosition());
        this.makeInstruction = makeInstruction;
    }

    @Override
    public String toString() {
        return super.toString() + " " + makeInstruction;
    }
}
