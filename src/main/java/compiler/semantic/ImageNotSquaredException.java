package compiler.semantic;

import language.util.Position;

public class ImageNotSquaredException extends RuntimeException {
    public ImageNotSquaredException(Object ingName, Position position) {
        super("Ingredient %s's image must be a squared. Same width and height at %s"
                .formatted(ingName, position));
    }
}
