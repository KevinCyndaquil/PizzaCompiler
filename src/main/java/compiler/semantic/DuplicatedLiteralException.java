package compiler.semantic;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;

public class DuplicatedLiteralException extends RuntimeException {

    public DuplicatedLiteralException(
            @NotNull ASTNode duplicated,
            @NotNull ASTNode origin) {
        super(("Semantic Error: Declaration duplicated of %s located at %s, " +
                "the first declaration is at %s")
                .formatted(
                        duplicated.getValue(),
                        duplicated.getPosition(),
                        origin.getPosition()));
    }
}