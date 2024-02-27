package compiler.semantic;

import compiler.parser.ASTNode;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class SymbolTable extends HashSet<Var> {

    public @Nullable Var get(String name) {
        return stream()
                .reduce(null, (a, s) -> s.name.equals(name) ? s : a);
    }

    public ASTNode getNode(String name) {
        Var var = get(name);
        return var == null ? null : var.node;
    }

    public boolean isDeclared(String name) {
        return stream()
                .map(v -> v.name.equals(name))
                .reduce(false, Boolean::logicalOr);
    }
}
