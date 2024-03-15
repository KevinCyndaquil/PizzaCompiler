package compiler.parser;

import compiler.lexical.Lexemes;
import org.jetbrains.annotations.NotNull;

public enum Expressions {
    PROGRAM,
    PARAMETER,
    INCLUDE,
    MAKE,
    SIZE,
    PIZZA,
    DEFINE,
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

    public static Expressions cast(@NotNull Lexemes lexeme) {
        return switch (lexeme) {
            case PLUS -> PLUS;
            case MINUS -> MINUS;
            case MULTIPLY -> MULTIPLY;
            case DIVIDE -> DIVIDE;
            default -> null;
        };
    }
}
