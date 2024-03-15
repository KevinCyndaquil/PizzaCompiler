package compiler.lexical;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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

    public static Lexemes get(StringBuilder value) {
        return Arrays.stream(Lexemes.values())
                .reduce(Lexemes.LITERAL, (a, t) -> (t.value.contentEquals(value)) ? t : a);
    }

    public static Lexemes get(char charValue) {
        return Arrays.stream(Lexemes.values())
                .filter(t -> (t.value.length() == 1))
                .reduce(null, (a, t) -> (t.value.charAt(0) == charValue) ? t : a);
    }
}
