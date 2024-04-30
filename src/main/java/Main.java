import program.DrawManager;
import program.Program;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Main {

    @Contract(pure = true)
    public static void main(String @NotNull [] args) {
        try {
            if (args.length < 1) throw new IllegalArgumentException(
                    "A pizza file path must be included");

            Program program = new Program(new File(args[0]));
            DrawManager drawer = new DrawManager(program.compile());
            drawer.draw();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}