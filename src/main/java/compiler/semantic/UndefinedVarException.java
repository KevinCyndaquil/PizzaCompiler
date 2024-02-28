package compiler.semantic;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;

public class UndefinedVarException extends RuntimeException {
    public UndefinedVarException(@NotNull ASTNode nodeUndefinedVar) {
        super("Semantic Error: %s named %s is not defined before to its use, call it at %s"
                .formatted(
                        nodeUndefinedVar.getType(),
                        nodeUndefinedVar.getValue(),
                        nodeUndefinedVar.getPosition()));
    }
}
