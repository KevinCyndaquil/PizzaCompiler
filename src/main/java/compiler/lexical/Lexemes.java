package compiler.lexical;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * This enum contains all lexemes used and validated in this language.
 */
@AllArgsConstructor
@Getter
public enum Lexemes {
    //RESERVED WORDS
    INCLUDE("include"),
    DEFINE("define"),
    INGREDIENT("ingredient"),
    SPECIALTY("specialty"),
    MAKE("make"),
    PIZZA("pizza"),
    BIG("big"),
    MEDIUM("medium"),
    PERSONAL("personal"),
    ADD("add"),
    OF("of"),
    AND("and"),
    RESIZE("resize"),
    SAVE("save"),
    AS("as"),
    //LEXEMES
    LITERAL("literal"),
    TEXT("text"),
    NUMBER("number"),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    OPEN_BRACE("{"),
    CLOSE_BRACE("}"),
    SEMICOLON(";"),
    SINGLE_QUOTE("'"),
    UNDERSCORE("_");

    public final String value;

    /**
     * Get the appropriate lexeme according to any value given.
     * @param value the value to be converted to a Lexemes object.
     * @return a Lexemes value.
     */
    public static Lexemes get(StringBuilder value) {
        return Arrays.stream(Lexemes.values())
                .reduce(Lexemes.LITERAL, (a, t) -> (t.value.contentEquals(value)) ? t : a);
    }

    /**
     * Does the same that the other, but this compares with a char value.
     * @param charValue the character to be compared.
     * @return a Lexemes value.
     */
    public static Lexemes get(char charValue) {
        return Arrays.stream(Lexemes.values())
                .filter(t -> (t.value.length() == 1))
                .reduce(null, (a, t) -> (t.value.charAt(0) == charValue) ? t : a);
    }
}
