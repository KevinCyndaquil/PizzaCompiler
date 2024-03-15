package language.types;

import compiler.parser.ASTNode;
import compiler.parser.Expressions;
import drawer.Program;
import compiler.semantic.InvalidPathException;
import compiler.semantic.PizzaDefinitionException;
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
    protected Image image;

    private final ASTNode pathNode;

    public Ingredient(@NotNull ASTNode ingredientNode) {
        super(ingredientNode.left());

        if (!ingredientNode.is(Expressions.INGREDIENT_VAR))
            throw new PizzaDefinitionException(ingredientNode, Expressions.INGREDIENT_VAR);

        this.pathNode = ingredientNode.left().left();
        this.image = validImage();
    }

    /**
     * First, checks if the path could be a URL, then it does a connection with the server provided
     * the resource, if the path is not a URL, then, checks if it could be a path.
     * @return A BufferedImage that contains the image read if all went good.
     */
    protected BufferedImage validImage() {
        try {
            URL url = new URL(pathNode.getValue().toString());
            URLConnection connection = url.openConnection();
            connection.connect();

            try (InputStream input = connection.getInputStream()) {
                return ImageIO.read(input);
            }
        } catch (MalformedURLException  e) {
            return validAsDirectory();
        } catch (IOException e) {
            throw InvalidPathException.notOpen(pathNode);
        }
    }

    /**
     * Checks if the path provided could be a directory.
     * The method checks if the path is a compiler resource reference (this means the resource that
     * the code is trying to access is in the resource of this program - the compiler program -).
     * If it is not a compiler resource, then it could be an absolute or relative path.
     * @return A BufferedImage that contains the image read if all went good.
     */
    protected BufferedImage validAsDirectory() {
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
