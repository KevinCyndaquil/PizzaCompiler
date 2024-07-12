package compiler.parser;

import compiler.lexical.Lexemes;
import compiler.lexical.Token;
import org.jetbrains.annotations.Unmodifiable;
import program.PizzaCodeSource;
import language.util.CodePosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * This is the Syntax Analyzer (Parser).
 * A parser is a software component that analyzes input data according to a defined syntax;
 * breaking it into tokens and building a structured representation such as an Abstract Syntax
 * Tree (AST) that captures the hierarchical relationships between elements in the input.
 */
public class Parser {
    private final List<Token> tokens;
    private int nextTokenPosition;
    private CodePosition currentCodePosition;

    private final ASTNode programNode;

    public Parser(@NotNull PizzaCodeSource program, List<Token> tokens) {
        this.tokens = tokens;
        this.nextTokenPosition = 0;
        this.currentCodePosition = new CodePosition(program.getPath());
        this.programNode = new ASTNode(
                Expressions.PROGRAM,
                program,
                currentCodePosition);
    }

    /**
     * Starts parsing all statements.
     * @return the sourceCodePath node.
     */
    public @Unmodifiable ASTNode parse() throws ExpressionNotInterpretedException {
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

    /**
     * Parse the including of libraries in this sourceCodePath.
     *
     * @return include node.
     */
    private @NotNull ASTNode parseInclude() {
        ASTNode includeNode = new ASTNode(Expressions.INCLUDE, currentCodePosition);
        includeNode.value = Lexemes.INCLUDE.value;
        includeNode.add(parseText());

        expected(Lexemes.SEMICOLON);

        return includeNode;
    }

    /**
     * Starts parsing the definition of assignments in this language.
     *
     * @return define node.
     */
    private @NotNull ASTNode parseDefine() {
        ASTNode defineNode = new ASTNode(Expressions.DEFINE, currentCodePosition);

        Token expectedToken = expected(Lexemes.INGREDIENT, Lexemes.SPECIALTY);

        switch (expectedToken.type()) {
            case INGREDIENT -> defineNode.add(parseIngredient());
            case SPECIALTY -> defineNode.add(parseSpecialty());
        }
        return defineNode;
    }

    /**
     * Parse an ingredient definition.
     *
     * @return ingredient node.
     */
    private @NotNull ASTNode parseIngredient() {
        ASTNode ingredientNode = new ASTNode(Expressions.INGREDIENT_VAR, currentCodePosition);

        Token identifierToken = expected(Lexemes.LITERAL);
        ASTNode identifierNode = new ASTNode(Expressions.LITERAL, identifierToken, currentCodePosition);
        ingredientNode.add(identifierNode);

        expected(Lexemes.OPEN_PARENTHESIS);
        identifierNode.add(parseText());
        expected(Lexemes.CLOSE_PARENTHESIS);

        if (ask(Lexemes.RESIZE) != null) ingredientNode.add(parseResize());

        expected(Lexemes.SEMICOLON);

        return ingredientNode;
    }

    /**
     * Parse the new size of ingredient's image.
     *
     * @return the resize node.
     */
    private @NotNull ASTNode parseResize() {
        Token resizeToken = currentToken();
        ASTNode resizeNode = new ASTNode(Expressions.RESIZE, resizeToken, currentCodePosition);

        Token numberToken = expected(Lexemes.NUMBER);
        ASTNode numberNode = new ASTNode(Expressions.NUMBER, numberToken, currentCodePosition);
        resizeNode.add(numberNode);

        return resizeNode;
    }

    /**
     * Parse the specialty definition.
     *
     * @return specialty node.
     */
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
        } while (!are(Lexemes.CLOSE_BRACE));

        expected(Lexemes.CLOSE_BRACE);

        return specialtyNode;
    }

    /**
     * Parse any text between two single_quotes and if there are other texts concatenated.
     *
     * @return the textNode.
     */
    private @NotNull ASTNode parseText() {
        expected(Lexemes.SINGLE_QUOTE);
        Token urlToken = expected(Lexemes.TEXT);
        ASTNode urlNode = new ASTNode(Expressions.PATH, urlToken, currentCodePosition);
        expected(Lexemes.SINGLE_QUOTE);

        if (are(Lexemes.SINGLE_QUOTE))
            urlNode.value = urlNode.value + parseText().value.toString();
        return urlNode;
    }

    /**
     * Starts parsing a make instruction.
     *
     * @return make node.
     */
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

    /**
     * Starts parsing the pizza's properties.
     *
     * @return pizza node.
     */
    private @NotNull ASTNode parsePizza() {
        ASTNode pizzaNode = new ASTNode(Expressions.PIZZA, currentCodePosition);

        Token addOrOfToken = expected(Lexemes.ADD, Lexemes.OF);
        switch (addOrOfToken.type()) {
            case ADD -> pizzaNode.add(parseAdd());
            case OF -> pizzaNode.add(parseOf());
        }

        if (addOrOfToken.is(Lexemes.OF))
            if (are(Lexemes.ADD)) {
                nextToken();
                pizzaNode.add(parseAdd());
            }

        if (ask(Lexemes.SAVE) != null) pizzaNode.add(parseSave());

        return pizzaNode;
    }

    /**
     * Starts parsing the ingredients of a pizza.
     *
     * @return add node.
     */
    private @NotNull ASTNode parseAdd() {
        ASTNode addNode = new ASTNode(Expressions.ADD, currentCodePosition);

        do {
            if (are(Lexemes.AND)) nextTokenPosition++;

            addNode.add(parsePizzaIngredients());
        } while (are(Lexemes.AND));

        return addNode;
    }

    /**
     * Parse all ingredients added to a pizza.
     *
     * @return ingredient literal node.
     */
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

    /**
     * Parse the specialties of a pizza.
     *
     * @return of node.
     */
    private @NotNull ASTNode parseOf() {
        ASTNode ofNode = new ASTNode(
                Expressions.OF,
                currentCodePosition);

        do {
            if (are(Lexemes.AND)) nextTokenPosition++;
            Token addToken = expected(Lexemes.LITERAL);
            ofNode.add(new ASTNode(
                    Expressions.SPECIALTY_VAR,
                    addToken,
                    currentCodePosition));
        } while (are(Lexemes.AND));

        return ofNode;
    }

    /**
     * This method parse first multiplication and division operations.
     * If there is not '*' or '/' lexeme after the number lexeme, then it returns that number
     * node read, else it creates the multiplication or division node checking if the second
     * lexeme could be a multiplication or division too.
     *
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
     *
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

    /**
     * Parse a save as instruction.
     *
     * @return save node.
     */
    private @NotNull ASTNode parseSave() {
        Token saveToken = currentToken();
        ASTNode saveNode = new ASTNode(Expressions.SAVE_AS, saveToken, currentCodePosition);

        expected(Lexemes.AS);
        saveNode.add(parseText());

        return saveNode;
    }

    /**
     * Check if the next token is of any Lexeme given without going to the next token.
     *
     * @param expectedLexeme the lexemes to be compared.
     * @return true if at least one lexeme is equals to the next token, else false.
     */
    private boolean are(Lexemes... expectedLexeme) {
        if (nextTokenPosition >= tokens.size()) return false;
        return Arrays.stream(expectedLexeme)
                .map(l -> tokens.get(nextTokenPosition).is(l))
                .reduce(false, Boolean::logicalOr);
    }

    /**
     * @return the current token read.
     */
    private Token currentToken() {
        if (nextTokenPosition == 0) return tokens.get(0);
        return tokens.get(nextTokenPosition - 1);
    }

    private @NotNull Token nextToken() {
        if (nextTokenPosition < tokens.size()) {
            Token currentToken = tokens.get(nextTokenPosition++);
            currentCodePosition = currentToken.position();
            return currentToken;
        } else throw new RuntimeException("The tokens ran out unexpectedly");
    }

    /**
     * Validates if one of the requested lexemes next to the current lexeme, if true the method
     * calls and returns nextToken() method, if there is not anyone, the method throws an
     * ExpectedLexemeException with information about error.
     *
     * @param expectedLexeme an array with the lexemes requested.
     * @return the next token with one of the lexemes requested.
     * @throws RuntimeException if the method does not find any of the requested lexemes after the
     *                          current token.
     */
    private @NotNull Token expected(Lexemes... expectedLexeme) throws ExpectedLexemeException {
        if (are(expectedLexeme))
            return nextToken();
        throw new ExpectedLexemeException(currentToken(), expectedLexeme);
    }

    /**
     * Checks if the requested tokens are next to the current token, if true the method calls and
     * returns the nextToken() method, otherwise, return null.
     *
     * @param askedLexemes an array with the lexemes requested.
     * @return the next token with one of the lexemes requested, or null if there is not anyone.
     */
    private @Nullable Token ask(Lexemes... askedLexemes) {
        if (are(askedLexemes))
            return nextToken();
        return null;
    }
}
