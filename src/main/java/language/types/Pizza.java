package language.types;

import compiler.parser.ASTNode;
import language.util.Drawable;
import language.util.Ingredible;
import language.util.Specializable;
import program.DefaultColors;
import language.util.Circle;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Pizza extends Assignment implements Drawable, Ingredible, Specializable {
    private static int index = 0;

    protected final @NotNull Sizes size;

    protected final Sauce sauce;
    protected final Cheese cheese;

    @Getter private final LinkedHashSet<Ingredients> ingredients = new LinkedHashSet<>();
    @Getter private final LinkedHashSet<Specialty> specialties = new LinkedHashSet<>();

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
            return switch (input) {
                case "big" -> Sizes.BIG;
                case "medium" -> Sizes.MEDIUM;
                case "personal" -> Sizes.PERSONAL;
                default -> throw new IllegalStateException("Unexpected value: " + input);
            };
        }
    }

    public static abstract class Topping extends Assignment implements Drawable {
        protected Sizes size;

        /**
         * When the topping is implicit on declaration, otherwise, it's not declared by user.
         * @param pizza the pizza instruction that it is.
         */
        public Topping(@NotNull Pizza pizza) {
            super();
            this.size = pizza.size;
            this.graphics = pizza.getGraphics();
        }
    }

    public Pizza(@NotNull ASTNode sizeNode) {
        super(sizeNode);
        this.size = Sizes.cast(sizeNode.getValue().toString());

        canvas = new BufferedImage(
                size.circle.diameter,
                size.circle.diameter,
                BufferedImage.TYPE_INT_ARGB);
        graphics = (Graphics2D) canvas.getGraphics();

        this.sauce = new Sauce(this);
        this.cheese = new Cheese(this);
    }

    @Override
    public String getImageName() {
        return imageName == null ? "pizza" + index++ : imageName;
    }

    @Override
    public void draw() {
        setComposite(AlphaComposite.Clear);
        fillRect(0, 0, size.circle.diameter, size.circle.diameter);
        setComposite(AlphaComposite.SrcOver);

        //draw border
        setColor(DefaultColors.PIZZA_BORDER.getColor());
        fillCircle(size.circle);
        //draw dough
        setColor(DefaultColors.PIZZA_FILL.getColor());
        fillCircle(size.circle.resize(-30));

        sauce.draw();
        cheese.draw();
        specialties.forEach(Specialty::draw);
        ingredients.forEach(Ingredients::draw);
    }

    @Override
    public void add(@NotNull Ingredient ing, int quantity) {
        ingredients.add(new Ingredients(this, ing, quantity));
    }

    @Override
    public void add(@NotNull Map<Ingredient, Integer> ingredients) {
        ingredients.forEach(this::add);
    }

    @Override
    public void add(@NotNull Specialty specialty) {
        specialty.setPizza(this);
        specialties.add(specialty);
    }

    @Override
    public void add(@NotNull LinkedHashSet<Specialty> specialties) {
        specialties.forEach(this::add);
    }

    @Override
    public String toString() {
        return "%s PIZZA %s%s"
                .formatted(
                        size,
                        specialties.stream()
                                .map(s -> s.getName() + ";")
                                .reduce("", String::concat),
                        ingredients.stream()
                                .map(e -> e.ingredient.getName() + ":" + e.quantity + ";")
                                .reduce("", String::concat));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pizza pizza)) return false;
        if (imageName == null) return false;
        return imageName.equals(pizza.imageName);
    }

    @Override
    public int hashCode() {
        return imageName == null ? 0 : imageName.hashCode();
    }
}
