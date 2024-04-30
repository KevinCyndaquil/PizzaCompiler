package compiler.semantic;

import compiler.parser.Expressions;

public class InvalidArithmeticOperationException extends RuntimeException {
    public InvalidArithmeticOperationException(Expressions symbol) {
        super("Couldn't match arithmetic symbol %s, it's not available"
                .formatted(symbol));
    }
}
