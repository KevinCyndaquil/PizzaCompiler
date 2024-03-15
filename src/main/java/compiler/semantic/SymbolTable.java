package compiler.semantic;

import language.types.Assignment;
import compiler.parser.ASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class SymbolTable extends HashSet<Assignment> {

    public Assignment get(Object value) {
        return stream()
                .reduce(null, (a, s) -> s.getName().equals(value) ? s : a);
    }

    protected Assignment get(@NotNull ASTNode node) {
        return get(node.getValue());
    }

    public boolean isDeclared(Object value) {
        return stream()
                .map(v -> v.getName().equals(value))
                .reduce(false, Boolean::logicalOr);
    }

    public boolean isDeclared(@NotNull ASTNode node) {
        return isDeclared(node.getValue());
    }
}
