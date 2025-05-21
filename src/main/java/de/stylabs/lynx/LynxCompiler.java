package de.stylabs.lynx;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.tokenizer.Tokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LynxCompiler {
    public static void main(String[] args) {
        File file = new File(args[0]);
        Tokenizer tokenizer = new Tokenizer(file);
        tokenizer.tokenize();

        try {
            FileOutputStream tokensOut = new FileOutputStream(new File("./expression.tokens"));
            for (var token : tokenizer.getTokens()) {
                String tokenString = token.type() + " " + token.value() + "\n";
                tokensOut.write(tokenString.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            AST ast = Parser.generateAST(tokenizer.getTokens());

            FileOutputStream astOut = new FileOutputStream(new File("./expression.ast"));
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