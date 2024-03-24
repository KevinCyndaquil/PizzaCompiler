package language.util;

import language.types.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Use it when the assigment can have ingredients.
 */
public interface Ingredible {
    void add(@NotNull Ingredient ing, int quantity);
    void add(@NotNull Map<Ingredient, Integer> ingredients);
}
