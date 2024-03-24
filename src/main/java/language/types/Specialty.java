package language.types;

import compiler.parser.ASTNode;
import language.util.Drawable;
import language.util.Ingredible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashSet;
import java.util.Map;

public class Specialty extends Assignment implements Drawable, Ingredible {
    public final @Unmodifiable Map<Ingredient, Integer> ingredientMap;
    public LinkedHashSet<Ingredients> ingredients = new LinkedHashSet<>();
    private Pizza pizza;

    public Specialty(@NotNull ASTNode node, Map<Ingredient, Integer> ingredients) {
        super(node);
        this.ingredientMap = ingredients;
    }

    /**
     * Must be used to draw the specialty.
     * @param pizza the pizza when specialty will be drew.
     */
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
        add(ingredientMap);
    }

    @Override
    public void add(@NotNull Ingredient ing, int quantity) {
        ingredients.add(new Ingredients(pizza, ing, quantity));
    }

    @Override
    public void add(@NotNull Map<Ingredient, Integer> ingredients) {
        ingredients.forEach(this::add);
    }

    @Override
    public void draw() {
        ingredients.forEach(Ingredients::draw);
    }
}
