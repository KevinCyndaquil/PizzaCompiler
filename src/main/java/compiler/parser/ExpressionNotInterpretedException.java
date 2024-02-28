package compiler.parser;

import compiler.lexical.Token;
import lombok.NonNull;

public class ExpressionNotInterpretedException extends RuntimeException {
    public ExpressionNotInterpretedException(@NonNull Token notInterpretedToken) {
        super(("Syntax Error: Expression '%s' didn't recognize " +
                "by syntax interpreter at %s")
                .formatted(notInterpretedToken.value(),
                        notInterpretedToken.position().y));
    }
}
