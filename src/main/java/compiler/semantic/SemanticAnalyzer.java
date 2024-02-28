package compiler.semantic;

import compiler.language.*;
import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SemanticAnalyzer {
    private final SymbolTable symbolTable = new SymbolTable();
    private final List<Instruction> instructions = new ArrayList<>();

    private final ASTNode programNode;

    public SemanticAnalyzer(@NotNull ASTNode programNode) {
        this.programNode = programNode;
    }

    public @Unmodifiable SymbolTable symbols() {
        return symbolTable;
    }

    public List<Instruction> analyze() {
       analyzeExpression(programNode);

       return instructions;
    }

    private void analyzeExpression(@NotNull ASTNode node) {
        node.children().forEach(n -> {
            switch (n.getType()) {
                case LITERAL -> define(n);
                case MAKE -> make(n);
            }

            analyzeExpression(n);
        });
    }

    private void define(@NotNull ASTNode literalNode) {
        if (literalNode.getFather() == null) return;

        switch (literalNode.getFather().getType()) {
            case INGREDIENT_VAR -> defineIngredient(literalNode);
            case SPECIALTY_VAR -> defineSpecialty(literalNode);
        }
    }

    private void defineIngredient(@NotNull ASTNode literalNode) {
        try {
            Ingredient ingredient = new Ingredient(literalNode);
            if (symbolTable.add(ingredient)) return;

            throw new DuplicatedVarException(
                    literalNode,
                    Objects.requireNonNull(symbolTable.get(literalNode.getValue())));
        } catch (MalformedURLException e) {
            throw new RuntimeException("The URL provided by %s located at %s must have a valid url"
                    .formatted(literalNode.getValue(), literalNode.getPosition()));
        }
    }

    private void defineSpecialty(@NotNull ASTNode literalNode) {
        symbolTable.add(new Specialty(
                literalNode,
                literalNode.children().stream()
                        .peek(n -> {
                            if (symbolTable.isDeclared(n.getValue())) return;
                            throw new UndefinedVarException(n);
                        })
                        .map(n -> {
                            Assignment var = symbolTable.get(n.getValue());
                            if (!(var instanceof Ingredient ingredient))
                                throw new TypeInvalidException(var, Expressions.INGREDIENT_VAR);

                            int quantity = operation(n.left());
                            return Map.entry(ingredient, quantity);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    private void make(@NotNull ASTNode makeNode) {
        makeNode.children().forEach(n -> {
            if (n.is(Expressions.SIZE)) instructions.add(new Make(makeNode, makePizza(n)));
        });
    }

    private @NotNull Pizza makePizza(@NotNull ASTNode sizeNode) {
        Pizza pizza = new Pizza(sizeNode);

        sizeNode.left().children().forEach(sizeN -> {
            switch (sizeN.getType()) {
                case OF -> sizeN.children().forEach(n -> {
                    if (!symbolTable.isDeclared(n.getValue()))
                        throw new UndefinedVarException(n);

                    Assignment var = symbolTable.get(n.getValue());
                    if (!(var instanceof Specialty specialty))
                        throw new TypeInvalidException(n, Expressions.SPECIALTY_VAR);
                    pizza.add(specialty);
                });
                case ADD -> sizeN.children().forEach(n -> {
                    if (!symbolTable.isDeclared(n.getValue()))
                        throw new UndefinedVarException(n);

                    Assignment var = symbolTable.get(n.getValue());

                    if (!(var instanceof Ingredient ingredient))
                        throw new TypeInvalidException(n, Expressions.INGREDIENT_VAR);
                    pizza.add(ingredient, operation(n.left()));
                });
            }
        });
        if (pizza.getIngredients().isEmpty() && pizza.getSpecialties().isEmpty())
            throw new RuntimeException("Make pizza expression must have unless one ingredient or specialty");

        return pizza;
    }

    private int operation(@NotNull ASTNode operationNode) {
        return switch (operationNode.getType()) {
            case PLUS -> operation(operationNode.left()) + operation(operationNode.right());
            case MINUS -> operation(operationNode.left()) - operation(operationNode.right());
            case MULTIPLY -> operation(operationNode.left()) * operation(operationNode.right());
            case DIVIDE -> operation(operationNode.left()) / operation(operationNode.right());
            case NUMBER -> Integer.parseInt(operationNode.getValue().toString());
            default -> throw new RuntimeException("Could match operation");
        };
    }
}
