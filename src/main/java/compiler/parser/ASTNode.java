package compiler.parser;

import compiler.lexical.Token;
import language.util.CodePosition;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines a root of the ASTNode (Abstract Syntax Tree).
 */
public class ASTNode {
    @Getter
    protected Object value;
    @Getter
    protected final @NotNull CodePosition position;
    @Getter
    protected final @NotNull Expressions type;

    @Getter
    protected ASTNode father;
    private final List<ASTNode> children = new ArrayList<>();

    protected ASTNode(@NotNull Expressions type, @NotNull Object value, @NotNull CodePosition position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public ASTNode(@NotNull Expressions type,
                   @NotNull CodePosition position) {
        this(type, type.name(), position);
    }

    public ASTNode(@NotNull Expressions type,
                   @NotNull Token token,
                   @NotNull CodePosition position) {
        this(type, token.value().toLowerCase(), position);
    }

    /**
     * @return all node's children.
     */
    public @Unmodifiable List<ASTNode> children() {
        return children;
    }

    /**
     * Adds a child to this node, at the same time, the node provides by parameter will become the child
     * of this object, having this node as its father.
     *
     * @param child the node object to be added as a child of this node.
     */
    protected void add(ASTNode child) {
        children.add(child);
        child.father = this;
    }

    /**
     * Checks if this node is at least one of the types given.
     *
     * @param expressions the types to be compared.
     * @return true if there is one coincidence, else false.
     */
    public boolean is(Expressions... expressions) {
        return Arrays.stream(expressions)
                .map(type::equals)
                .reduce(false, Boolean::logicalOr);
    }

    /**
     * Checks if there is a root in this node that has the Expression type given.
     *
     * @param type the expression type to be found.
     * @return a list with all nodes that has the type given, empty if there is not any with that
     * characteristic.
     */
    @Contract("_ -> new")
    public List<ASTNode> find(Expressions type) {
        var filter = children.stream()
                .filter(c -> c.type.equals(type))
                .toList();

        if (!filter.isEmpty()) return filter;
        return children.stream()
                .flatMap(c -> c.find(type).stream())
                .toList();
    }

    /**
     * @return the first child.
     * @throws EmptyASTException if this node does not have roots.
     */
    public @NotNull ASTNode left() throws EmptyASTException {
        if (children.isEmpty())
            throw new EmptyASTException(EmptyASTException.Case.LEFT);
        return children.get(0);
    }

    /**
     * @return the second child.
     * @throws EmptyASTException if this node does not have at least two roots.
     */
    public @NotNull ASTNode right() throws EmptyASTException {
        if (children.size() < 2)
            throw new EmptyASTException(EmptyASTException.Case.RIGHT);
        return children.get(1);
    }

    /**
     * @return the top father in this node.
     */
    public @NotNull ASTNode root() {
        ASTNode current = this;

        while (current.father != null) {
            current = current.father;
        }
        return current;
    }

    private @NotNull String formatNode(int depth) {
        StringBuilder result = new StringBuilder();
        result.append("\t".repeat(depth));
        result.append("- [c:%s; f:%s] %s".formatted(position.y + 1, position.x + 1, type));
        result.append(":%s".formatted(value));

        if (!children.isEmpty()) {
            for (ASTNode child : children) {
                result.append("\n").append(child.formatNode(depth + 1));
            }
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return formatNode(0);
    }

    public static class EmptyASTException extends RuntimeException {
        public EmptyASTException(Case side) {
            super("There is not a %s value in children"
                    .formatted(side == Case.LEFT ? "left(index: 0)" : "right(index: 1)"));
        }

        public enum Case {
            LEFT,
            RIGHT,
        }
    }
}