package language.types;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import compiler.semantic.ImageNotSquaredException;
import program.Program;
import compiler.semantic.InvalidPathException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class Ingredient extends Assignment {
    private final ASTNode pathNode;
    private final Dimension size;

    public Ingredient(@NotNull ASTNode ingNode) {
        super(ingNode.left());

        //if (!ingNode.is(Expressions.INGREDIENT_VAR))
        //    throw new IllegalDefinitionException(ingNode, Expressions.INGREDIENT_VAR);

        this.pathNode = ingNode.left().left();
        this.canvas = obtainImage();

        var resizeFound = ingNode.find(Expressions.RESIZE);

        if (resizeFound.isEmpty()) size = new Dimension(canvas.getWidth(), canvas.getHeight());
        else {
            int radius = Integer.parseInt(resizeFound.get(0).left().getValue().toString());
            size = new Dimension(radius, radius);
        }

        if (size.getWidth() != size.getHeight())
            throw new ImageNotSquaredException(getName(), ingNode.getPosition());
    }

    /**
     * First, checks if the path could be a URL, then it does a connection with the server provided
     * the resource, if the path is not a URL, then, checks if it could be a path.
     * @return A BufferedImage that contains the canvas read if all went good.
     */
    protected BufferedImage obtainImage() {
        try {
            URL url = new URL(pathNode.getValue().toString());
            URLConnection connection = url.openConnection();
            connection.connect();

            try (InputStream input = connection.getInputStream()) {
                return ImageIO.read(input);
            }
        } catch (MalformedURLException  e) {
            return readImageInDirectory();
        } catch (IOException e) {
            throw InvalidPathException.notOpen(pathNode);
        }
    }

    /**
     * Checks if the path provided could be a directory.
     * The method checks if the path is a compiler resource reference (this means the resource that
     * the code is trying to access is in the resource of this program - the compiler program -).
     * If it is not a compiler resource, then it could be an absolute or relative path.
     * @return A BufferedImage that contains the canvas read if all went good.
     */
    protected BufferedImage readImageInDirectory() {
        try {
            Program program = (Program) pathNode.root().getValue();
            Path path = Paths.get(pathNode.getValue().toString());

            try (FileInputStream input = new FileInputStream(program.getResource(path))) {
                return ImageIO.read(input);
            }
        } catch (FileNotFoundException | URISyntaxException e) {
            throw InvalidPathException.invalid(pathNode);
        } catch (IOException e) {
            throw InvalidPathException.notOpen(pathNode);
        }
    }
}
