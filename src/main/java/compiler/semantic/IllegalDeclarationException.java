package compiler.semantic;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class IllegalDeclarationException extends RuntimeException {
    public IllegalDeclarationException(String message) {
        super(message);
    }

    @Contract("_ -> new")
    public static @NotNull IllegalDeclarationException addingIngredientError(@NotNull ASTNode addNode) {
        return new IllegalDeclarationException(
                "Two or more adding of ingredients at %s in %s"
                        .formatted(addNode.getPosition(), addNode.root().getValue()));
    }
}
