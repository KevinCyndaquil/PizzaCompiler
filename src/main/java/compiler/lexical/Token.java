package compiler.lexical;

import language.util.Position;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a token of any program.
 * @param type a Lexemes value that represents its function.
 * @param value the value contained in the token.
 * @param position its position in the program code.
 */
public record Token(@NotNull Lexemes type,
                    @NotNull String value,
                    @NotNull Position position) {
    public boolean is(Lexemes lexeme) {
        return type.equals(lexeme);
    }

    @Override
    public String toString() {
        return "token_id:%s;value=%s;%s"
                .formatted(type, value, position);
    }
}
