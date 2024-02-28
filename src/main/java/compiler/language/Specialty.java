package compiler.language;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public class Specialty extends Var {

    private final @Unmodifiable Map<Ingredient, Integer> ingredients;

    public Specialty(@NotNull ASTNode node, Map<Ingredient, Integer> ingredients) {
        super(node);
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(name, ingredients);
    }
}
