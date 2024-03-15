package language.types;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public class Specialty extends Assignment {
    public final @Unmodifiable Map<Ingredient, Integer> ingredients;

    public Specialty(@NotNull ASTNode node, Map<Ingredient, Integer> ingredients) {
        super(node);
        this.ingredients = ingredients;
    }
}
