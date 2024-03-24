package language.util;

import language.types.Specialty;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

/**
 * Use it when the assignment can have specialities.
 */
public interface Specializable {
    void add(@NotNull Specialty specialty);

    void add(@NotNull LinkedHashSet<Specialty> specialties);
}
