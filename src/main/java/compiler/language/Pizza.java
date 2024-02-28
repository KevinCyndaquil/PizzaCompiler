package compiler.language;

import compiler.parser.ASTNode;
import compiler.util.CPoint;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class Pizza extends Instruction {
    private final @NotNull Sizes size;
    private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    private final Set<Specialty> specialties = new HashSet<>();

    public enum Sizes {
        BIG,
        MEDIUM,
        PERSONAL;

        public static Sizes cast(String input) {
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
    }

    public Pizza(@NotNull ASTNode sizeNode) {
        super(sizeNode.getPosition());
        this.size = Sizes.cast(sizeNode.getValue().toString());
    }

    public void add(Ingredient ingredient, Integer quantity) {
        ingredients.put(ingredient, quantity);
    }

    public void add(Specialty specialty) {
        specialties.add(specialty);
    }

    @Override
    public String toString() {
        return "%s PIZZA %s%s"
                .formatted(
                        size,
                        specialties.stream()
                                .map(s -> s.name + ";")
                                .reduce("", String::concat),
                        ingredients.entrySet().stream()
                                .map(e -> e.getKey().name + ":" + e.getValue() + ";")
                                .reduce("", String::concat));
    }
}
