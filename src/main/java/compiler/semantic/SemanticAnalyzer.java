package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;

public class SemanticAnalyzer {
    private final SymbolTable symbolTable = new SymbolTable();
    private final ASTNode programNode;

    public SemanticAnalyzer(@NotNull ASTNode programNode) {
        this.programNode = programNode;
    }

    public void analyze() {
       analyzeExpression(programNode);
    }

    private void analyzeExpression(@NotNull ASTNode exNode) {
        exNode.children().forEach(n -> {
            switch (n.getType()) {
                case LITERAL -> defineIngredient(n);
            }

            analyzeExpression(n);
        });
    }

    private void defineIngredient(@NotNull ASTNode node) {
        if (node.getFather() == null) return;
        if (node.getFather().getType() != Expressions.INGREDIENT_VAR) return;

        try {
            Ingredient ingredient = new Ingredient(node);

            if (symbolTable.add(ingredient)) return;

            throw new DuplicatedLiteralException(
                    node,
                    symbolTable.getNode(node.getValue()));



        } catch (MalformedURLException e) {
            throw new RuntimeException("The URL provided by %s located at %s must have a valid url"
                    .formatted(node.getValue(), node.getPosition()));
        }


    }
}
