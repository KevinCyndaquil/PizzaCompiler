package program;

import lombok.Getter;

import java.awt.*;

@Getter
public enum DefaultColors {
    PIZZA_BORDER(new Color(255, 215, 93)),
    PIZZA_FILL(new Color(255, 255, 108)),
    SAUCE(new Color(201, 5, 64)),
    BURNED_CHEESE(new Color(227, 181, 102)),
    CHEESE(new Color(255, 240, 145));

    private final Color color;

    DefaultColors(Color color) {
        this.color = color;
    }
}
