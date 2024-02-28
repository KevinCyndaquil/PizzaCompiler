package compiler.language;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public class Specialty extends Assignment {
    private final @Unmodifiable Map<Ingredient, Integer> ingredients;

    public Specialty(@NotNull ASTNode node, Map<Ingredient, Integer> ingredients) {
        super(node);
        this.ingredients = ingredients;
    }

    public @Unmodifiable Map<Ingredient, Integer> ingredients() {
        return ingredients;
    }
}
