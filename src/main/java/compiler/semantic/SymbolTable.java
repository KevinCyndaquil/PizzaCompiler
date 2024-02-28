package compiler.semantic;

import compiler.language.Assignment;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class SymbolTable extends HashSet<Assignment> {

    public @Nullable Assignment get(Object value) {
        return stream()
                .reduce(null, (a, s) -> s.getName().equals(value) ? s : a);
    }

    public boolean isDeclared(Object value) {
        return stream()
                .map(v -> v.getName().equals(value))
                .reduce(false, Boolean::logicalOr);
    }

    public boolean isDeclaredAs(Object value, Class<? extends Assignment> aClass) {
        var assignmentDeclared = stream()
                .reduce(null, (a, v) -> v.getName().equals(value) ? v : a);
        if (assignmentDeclared == null) return false;
        return assignmentDeclared.getClass().equals(aClass);
    }
}
