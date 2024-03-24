package language.types;

import program.DefaultColors;
import org.jetbrains.annotations.NotNull;

public class Sauce extends Pizza.Topping {
    public Sauce(@NotNull Pizza pizza) {
        super(pizza);
    }

    @Override
    public void draw() {
        graphics.setColor(DefaultColors.SAUCE.getColor());
        fillCircle(size.getCircle().resize(-50));
    }
}
