import program.DrawManager;
import program.ExParams;
import program.PizzaCodeSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Main {

    @Contract(pure = true)
    public static void main(String @NotNull [] args) {
        try {
            boolean showProcess = false;
            String imgExtension = "png";

            if (args.length < 1) throw new IllegalArgumentException(
                    "A pizza file path must be included");
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    switch (ExParams.get(args[i])) {
                        case show -> showProcess = true;
                        case image_extension -> {
                            if (args.length > i + 1) imgExtension = args[++i];
                            else throw new IllegalArgumentException(
                                    "Image extension must be included after explicit call of img extension");
                        }
                        case undefined -> throw new IllegalArgumentException(
                                "%s is not recognized as a execution param".formatted(args[i]));
                    }
                }
            }

            PizzaCodeSource program = new PizzaCodeSource(new File(args[0]), showProcess);
            DrawManager drawer = new DrawManager(program.compile(), imgExtension);
            drawer.draw();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}