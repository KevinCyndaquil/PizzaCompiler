package compiler;

import compiler.lexical.LexicalAnalyzer;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalyzer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    @Contract(pure = true)
    public static void main(String @NotNull [] args) {
        if (args.length < 1) throw new IllegalArgumentException(
                "A pizza file path must be included");

        String path = args[0];
        String extension = path.substring(path.lastIndexOf(".") + 1);

        if (!extension.equalsIgnoreCase("pf"))
            throw new IllegalArgumentException(
                    "The file's extension '%s' is not available. Use instead 'pf' extension"
                            .formatted(extension));

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(reader);
            var tokens = lexicalAnalyzer.analyze();

            System.out.println("----TOKENS----");
            tokens.forEach(System.out::println);

            Parser parser = new Parser(tokens);
            var programNode = parser.parse();

            System.out.println("\n\n----PROGRAM ASTNode----");
            System.out.println(programNode);

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(programNode);
            semanticAnalyzer.analyze(); //This is still in development

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Pizza file (.pf) path='%s' could not be found".formatted(path));
        } catch (IOException e) {
            throw new RuntimeException("File %s could not be access".formatted(path));
        }
    }
}