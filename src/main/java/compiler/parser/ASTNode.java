package compiler.parser;

import compiler.lexical.Token;
import compiler.util.CPoint;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    @Getter protected String value;
    @Getter protected final @NotNull CPoint position;

    @Getter protected final @NotNull Expressions type;
    @Getter protected ASTNode father;
    private final List<ASTNode> children = new ArrayList<>();

    public ASTNode(@NotNull Expressions type,
                   @NotNull CPoint position) {
        this.type = type;
        this.position = position;
    }

    public ASTNode(@NotNull Expressions type,
                   @NotNull Token token,
                   @NotNull CPoint position) {
        this.type = type;
        this.value = token.value();
        this.position = position;
    }

    public @Unmodifiable List<ASTNode> children() {
        return children;
    }

    public @Unmodifiable List<ASTNode> childrenAsSequence() {
        if (children.isEmpty()) return null;

        List<ASTNode> sequence = new ArrayList<>();
        List<ASTNode> currentChildren = children;
        ASTNode current = children.get(0);
        int i = 0;

        while(current != null) {
            sequence.add(current);
            current = currentChildren.get(i++);

        }

        return sequence;
    }

    protected void add(ASTNode node) {
        children.add(node);
        node.father = this;
    }

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
}