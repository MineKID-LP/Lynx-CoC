package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class VariableDeclarationPattern extends Pattern {
    public VariableDeclarationPattern() {
        super();
        //  string reversed = test.reverse(input);
        //  int a = 24;
        //  int e = 120 * a;

        add(TokenType.IDENTIFIER);
        addOptional(TokenType.LEFT_SQUARE_BRACKET);
        addOptional(TokenType.RIGHT_SQUARE_BRACKET);
        add(TokenType.IDENTIFIER);
        add(TokenType.ASSIGN);
        anyUntil(TokenType.SEMICOLON);
    }
}
