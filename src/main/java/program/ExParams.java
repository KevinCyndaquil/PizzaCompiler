package program;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public enum ExParams {
    show(Set.of("-s", "--show-mode")),
    image_extension(Set.of("-i", "--image-extension")),
    undefined(Set.of());

    final Set<String> symbols;

    public static ExParams get(String symbol) {
        for (ExParams p : values()) {
            if (p.symbols.contains(symbol)) return p;
        }
        return undefined;
    }
}