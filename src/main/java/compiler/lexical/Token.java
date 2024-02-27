package compiler.lexical;

import compiler.util.CPoint;
import org.jetbrains.annotations.NotNull;

public record Token(@NotNull Lexemes type,
                    @NotNull String value,
                    @NotNull CPoint position) {
    public boolean is(Lexemes lexeme) {
        return type.equals(lexeme);
    }

    @Override
    public String toString() {
        return "token_id:%s;value=%s;%s"
                .formatted(type, value, position);
    }
}
