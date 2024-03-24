package language.types;

import language.util.Circle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Ingredients extends Pizza.Topping {
    public final Ingredient ingredient;
    public final int quantity;

    private int rows = 0;
    private final List<Double> distribution;
    private double maxToppings = 0d;

    public Ingredients(@NotNull Pizza pizza, @NotNull Ingredient ingredient, int quantity) {
        super(pizza);
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.distribution = calculateRows();
    }

    public List<Double> calculateRows() {
        List<Double> distribution = new ArrayList<>();
        rows = 0;
        double baseToppings = 6d;
        double last = 0d;

        while (quantity > maxToppings) {
            maxToppings += baseToppings * ++rows;
            distribution.add(maxToppings - last);
            last = maxToppings;
        }

        return distribution.stream()
                .map(d -> d / maxToppings)
                .toList();
    }

    @Override
    public void draw() {
        Circle circle = size.getCircle().resize(-70);
        int divRadius = circle.radius / rows;
        int minRadius = divRadius / 2;

        var quantities = distribution.stream()
                .map(d -> d * quantity)
                .map(d -> d % 1 <= 0.5 ? Math.floor(d) : Math.round(d))
                .map(Double::intValue)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        System.out.println(quantities);

        quantities.forEach(d -> {
            circle.generateRangePoints(minRadius + divRadius * (rows -1), divRadius * rows, d)
                    .forEach(p -> drawIngredient(ingredient, p));
            rows--;
        });
        rows = distribution.size();
    }

    @Override
    public String toString() {
        return ingredient.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ingredients ingredients)) return false;
        return ingredient.equals(ingredients.ingredient);
    }

    @Override
    public int hashCode() {
        return ingredient.hashCode();
    }
}
