package compiler.semantic;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;

public class ImageZeroSizeException extends RuntimeException {
    public ImageZeroSizeException(@NotNull ASTNode literalNode) {
        super("Ingredient %s must have a dimension greater than zero located at %s"
                .formatted(literalNode.getValue(), literalNode.getPosition()));
    }
}
