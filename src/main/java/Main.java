import drawer.Drawer;
import drawer.Program;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Main {

    @Contract(pure = true)
    public static void main(String @NotNull [] args) {
        if (args.length < 1) throw new IllegalArgumentException(
                "A pizza file path must be included");

        Program program = new Program(new File(args[0]));
        Drawer drawer = new Drawer(program.getPath().getParent(), program.compile().instructions);
        drawer.draw();
    }
}