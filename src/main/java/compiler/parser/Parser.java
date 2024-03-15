package compiler.parser;

import compiler.lexical.Lexemes;
import compiler.lexical.Token;
import drawer.Program;
import language.util.CPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Parser {
    private final Program program;
    private final List<Token> tokens;
    private int nextTokenPosition;
    private CPoint currentCodePosition;

    public Parser(Program program, List<Token> tokens) {
        this.program = program;

        this.tokens = tokens;
        this.nextTokenPosition = 0;
        this.currentCodePosition = new CPoint();
    }

    public ASTNode parse() {
        return parseStatement();
    }

    public ASTNode parseStatement() {
        ASTNode programNode = new ASTNode(Expressions.PROGRAM, currentCodePosition);
        programNode.value = program;

        do {
            Token currentToken = nextToken();
            programNode.add(switch (currentToken.type()) {
                case MAKE -> parseMake();
                case DEFINE -> parseDefine();
                case INCLUDE -> parseInclude();
                default -> throw new ExpressionNotInterpretedException(currentToken);
            });

        } while (nextTokenPosition < tokens.size());

        return programNode;
    }

    private @NotNull ASTNode parseInclude() {
        ASTNode includeNode = new ASTNode(Expressions.INCLUDE, currentCodePosition);
        includeNode.value = Lexemes.INCLUDE.value;
        includeNode.add(parseText());

        expected(Lexemes.SEMICOLON);

        return includeNode;
    }

    private @NotNull ASTNode parseDefine() {
        ASTNode defineNode = new ASTNode(Expressions.DEFINE, currentCodePosition);

        Token expectedToken = expected(Lexemes.INGREDIENT, Lexemes.SPECIALTY);

        switch (expectedToken.type()) {
            case INGREDIENT -> defineNode.add(parseIngredient());
            case SPECIALTY -> defineNode.add(parseSpecialty());
        }
        return defineNode;
    }

    private @NotNull ASTNode parseIngredient() {
        ASTNode ingredientNode = new ASTNode(Expressions.INGREDIENT_VAR, currentCodePosition);

        Token identifierToken = expected(Lexemes.LITERAL);
        ASTNode identifierNode = new ASTNode(Expressions.LITERAL, identifierToken, currentCodePosition);
        ingredientNode.add(identifierNode);

        expected(Lexemes.OPEN_PARENTHESIS);
        identifierNode.add(parseText());
        expected(Lexemes.CLOSE_PARENTHESIS);

        expected(Lexemes.SEMICOLON);

        return ingredientNode;
    }

    private @NotNull ASTNode parseSpecialty() {
        ASTNode specialtyNode = new ASTNode(Expressions.SPECIALTY_VAR, currentCodePosition);

        Token identifierToken = expected(Lexemes.LITERAL);
        ASTNode identifierNode = new ASTNode(
                Expressions.LITERAL,
                identifierToken,
                currentCodePosition);
        specialtyNode.add(identifierNode);

        expected(Lexemes.OPEN_BRACE);

        do {
            identifierNode.add(parsePizzaIngredients());
            expected(Lexemes.SEMICOLON);
        } while (!match(Lexemes.CLOSE_BRACE));

        expected(Lexemes.CLOSE_BRACE);

        return specialtyNode;
    }

    private @NotNull ASTNode parseText() {
        expected(Lexemes.SINGLE_QUOTE);
        Token urlToken = expected(Lexemes.TEXT);
        ASTNode urlNode = new ASTNode(Expressions.PATH, urlToken, currentCodePosition);
        expected(Lexemes.SINGLE_QUOTE);

        if (match(Lexemes.SINGLE_QUOTE))
            urlNode.value = urlNode.value + parseText().value.toString();
        return urlNode;
    }

    private @NotNull ASTNode parseMake() {
        ASTNode makeNode = new ASTNode(Expressions.MAKE, currentCodePosition);

        Token sizeToken = expected(Lexemes.BIG, Lexemes.MEDIUM, Lexemes.PERSONAL);
        ASTNode sizeNode = new ASTNode(Expressions.SIZE, sizeToken, currentCodePosition);
        makeNode.add(sizeNode);

        expected(Lexemes.PIZZA);
        sizeNode.add(parsePizza());

        expected(Lexemes.SEMICOLON);

        return makeNode;
    }

    private @NotNull ASTNode parsePizza() {
        ASTNode pizzaNode = new ASTNode(Expressions.PIZZA, currentCodePosition);

        Token addOrOfToken = expected(Lexemes.ADD, Lexemes.OF);
        switch (addOrOfToken.type()) {
            case ADD -> pizzaNode.add(parseAdd());
            case OF -> pizzaNode.add(parseOf());
        }

        if (addOrOfToken.is(Lexemes.OF))
            if (match(Lexemes.ADD)) {
                nextToken();
                pizzaNode.add(parseAdd());
            }

        return pizzaNode;
    }

    private @NotNull ASTNode parseAdd() {
        ASTNode addNode = new ASTNode(Expressions.ADD, currentCodePosition);

        do {
            if (match(Lexemes.AND)) nextTokenPosition++;

            addNode.add(parsePizzaIngredients());
        } while (match(Lexemes.AND));

        return addNode;
    }

    private @NotNull ASTNode parsePizzaIngredients() {
        Token literalToken = expected(Lexemes.LITERAL);
        ASTNode ingredientLiteralNode = new ASTNode(
                Expressions.INGREDIENT_VAR,
                literalToken,
                currentCodePosition);

        expected(Lexemes.OPEN_PARENTHESIS);
        ingredientLiteralNode.add(parsePlusminusOperation());
        expected(Lexemes.CLOSE_PARENTHESIS);

        return ingredientLiteralNode;
    }

    private @NotNull ASTNode parseOf() {
        ASTNode ofNode = new ASTNode(
                Expressions.OF,
                currentCodePosition);

        do {
            if (match(Lexemes.AND)) nextTokenPosition++;
            Token addToken = expected(Lexemes.LITERAL);
            ofNode.add(new ASTNode(
                    Expressions.SPECIALTY_VAR,
                    addToken,
                    currentCodePosition));
        } while (match(Lexemes.AND));

        return ofNode;
    }

    /**
     * This method parse first multiplication and division operations.
     * If there is not '*' or '/' lexeme after the number lexeme, then it returns that number
     * node read, else it creates the multiplication or division node checking if the second
     * lexeme could be a multiplication or division too.
     * @return number node or mul or div node.
     */
    private @NotNull ASTNode parseMuldivOperation() {
        Token numberToken = expected(Lexemes.NUMBER);
        ASTNode numberNode = new ASTNode(
                Expressions.NUMBER,
                numberToken,
                currentCodePosition);

        Token muldivToken = ask(Lexemes.MULTIPLY, Lexemes.DIVIDE);
        if (muldivToken == null) return numberNode;

        ASTNode muldivNode = new ASTNode(
                Expressions.cast(muldivToken.type()),
                currentCodePosition);
        muldivNode.add(numberNode);
        muldivNode.add(parseMuldivOperation());

        return muldivNode;
    }

    /**
     * This method parse sum and minus operations.
     * To parse those operations, the method checks if there are mul or div operations before to
     * start to analyze the tokens (This based on operations' hierarchy).
     * If there is not '+' or '-' lexeme returns number node created by method
     * parseMuldivOperation, else returns a sum or minus operation node that could contain
     * mul or div operation.
     * The operations returned by this method are already ordered.
     * @return a number node or operation node.
     */
    private @NotNull ASTNode parsePlusminusOperation() {
        ASTNode numberNode = parseMuldivOperation();

        Token plusminusToken = ask(Lexemes.PLUS, Lexemes.MINUS);
        if (plusminusToken == null) return numberNode;

        ASTNode plusminusNode = new ASTNode(
                Expressions.cast(plusminusToken.type()),
                currentCodePosition);
        plusminusNode.add(numberNode);
        plusminusNode.add(parsePlusminusOperation());

        return plusminusNode;
    }

    private boolean match(Lexemes... expectedLexeme) {
        if (nextTokenPosition >= tokens.size()) return false;
        return Arrays.stream(expectedLexeme)
                .map(l -> tokens.get(nextTokenPosition).is(l))
                .reduce(false, Boolean::logicalOr);
    }

    private Token currentToken() {
        if (nextTokenPosition == 0) return tokens.get(0);
        return tokens.get(nextTokenPosition - 1);
    }

    private @NotNull Token nextToken() {
        if (nextTokenPosition < tokens.size()) {
            Token currentToken = tokens.get(nextTokenPosition++);
            currentCodePosition = currentToken.position();
            return currentToken;
        }
        else throw new RuntimeException("The tokens ran out unexpectedly");
    }

    /**
     * Validates if one of the requested lexemes next to the current lexeme, if true the method
     * calls and returns nextToken() method, if there is not anyone, the method throws an
     * ExpectedLexemeException with information about error.
     * @param expectedLexeme an array with the lexemes requested.
     * @return the next token with one of the lexemes requested.
     * @throws RuntimeException if the method does not find any of the requested lexemes after the
     * current token.
     */
    private @NotNull Token expected(Lexemes... expectedLexeme) throws RuntimeException {
        if (match(expectedLexeme))
            return nextToken();
        throw new ExpectedLexemeException(currentToken(), expectedLexeme);
    }

    /**
     * Checks if the requested tokens are next to the current token, if true the method calls and
     * returns the nextToken() method, otherwise, return null.
     * @param askedLexemes an array with the lexemes requested.
     * @return the next token with one of the lexemes requested, or null if there is not anyone.
     */
    private @Nullable Token ask(Lexemes... askedLexemes) {
        if (match(askedLexemes))
            return nextToken();
        return null;
    }
}
