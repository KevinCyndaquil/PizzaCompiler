package compiler.lexical;

import compiler.util.CPoint;
import org.jetbrains.annotations.NotNull;

public class CharUnrecognizedException extends RuntimeException {

    public CharUnrecognizedException(char unrecognizedChar,
                                     @NotNull CPoint position) {
        super("Lexical Error: Char '%s' is not recognized, located at %s"
                .formatted(unrecognizedChar, position));
    }
}
