package compiler.parser;

import compiler.lexical.Lexemes;
import org.jetbrains.annotations.NotNull;

/**
 * Contains all expressions in this language.
 */
public enum Expressions {
    PROGRAM,
    PARAMETER,
    INCLUDE,
    MAKE,
    SIZE,
    PIZZA,
    DEFINE,
    RESIZE,
    SAVE_AS,
    INGREDIENT_VAR,
    SPECIALTY_VAR,
    NUMBER,
    LITERAL,
    PATH,
    ADD,
    OF,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE;

    public static @NotNull Expressions cast(@NotNull Lexemes lexeme) {
        return switch (lexeme) {
            case PLUS -> PLUS;
            case MINUS -> MINUS;
            case MULTIPLY -> MULTIPLY;
            case DIVIDE -> DIVIDE;
            default -> throw new RuntimeException("Unexpected lexeme to cast " + lexeme);
        };
    }
}
