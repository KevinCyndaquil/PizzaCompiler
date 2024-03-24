package language.types;

import program.DefaultColors;
import language.util.Circle;
import language.util.Segment;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cheese extends Pizza.Topping {
    public Cheese(@NotNull Pizza pizza) {
        super(pizza);
    }

    @Override
    public void draw() {
        List<Segment> segments = new ArrayList<>();
        Circle circle = size.getCircle().resize(-55);

        graphics.setColor(DefaultColors.BURNED_CHEESE.getColor());
        fillCircle(circle);

        for (int i = 0; i <= 400; i++) {
            segments.add(new Segment(
                    circle.generateEdgePoint(),
                    circle.generateEdgePoint()));
        }

        graphics.setStroke(new BasicStroke(10.0f));
        graphics.setColor(DefaultColors.CHEESE.getColor());
        segments.forEach(this::drawSegment);
        graphics.setStroke(new BasicStroke(1.0f));
    }
}
