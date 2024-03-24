package language;

import compiler.parser.ASTNode;
import language.types.Assignment;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class Make extends Instruction {
    private Assignment instruction;

    public Make(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return super.toString() + " " + instruction;
    }
}
