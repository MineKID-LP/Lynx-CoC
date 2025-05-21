package de.stylabs.lynx.generator;

import de.stylabs.lynx.parser.AST;

public class Generator {
    public static String generateASM(AST ast, Architecture architecture) {
        switch (architecture) {
            case x86_64 -> {
                return generateX86_64(ast);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported architecture: " + architecture);
            }
        }
    }

    private static String generateX86_64(AST ast) {
        StringBuilder sb = new StringBuilder();

        // TODO: Implement the x86_64 code generation logic here
        // And uhh.. Good luck future me

        return sb.toString();
    }
}
