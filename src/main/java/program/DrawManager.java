package program;

import compiler.semantic.SemanticAnalyzer;
import language.Instruction;
import language.Make;
import language.types.*;
import language.util.Drawable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;

/**
 * This class manages the draws, and imports them.
 */
public class DrawManager {
    private final LinkedHashSet<Instruction> instructions;
    private final Path sourcePath;
    private final String imgExtension;

    public DrawManager(@NotNull SemanticAnalyzer.Intermediate intermediate, String imgExtension) {
        this.sourcePath = intermediate.program.getPath().getParent();
        this.instructions = intermediate.instructions;
        this.imgExtension = imgExtension;
    }

    /**
     * Checks each instruction and does something according to each instruction.
     */
    public void draw() {
        instructions.forEach(i -> {
            if (i instanceof Make make) {
                checkMake(make);
            }
        });
    }

    /**
     * Calls to method draw in Drawable interface, and does the exporting.
     *
     * @param drawable the drawable object.
     * @param <D>      the type of assigment.
     */
    private <D extends Assignment & Drawable> void draw(@NotNull D drawable) {
        drawable.draw();
        export(drawable.getCanvas(), drawable.getImageName());
    }

    private void checkMake(@NotNull Make make) {
        if (make.getInstruction() instanceof Pizza pizza) {
            draw(pizza);
        }
    }

    protected void export(@NotNull BufferedImage image, String saveAS) {
        Path pathName = Paths.get(saveAS + "." + imgExtension);
        File imgFile = new File(sourcePath.resolve(pathName).toUri());

        try {
            ImageIO.write(image, imgExtension, imgFile);
            System.out.println("Canvas exported as" +
                    ": " + imgFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Could not save the canvas: " + e.getMessage());
        }
    }
}
