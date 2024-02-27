package compiler.parser;

import compiler.lexical.Lexemes;
import compiler.lexical.Token;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ExpectedLexemeException extends RuntimeException {
    public ExpectedLexemeException(@NonNull Token tokenBefore,
                                   @NotNull Lexemes... expectedLexeme) {
        super("Syntax Error: waited %s expression/s after %s expression at %s"
                .formatted(
                        Arrays.toString(expectedLexeme),
                        tokenBefore.type(),
                        tokenBefore.position()));
    }
}
