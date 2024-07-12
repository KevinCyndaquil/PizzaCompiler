package program;

import compiler.parser.ExpectedLexemeException;
import compiler.parser.ExpressionNotInterpretedException;
import compiler.semantic.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Matriz de casos de error del lenguaje PizzaL.
 */
class ProgramTest {

    /**
     * Error por usar un caracter que no pertenece al alfabeto.
     */
    @Test
    void charUnrecognized() {
        PizzaCodeSource program = new PizzaCodeSource("""
                include 'basicmenu';
                make big pizza of $MEXICANA;
                """);

        assertThrows(IOException.class, program::compile);
    }

    /**
     * Error por usar un lexeme en donde no se esperaba su uso.
     */
    @Test
    void expectedLexeme() {
        PizzaCodeSource program = new PizzaCodeSource("""
                include make;
                """);

        assertThrows(ExpectedLexemeException.class, program::compile);
    }

    /**
     * Error por no terminar una línea de código con punto y coma.
     */
    @Test
    void expectedSemicolon() {
        PizzaCodeSource program = new PizzaCodeSource("""
                include 'basicmenu'
                """);

        assertThrows(ExpectedLexemeException.class, program::compile);
    }

    /**
     * Error por ingresar una literal que no puede ser reconocida por el analizador sintáctico.
     */
    @Test
    void expressionNotInterpreted() {
        PizzaCodeSource program = new PizzaCodeSource("""
                bake big pizza of MEXICANA;
                """);

        assertThrows(ExpressionNotInterpretedException.class, program::compile);
    }

    /**
     * Error por volver a definir una literal.
     */
    @Test
    void duplicatedDefinition() {
        PizzaCodeSource program = new PizzaCodeSource("""
                define ingredient NAHIDA('https://ih1.redbubble.net/image.4673018384.0305/flat,750x,075,f-pad,750x1000,f8f8f8.jpg') resize 100;
                define ingredient NAHIDA('https://ih1.redbubble.net/image.4673018384.0305/flat,750x,075,f-pad,750x1000,f8f8f8.jpg') resize 200;
                """);

        assertThrows(DuplicatedDefinitionException.class, program::compile);
    }

    /**
     * Error por usar una imagén no cuadrada.
     */
    @Test
    void imageNotSquared() {
        PizzaCodeSource program = new PizzaCodeSource("""
                define ingredient NAHIDA('https://ih1.redbubble.net/image.4673018384.0305/flat,750x,075,f-pad,750x1000,f8f8f8.jpg');
                """);

        assertThrows(ImageNotSquaredException.class, program::compile);
    }

    /**
     * Error por usar una dirección invalida a una imagén.
     */
    @Test
    void invalidPath() {
        PizzaCodeSource program = new PizzaCodeSource("""
                define ingredient NAHIDA('https://genshin.com/nahida.png');
                """);

        assertThrows(InvalidPathException.class, program::compile);
    }

    /**
     * Error por llamar a una literal que no ha sido definida con anterioridad.
     */
    @Test
    void undefinedVar() {
        PizzaCodeSource program = new PizzaCodeSource("""
                make big pizza add PEPPERONI(10);
                """);

        assertThrows(UndefinedVarException.class, program::compile);
    }

    /**
     * Error por llamar a una literal en donde se espera que sea de un tipo distinto.
     */
    @Test
    void pizzaBadIngredient() {
        PizzaCodeSource program = new PizzaCodeSource("""
                include 'basicmenu';
                make big pizza of PEPPERONI;
                """);

        assertThrows(IllegalDefinitionException.class, program::compile);
    }

    /**
     * Error por llamar a una literal en donde se espera que sea de un tipo distinto.
     */
    @Test
    void pizzaBadSpecialty() {
        PizzaCodeSource program = new PizzaCodeSource("""
                include 'basicmenu';
                make big pizza add HAWAIANA(10);
                """);

        assertThrows(IllegalDefinitionException.class, program::compile);
    }

    /**
     * Error por definir un ingrediente con cero toppings.
     */
    @Test
    void pizzaBadQuantityIngredient() {
        PizzaCodeSource program = new PizzaCodeSource("""
                include 'basicmenu';
                make big pizza add PEPPERONI(0);
                """);

        assertThrows(IllegalDefinitionException.class, program::compile);
    }
}