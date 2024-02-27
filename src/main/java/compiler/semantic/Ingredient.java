package compiler.semantic;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Ingredient extends Var{
    protected URL imageUrl;

    public Ingredient(@NotNull ASTNode node) throws MalformedURLException {
        super(node);

        List<ASTNode> nodes = node.find(Expressions.URL_LITERAL);
        if (nodes.isEmpty())
            throw new RuntimeException("The ASTNode provided to define a ingredient must be indeed an %s expression"
                .formatted(Expressions.INGREDIENT_VAR));
        imageUrl = new URL(nodes.get(0).getValue());
    }
}
