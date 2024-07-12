package compiler.lexical;

import language.util.CodePosition;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a token of any source code.
 * @param type a Lexemes value that represents its function.
 * @param value the value contained in the token.
 * @param position its position in the source code.
 */
public record Token(@NotNull Lexemes type,
                    @NotNull String value,
                    @NotNull CodePosition position) {
    public boolean is(Lexemes lexeme) {
        return type.equals(lexeme);
    }

    @Override
    public String toString() {
        return "token(id=%s; value=%s; source=%s)"
                .formatted(type, value, position);
    }
}
