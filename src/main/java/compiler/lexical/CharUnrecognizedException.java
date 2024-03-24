package compiler.lexical;

import language.util.Position;
import org.jetbrains.annotations.NotNull;

public class CharUnrecognizedException extends RuntimeException {

    public CharUnrecognizedException(char unrecognizedChar,
                                     @NotNull Position position) {
        super("Lexical Error: Char '%s' is not recognized, located at %s"
                .formatted(unrecognizedChar, position));
    }
}
