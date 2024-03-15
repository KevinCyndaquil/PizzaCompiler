package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import drawer.Program;
import language.*;
import language.expressions.Make;
import language.types.Assignment;
import language.types.Ingredient;
import language.expressions.Pizza;
import language.types.Specialty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SemanticAnalyzer {

    private final SymbolTable symbolTable = new SymbolTable();
    private final List<Instruction> instructions = new ArrayList<>();

    private final ASTNode programNode;

    public SemanticAnalyzer(@NotNull ASTNode programNode) {
        this.programNode = programNode;
    }

    public Intermediate analyze() {
       analyzeProgram(programNode);

       return new Intermediate(instructions, symbolTable);
    }

    private void analyzeProgram(@NotNull ASTNode node) {
        node.children().forEach(n -> {
            switch (n.getType()) {
                case DEFINE -> analyzeDefine(n);
                case MAKE -> addMake(n);
                case INCLUDE -> analyzeInclude(n);
                default -> analyzeProgram(n);
            }
        });
    }

    private void analyzeInclude(@NotNull ASTNode includeNode) {
        Program sourceProgram = (Program) programNode.getValue();
        Path path = Paths.get(includeNode.left().getValue().toString() + ".pf");

        if (path.getFileName().equals(sourceProgram.getPath().getFileName()))
            throw InvalidPathException.recursive(includeNode.left());

        try {
            Program includeProgram = new Program(sourceProgram.getFile(path));

            var include = includeProgram.compile();
            //add each instruction and symbol
            instructions.addAll(include.instructions);
            symbolTable.addAll(include.symbols);
        } catch (URISyntaxException e) {
            throw InvalidPathException.invalid(includeNode.left());
        }
    }

    private void analyzeDefine(@NotNull ASTNode defineNode) {
        switch (defineNode.left().getType()) {
            case INGREDIENT_VAR -> analyzeIngredientDefinition(defineNode.left());
            case SPECIALTY_VAR -> analyzeSpecialtyDefinition(defineNode.left());
        }
    }

    private void analyzeIngredientDefinition(@NotNull ASTNode ingredientNode) {
        ASTNode literalNode = ingredientNode.left();
        Ingredient ingredient = new Ingredient(ingredientNode);

        if (symbolTable.add(ingredient)) return;

        Assignment declaredIngredient = symbolTable.get(literalNode.getValue());
        throw new DuplicatedVarException(literalNode, declaredIngredient);
    }

    private void analyzeSpecialtyDefinition(@NotNull ASTNode specialtyNode) {
        ASTNode literalNode = specialtyNode.left();

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
                                throw new PizzaDefinitionException(n, Expressions.INGREDIENT_VAR);

                            int quantity = doOperation(n.left());
                            return Map.entry(ingredient, quantity);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    private void addMake(@NotNull ASTNode makeNode) {
        makeNode.children().forEach(n -> {
            if (n.is(Expressions.SIZE)) instructions.add(new Make(makeNode, analyzePizza(n)));
        });
    }

    private @NotNull Pizza analyzePizza(@NotNull ASTNode sizeNode) {
        Pizza pizza = new Pizza(sizeNode);

        sizeNode.left().children().forEach(n -> {
            switch (n.getType()) {
                case OF -> pizza.add(validSpecialties(n));
                case ADD -> pizza.add(validIngredients(n));
            }
        });

        return pizza;
    }

    private List<Specialty> validSpecialties(@NotNull ASTNode ofNode) {
        return ofNode.children().stream()
                .map(n -> {
                    if (!symbolTable.isDeclared(n))
                        throw new UndefinedVarException(n);

                    Assignment assignment = symbolTable.get(n);

                    if (assignment instanceof Specialty specialty)
                        return specialty;

                    throw new PizzaDefinitionException(n, Expressions.INGREDIENT_VAR);
                })
                .toList();
    }

    private Map<Ingredient, Integer> validIngredients(@NotNull ASTNode addNode) {
        return addNode.children().stream()
                .map(n -> {
                    if (!symbolTable.isDeclared(n))
                        throw new UndefinedVarException(n);

                    Assignment assignment = symbolTable.get(n);

                    if (assignment instanceof Ingredient ingredient)
                        return Map.entry(ingredient, doOperation(n.left()));

                    throw new PizzaDefinitionException(n, Expressions.INGREDIENT_VAR);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private int doOperation(@NotNull ASTNode operationNode) {
        return switch (operationNode.getType()) {
            case PLUS -> doOperation(operationNode.left()) + doOperation(operationNode.right());
            case MINUS -> doOperation(operationNode.left()) - doOperation(operationNode.right());
            case MULTIPLY -> doOperation(operationNode.left()) * doOperation(operationNode.right());
            case DIVIDE -> doOperation(operationNode.left()) / doOperation(operationNode.right());
            case NUMBER -> Integer.parseInt(operationNode.getValue().toString());
            default -> throw new RuntimeException("Could not match operation %s"
                    .formatted(operationNode.getValue()));
        };
    }

    @Override
    public String toString() {
        return instructions.toString();
    }

    public static class Intermediate {
        public @Unmodifiable List<Instruction> instructions;
        public SymbolTable symbols;

        public Intermediate(@Unmodifiable List<Instruction> instructions, SymbolTable symbols) {
            this.instructions = instructions;
            this.symbols = symbols;
        }
    }
}
