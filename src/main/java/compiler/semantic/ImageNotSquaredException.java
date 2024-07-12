package compiler.semantic;

import language.util.CodePosition;

public class ImageNotSquaredException extends RuntimeException {
    public ImageNotSquaredException(Object ingName, CodePosition position) {
        super("Ingredient %s's image must be a squared. Same width and height at %s"
                .formatted(ingName, position));
    }
}
