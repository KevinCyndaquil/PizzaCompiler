package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import language.types.Assignment;
import language.util.CodePosition;
import org.jetbrains.annotations.NotNull;

public class IllegalDefinitionException extends RuntimeException {
    public IllegalDefinitionException(@NotNull ASTNode invalidNode, Expressions expected) {
        super("Semantic Error: Use of %s named %s is incorrect, located at %s. Expected %s instead"
                .formatted(
                        invalidNode.getType(),
                        invalidNode.getValue(),
                        invalidNode.getPosition(),
                        expected));
    }

    public IllegalDefinitionException(@NotNull Assignment invalidAssignment, CodePosition usedAt, String cause) {
        super("Semantic Error: Use of %s named %s is incorrect, located at %s.%s"
                .formatted(
                        invalidAssignment.getClass().getSimpleName(),
                        invalidAssignment.getName(),
                        usedAt,
                        cause == null ? "" : " Because %s".formatted(cause)));
    }
}
