package compiler.semantic;

import compiler.language.Var;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class SymbolTable extends HashSet<Var> {

    public @Nullable Var get(Object value) {
        return stream()
                .reduce(null, (a, s) -> s.getName().equals(value) ? s : a);
    }

    public boolean isDeclared(Object value) {
        return stream()
                .map(v -> v.getName().equals(value))
                .reduce(false, Boolean::logicalOr);
    }
}
