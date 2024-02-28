package compiler.semantic;

import compiler.language.Var;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;

public class TypeInvalidException extends RuntimeException {
    public TypeInvalidException(@NotNull Var invalidNode, Expressions expected) {
        super("Semantic Error: Use of %s named %s is incorrect, located at %s. Expected %s instead"
                .formatted(
                        invalidNode.getClass().getSimpleName().toUpperCase(),
                        invalidNode.getName(),
                        invalidNode.getDeclarationPosition(),
                        expected));
    }
}
