package compiler.semantic;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class InvalidPathException extends RuntimeException {

    public InvalidPathException(String message) {
        super(message);
    }

    @Contract("_ -> new")
    public static @NotNull InvalidPathException invalid(@NotNull ASTNode pathNode) {
        return new InvalidPathException(
                "The direction PATH, URL or URI provided '%s' by %s located at %s is incorrect or not valid"
                        .formatted(
                                pathNode.getValue(),
                                pathNode.getFather().getValue(),
                                pathNode.getPosition()));
    }

    @Contract("_ -> new")
    public static @NotNull InvalidPathException notOpen(@NotNull ASTNode pathNode) {
        return new InvalidPathException(
                "The direction PATH, URL or URI provided '%s' by %s located at %s is not accessible"
                        .formatted(
                                pathNode.getValue(),
                                pathNode.getFather().getValue(),
                                pathNode.getPosition()));
    }

    public static @NotNull InvalidPathException recursive(@NotNull ASTNode pathNode) {
        return new InvalidPathException(
                "The direction PATH, URL or URI provided '%s' by %s located at %s is the same that its caller"
                        .formatted(
                                pathNode.getValue(),
                                pathNode.getFather().getValue(),
                                pathNode.getPosition()));
    }
}


