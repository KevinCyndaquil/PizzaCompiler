package compiler.semantic;

import compiler.language.Assignment;
import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;

public class DuplicatedVarException extends RuntimeException {

    public DuplicatedVarException(
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