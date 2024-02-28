package compiler.semantic;

import compiler.language.Assignment;
import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;

public class TypeInvalidException extends RuntimeException {
    public TypeInvalidException(@NotNull Assignment invalidVar, Expressions expected) {
        super("Semantic Error: Use of %s named %s is incorrect, located at %s. Expected %s instead"
                .formatted(
                        invalidVar.getClass().getSimpleName().toUpperCase(),
                        invalidVar.getName(),
                        invalidVar.getDeclaredAt(),
                        expected));
    }

    public TypeInvalidException(@NotNull ASTNode invalidNode, Expressions expected) {
        super("Semantic Error: Use of %s named %s is incorrect, located at %s. Expected %s instead"
                .formatted(
                        invalidNode.getType(),
                        invalidNode.getValue(),
                        invalidNode.getPosition(),
                        expected));
    }
}
