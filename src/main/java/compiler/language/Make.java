package compiler.language;

import compiler.util.CPoint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Make extends Expression{
    protected @NotNull Sizes size;
    protected @NotNull List<Ingredient> ingredients = new ArrayList<>();
    protected @NotNull List<Specialty> specialties = new ArrayList<>();

    public Make(@NotNull CPoint declarationPosition,
                @NotNull Sizes size,
                List<Ingredient> ingredients,
                List<Specialty> specialties) {
        super(declarationPosition);
        this.size = size;
        this.ingredients.addAll(ingredients);
        this.specialties.addAll(specialties);
    }
}
