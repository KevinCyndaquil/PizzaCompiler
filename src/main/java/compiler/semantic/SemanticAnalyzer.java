package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import program.Program;
import language.*;
import language.Make;
import language.types.Assignment;
import language.types.Ingredient;
import language.types.Pizza;
import language.types.Specialty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A semantic analyzer is a component of a compiler or interpreter that checks the meaning and
 * correctness of the parsed code according to the rules and semantics of the programming language.
 * It performs various tasks such as type checking, scope resolution, detecting semantic errors,
 * and ensuring compliance with language-specific constraints and specifications.
 * The semantic analyzer ensures that the code makes sense in the context of the language's
 * semantics and generates meaningful instructions or actions for further processing.
 */
public class SemanticAnalyzer {
    private final SymbolTable symbolTable = new SymbolTable();
    private final LinkedHashSet<Instruction> instructions = new LinkedHashSet<>();

    private final ASTNode programNode;

    public SemanticAnalyzer(@NotNull ASTNode programNode) {
        this.programNode = programNode;
    }

    public Intermediate analyze() {
       analyzeProgram(programNode);

       return new Intermediate(programNode, instructions, symbolTable);
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

            Intermediate include = includeProgram.compile();
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

        if (ingredient.getSize().getWidth() == 0 || ingredient.getSize().getHeight() == 0)
            throw new ImageZeroSizeException(literalNode);
        if (symbolTable.add(ingredient)) return;

        Assignment declaredIngredient = symbolTable.get(literalNode.getValue());
        throw new DuplicatedDefinitionException(literalNode, declaredIngredient);
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
                                throw new IllegalDefinitionException(n, Expressions.INGREDIENT_VAR);

                            int quantity = doOperation(n.left());
                            return Map.entry(ingredient, quantity);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> k, LinkedHashMap::new))));
    }

    private void addMake(@NotNull ASTNode makeNode) {
        Make make = new Make(makeNode);
        instructions.add(make);

        makeNode.children().forEach(n -> {
            if (n.is(Expressions.SIZE)) make.setInstruction(analyzePizza(n));
        });
    }

    private @NotNull Pizza analyzePizza(@NotNull ASTNode sizeNode) {
        Pizza pizza = new Pizza(sizeNode);

        sizeNode.left().children().forEach(n -> {
            switch (n.getType()) {
                case OF -> pizza.add(validSpecialties(n));
                case ADD -> pizza.add(validIngredients(n));
                case SAVE_AS -> pizza.setImageName(n.left().getValue().toString());
            }
        });
        return pizza;
    }

    private LinkedHashSet<Specialty> validSpecialties(@NotNull ASTNode ofNode) {
        return ofNode.children().stream()
                .map(n -> {
                    if (!symbolTable.isDeclared(n))
                        throw new UndefinedVarException(n);

                    Assignment assignment = symbolTable.get(n);

                    if (assignment instanceof Specialty specialty)
                        return specialty;

                    throw new IllegalDefinitionException(n, Expressions.INGREDIENT_VAR);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashMap<Ingredient, Integer> validIngredients(@NotNull ASTNode addNode) {
        return addNode.children().stream()
                .map(n -> {
                    if (!symbolTable.isDeclared(n))
                        throw new UndefinedVarException(n);

                    Assignment assignment = symbolTable.get(n);

                    System.out.println(n.getValue());

                    int quantity = doOperation(n.left());

                    if (quantity<= 0)
                        throw new IllegalDefinitionException(
                                assignment,
                                n.getPosition(),
                                "quantity of ingredients must be greater that zero");

                    if (assignment instanceof Ingredient ingredient)
                        return Map.entry(ingredient, quantity);

                    throw new IllegalDefinitionException(n, Expressions.INGREDIENT_VAR);
                })
                .peek(e -> {

                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private int doOperation(@NotNull ASTNode operationNode) {
        return switch (operationNode.getType()) {
            case PLUS -> doOperation(operationNode.left()) + doOperation(operationNode.right());
            case MINUS -> doOperation(operationNode.left()) - doOperation(operationNode.right());
            case MULTIPLY -> doOperation(operationNode.left()) * doOperation(operationNode.right());
            case DIVIDE -> doOperation(operationNode.left()) / doOperation(operationNode.right());
            case NUMBER -> Integer.parseInt(operationNode.getValue().toString());
            default -> throw new InvalidArithmeticOperationException(operationNode.getType());
        };
    }

    @Override
    public String toString() {
        return instructions.toString();
    }

    /**
     * Contains the output of semantic analyzer process.
     */
    public static class Intermediate {
        public Program program;
        public @Unmodifiable LinkedHashSet<Instruction> instructions;
        public SymbolTable symbols;

        public Intermediate(@NotNull ASTNode programNode,
                            @Unmodifiable LinkedHashSet<Instruction> instructions,
                            SymbolTable symbols) {
            this.program = (Program) programNode.getValue();
            this.instructions = instructions;
            this.symbols = symbols;
        }
    }
}
