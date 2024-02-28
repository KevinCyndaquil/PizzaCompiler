package compiler.language;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Getter
public class Ingredient extends Var {
    protected URL imageUrl;

    public Ingredient(@NotNull ASTNode node) throws MalformedURLException {
        super(node);

        List<ASTNode> nodes = node.find(Expressions.URL_LITERAL);
        if (nodes.isEmpty())
            throw new RuntimeException("The ASTNode provided to define a ingredient must be indeed an %s expression"
                .formatted(Expressions.INGREDIENT_VAR));
        imageUrl = new URL(nodes.get(0).getValue().toString());
    }

    @Override
    public String toString() {
        return name;
    }
}
