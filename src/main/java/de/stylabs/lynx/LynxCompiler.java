package de.stylabs.lynx;

import de.stylabs.lynx.generator.Architecture;
import de.stylabs.lynx.generator.Generator;
import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.tokenizer.Tokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LynxCompiler {
    public static void main(String[] args) {
        File file = new File(String.join(" ", args));
        Tokenizer tokenizer = new Tokenizer(file);
        tokenizer.tokenize();
        AST ast = Parser.generateAST(tokenizer.getTokens());

        String asm = Generator.generateASM(ast, Architecture.x86_64);

        try {
            FileOutputStream astOut = new FileOutputStream(new File("./out.ast"));
            astOut.write(ast.toJSON().getBytes());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    public static void print(String ...messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}