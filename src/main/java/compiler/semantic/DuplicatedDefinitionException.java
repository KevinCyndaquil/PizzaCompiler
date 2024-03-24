package compiler.semantic;

import language.types.Assignment;
import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;

public class DuplicatedDefinitionException extends RuntimeException {

    public DuplicatedDefinitionException(
            @NotNull ASTNode duplicated,
            @NotNull Assignment origin) {
        super(("Semantic Error: Declaration duplicated of %s located at %s, " +
                "the first declaration is at %s")
                .formatted(
                        duplicated.getValue(),
                        duplicated.getPosition(),
                        origin.getDeclaredAt()));
    }
}