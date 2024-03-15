package language.expressions;

import compiler.parser.ASTNode;
import language.util.CPoint;
import language.util.Circle;
import language.Instruction;
import language.types.Ingredient;
import language.types.Specialty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;


public class Pizza extends Instruction {
    private final @NotNull Sizes size;

    public final Circle internalCircle;
    public final Circle sauceCircle;
    public final Circle cheeseCircle;

    @Getter private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    @Getter private final Set<Specialty> specialties = new HashSet<>();

    @Getter
    public enum Sizes {
        BIG(500),
        MEDIUM(300),
        PERSONAL(150);

        private final Circle circle;

        Sizes(int radius) {
            this.circle = new Circle(radius, new Point(radius, radius));
        }

        public static @NotNull Sizes cast(@NotNull String input) {
            return switch (input.toUpperCase()) {
                case "BIG" -> Sizes.BIG;
                case "MEDIUM" -> Sizes.MEDIUM;
                case "PERSONAL" -> Sizes.PERSONAL;
                default -> throw new IllegalStateException("Unexpected value: " + input);
            };
        }
    }

    public Pizza(@NotNull CPoint declaredAt, @NotNull Sizes size) {
        super(declaredAt);

        this.size = size;
        this.internalCircle = size.getCircle().resize(-30);
        this.sauceCircle = size.getCircle().resize(-50);
        this.cheeseCircle = size.getCircle().resize(-58);
    }

    public Pizza(@NotNull ASTNode sizeNode) {
        this(sizeNode.getPosition(), Sizes.cast(sizeNode.getValue().toString()));
    }

    public void add(Ingredient ingredient, Integer quantity) {
        ingredients.put(ingredient, quantity);
    }

    public void add(Map<Ingredient, Integer> ingredients) {
        this.ingredients.putAll(ingredients);
    }

    public void add(Specialty specialty) {
        specialties.add(specialty);
    }

    public void add(List<Specialty> specialties) {
        this.specialties.addAll(specialties);
    }

    public Circle circle() {
        return size.circle;
    }

    @Override
    public String toString() {
        return "%s PIZZA %s%s"
                .formatted(
                        size,
                        specialties.stream()
                                .map(s -> s.getName() + ";")
                                .reduce("", String::concat),
                        ingredients.entrySet().stream()
                                .map(e -> e.getKey().getName() + ":" + e.getValue() + ";")
                                .reduce("", String::concat));
    }
}
