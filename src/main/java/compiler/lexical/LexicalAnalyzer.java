package compiler.lexical;

import compiler.util.CPoint;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Analyze the string's characters; the string may be the code source to be interpreted.
 * The constructor splits the code when there is a '\n' character, otherwise, a line break; then
 * the lexical analyzer checks each character, if the character is not valid, throws a
 * CharUnrecognizedException
 */

public class LexicalAnalyzer {
    private final List<String> code = new ArrayList<>();
    private final CPoint currentPosition = new CPoint();

    private final List<Token> tokens = new ArrayList<>();
    private Token lastToken;

    /**
     * The current character, while the analyzer does its work, starts with '\0'.
     */
    private char currentChar = '\0';

    /**
     * If the analyzer is parsing a text
     */
    private boolean isParsingText = false;

    public LexicalAnalyzer(@NotNull BufferedReader reader) throws IOException {
        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.startsWith("//")) continue;
            code.add(line);
        }
    }

    public List<Token> analyze() {
        code.forEach(input -> {
            currentPosition.x = 0;

            while (currentPosition.x < input.length()) {
                currentChar = input.charAt(currentPosition.x);

                if (Character.isWhitespace(currentChar)) checkWhitespace();
                else if (Character.isDigit(currentChar)) checkDigit(input);
                else if (Character.isLetter(currentChar) ||
                        currentChar == '_') checkLetter(input);
                else checkSpecialChar(input);
            }

            currentPosition.y++;
        });

        return tokens;
    }

    private void add(@NotNull Token token) {
        tokens.add(token);
        lastToken = token;
    }

    private void checkWhitespace() {
        currentPosition.x += String.valueOf(currentChar).length();
    }

    private void checkDigit(String input) {
        if (checkText(input)) return;
        add(lexemeAsNumber(input));
    }

    private void checkLetter(String input) {
        if (checkText(input)) return;
        add(lexemeAsKeywordOrLiteral(input));
    }

    private boolean checkText(String input) {
        if (!isParsingText) return false;

        add(lexemeAsText(input));
        return true;
    }

    private void checkSpecialChar(String input) {
        if (checkText(input)) return;

        add(lexemeAsSpecialChar());
        currentPosition.x += String.valueOf(currentChar).length();
    }

    private @NotNull Token lexemeAsNumber(@NotNull String input) {
        CPoint lexemePosition = new CPoint(currentPosition);
        StringBuilder value = new StringBuilder();

        while (currentPosition.x < input.length() &&
                Character.isDigit(input.charAt(currentPosition.x))) {
            value.append(input.charAt(currentPosition.x++));
        }

        return new Token(
                Lexemes.NUMBER,
                value.toString(),
                lexemePosition);
    }

    private @NotNull Token lexemeAsKeywordOrLiteral(@NotNull String input) {
        CPoint lexemePosition = new CPoint(currentPosition);
        StringBuilder value = new StringBuilder();

        while (currentPosition.x < input.length() &&
                (Character.isLetterOrDigit(currentChar) ||
                        currentChar == '_')) {
            value.append(currentChar);
            currentChar = input.charAt(++currentPosition.x);
        }

        return new Token(Lexemes.get(value), value.toString(), lexemePosition);
    }

    private @NotNull Token lexemeAsText(@NotNull String input) {
        CPoint lexemePosition = new CPoint(currentPosition);
        StringBuilder text = new StringBuilder();

        while (currentPosition.x < input.length() &&
                currentChar != '\'') {
            text.append(input.charAt(currentPosition.x));
            currentChar = input.charAt(++currentPosition.x);
        }

        isParsingText = false;
        return new Token(
                Lexemes.TEXT,
                text.toString(),
                lexemePosition);
    }

    private @NotNull Token lexemeAsSpecialChar() {
        CPoint lexemePosition = new CPoint(currentPosition);
        Lexemes lexeme = Lexemes.get(currentChar);

        if (Objects.isNull(lexeme))
            throw new CharUnrecognizedException(currentChar, lexemePosition);

        if (lexeme == Lexemes.SINGLE_QUOTE && lastToken != null)
            if (!lastToken.is(Lexemes.TEXT))
                isParsingText = true;

        return new Token(
                lexeme,
                String.valueOf(currentChar),
                lexemePosition);
    }
}
