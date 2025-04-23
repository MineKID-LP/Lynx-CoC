package de.stylabs;

import de.stylabs.parser.AST;
import de.stylabs.parser.Parser;
import de.stylabs.tokenizer.Tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LynxCompiler {
    public static void main(String[] args) {
        File file = new File("./outline.lynx");
        Tokenizer tokenizer = new Tokenizer(file);
        tokenizer.tokenize();

        try {
            FileOutputStream tokensOut = new FileOutputStream(new File("./outline.tokens"));
            for (var token : tokenizer.getTokens()) {
                String tokenString = token.type() + " " + token.value() + "\n";
                tokensOut.write(tokenString.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AST ast = Parser.generateAST(tokenizer.getTokens());

    }
}