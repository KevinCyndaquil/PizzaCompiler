package program;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * This enum contains all code or libraries designed by us, like the basic menu library.
 * These codes must be in the resources folder with all its resources.
 */
@Getter
public enum Libraries {
    BASIC_MENU(Objects.requireNonNull(Libraries.class.getClassLoader().getResource("basicmenu.pf")));

    private final Program program;

    Libraries(@NotNull URL fileURL) {
        System.out.println(fileURL);

        try {
            File file = new File(fileURL.toURI());
            this.program = new Program(file);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL provided by %s is not available. Cause: %s"
                    .formatted(fileURL, e));
        }
    }
}
