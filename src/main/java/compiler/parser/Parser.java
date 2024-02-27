package compiler.parser;

import compiler.lexical.Lexemes;
import compiler.lexical.Token;
import compiler.util.CPoint;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@Data
public class Parser {
    private final List<Token> tokens;
    private int nextTokenPosition;
    private CPoint currentCodePosition;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.nextTokenPosition = 0;
        this.currentCodePosition = new CPoint();
    }

    public ASTNode parse() {
        return parseStatement();
    }

    public ASTNode parseStatement() {
        ASTNode programNode = new ASTNode(Expressions.PROGRAM, currentCodePosition);

        do {
            Token currentToken = nextToken();
            programNode.add(switch (currentToken.type()) {
                case MAKE -> parseMake();
                case DEFINE -> parseDefine();
                default -> throw new ExpressionNotInterpretedException(currentToken);
            });

        } while (nextTokenPosition < tokens.size());

        return programNode;
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
        identifierNode.add(parseUrlLiteral());
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
            identifierNode.add(parseIngredientLiteralStatement());
            expected(Lexemes.SEMICOLON);
        } while (!match(Lexemes.CLOSE_BRACE));

        expected(Lexemes.CLOSE_BRACE);

        return specialtyNode;
    }

    private @NotNull ASTNode parseUrlLiteral() {
        expected(Lexemes.SINGLE_QUOTE);
        Token urlToken = expected(Lexemes.TEXT);
        ASTNode urlNode = new ASTNode(Expressions.URL_LITERAL, urlToken, currentCodePosition);
        expected(Lexemes.SINGLE_QUOTE);

        if (match(Lexemes.SINGLE_QUOTE))
            urlNode.value = urlNode.value + parseUrlLiteral().value;
        return urlNode;
    }

    private @NotNull ASTNode parseMake() {
        ASTNode makeNode = new ASTNode(Expressions.MAKE, currentCodePosition);

        Token sizeToken = expected(Lexemes.BIG, Lexemes.MEDIUM, Lexemes.PERSONAL);
        ASTNode sizeNode = new ASTNode(Expressions.SIZE, sizeToken, currentCodePosition);
        makeNode.add(sizeNode);

        expected(Lexemes.PIZZA);
        makeNode.add(parsePizzaStatement());

        expected(Lexemes.SEMICOLON);

        return makeNode;
    }

    private @NotNull ASTNode parsePizzaStatement() {
        ASTNode pizzaNode = new ASTNode(Expressions.PIZZA, currentCodePosition);

        Token addOrOfToken = expected(Lexemes.ADD, Lexemes.OF);
        switch (addOrOfToken.type()) {
            case ADD -> pizzaNode.add(parseAddStatement());
            case OF -> pizzaNode.add(parseOfStatement());
        }

        if (addOrOfToken.is(Lexemes.OF))
            if (match(Lexemes.ADD)) {
                nextToken();
                pizzaNode.add(parseAddStatement());
            }

        return pizzaNode;
    }

    private @NotNull ASTNode parseAddStatement() {
        ASTNode addNode = new ASTNode(Expressions.ADD, currentCodePosition);

        do {
            if (match(Lexemes.AND)) nextTokenPosition++;

            addNode.add(parseIngredientLiteralStatement());
        } while (match(Lexemes.AND));

        return addNode;
    }

    private @NotNull ASTNode parseIngredientLiteralStatement() {
        Token literalToken = expected(Lexemes.LITERAL);
        ASTNode ingredientLiteralNode = new ASTNode(
                Expressions.INGREDIENT_VAR,
                literalToken,
                currentCodePosition);

        expected(Lexemes.OPEN_PARENTHESIS);
        ingredientLiteralNode.add(parseOperation());
        expected(Lexemes.CLOSE_PARENTHESIS);

        return ingredientLiteralNode;
    }

    private @NotNull ASTNode parseOfStatement() {
        ASTNode ofNode = new ASTNode(
                Expressions.OF,
                currentCodePosition);

        do {
            if (match(Lexemes.AND)) nextTokenPosition++;
            Token addToken = expected(Lexemes.LITERAL);
            ofNode.add(new ASTNode(
                    Expressions.LITERAL,
                    addToken,
                    currentCodePosition));
        } while (match(Lexemes.AND));

        return ofNode;
    }

    private @NotNull ASTNode parseOperation() {
        Token numToken = expected(Lexemes.NUMBER);
        ASTNode numNode = new ASTNode(
                Expressions.NUMBER,
                numToken,
                currentCodePosition);

        Token operationToken = ask(
                Lexemes.PLUS,
                Lexemes.MINUS,
                Lexemes.MULTIPLY,
                Lexemes.DIVIDE);

        if (operationToken == null) return numNode;

        ASTNode operationNode = new ASTNode(
                Expressions.cast(operationToken.type()),
                currentCodePosition);
        operationNode.add(numNode);
        operationNode.add(parseOperation());

        return operationNode;
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
        else throw new RuntimeException("Fin inesperado de tokens");
    }

    private @NotNull Token expected(Lexemes... expectedLexeme) throws RuntimeException {
        if (match(expectedLexeme))
            return nextToken();
        throw new ExpectedLexemeException(currentToken(), expectedLexeme);
    }

    private @Nullable Token ask(Lexemes... askedLexemes) {
        if (match(askedLexemes))
            return nextToken();
        return null;
    }
}
