package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;

public class PizzaDefinitionException extends RuntimeException {
    public PizzaDefinitionException(@NotNull ASTNode invalidNode, Expressions expected) {
        super("Semantic Error: Use of %s named %s is incorrect, located at %s. Expected %s instead"
                .formatted(
                        invalidNode.getType(),
                        invalidNode.getValue(),
                        invalidNode.getPosition(),
                        expected));
    }
}
