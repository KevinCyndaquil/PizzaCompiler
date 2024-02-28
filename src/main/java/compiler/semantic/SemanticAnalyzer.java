package compiler.semantic;

import compiler.language.Ingredient;
import compiler.language.Specialty;
import compiler.language.Var;
import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SemanticAnalyzer {
    private final SymbolTable symbolTable = new SymbolTable();

    private final ASTNode programNode;

    public SemanticAnalyzer(@NotNull ASTNode programNode) {
        this.programNode = programNode;
    }

    public @Unmodifiable SymbolTable symbols() {
        return symbolTable;
    }

    public void analyze() {
       analyzeExpression(programNode);
    }

    private void analyzeExpression(@NotNull ASTNode exNode) {
        exNode.children().forEach(n -> {
            switch (n.getType()) {
                case LITERAL -> define(n);
                case MAKE -> make(n);
            }

            analyzeExpression(n);
        });
    }

    private void define(@NotNull ASTNode node) {
        if (node.getFather() == null) return;

        switch (node.getFather().getType()) {
            case INGREDIENT_VAR -> defineIngredient(node);
            case SPECIALTY_VAR -> defineSpecialty(node);
        }
    }

    private void defineIngredient(@NotNull ASTNode node) {
        try {
            Ingredient ingredient = new Ingredient(node);
            if (symbolTable.add(ingredient)) return;

            throw new DuplicatedVarException(
                    node,
                    Objects.requireNonNull(symbolTable.get(node.getValue())));
        } catch (MalformedURLException e) {
            throw new RuntimeException("The URL provided by %s located at %s must have a valid url"
                    .formatted(node.getValue(), node.getPosition()));
        }
    }

    private void defineSpecialty(@NotNull ASTNode node) {
        symbolTable.add(new Specialty(
                node,
                node.children().stream()
                        .peek(n -> {
                            if (symbolTable.isDeclared(n.getValue())) return;
                            throw new UndefinedVarException(n);
                        })
                        .map(n -> {
                            Var var = symbolTable.get(n.getValue());
                            if (!(var instanceof Ingredient ingredient))
                                throw new TypeInvalidException(var, Expressions.INGREDIENT_VAR);

                            int quantity = operation(n.left());
                            return Map.entry(ingredient, quantity);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    private void make(@NotNull ASTNode node) {
        node.children();
    }

    private int operation(@NotNull ASTNode node) {
        return switch (node.getType()) {
            case PLUS -> operation(node.left()) + operation(node.right());
            case MINUS -> operation(node.left()) - operation(node.right());
            case MULTIPLY -> operation(node.left()) * operation(node.right());
            case DIVIDE -> (operation(node.left()) / operation(node.right()));
            case NUMBER -> Integer.parseInt(node.getValue().toString());
            default -> throw new RuntimeException("Could match operation");
        };
    }
}
