package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class VariableAssignmentPattern extends Pattern{
    public VariableAssignmentPattern() {
        super();
        //  reversed = "";                          Re-Assginment
        //  array[1] = 5;                           Array Assignment

        add(TokenType.IDENTIFIER);
        addOptional(TokenType.LEFT_SQUARE_BRACKET);
        addOptional(TokenType.IDENTIFIER);
        addOptional(TokenType.RIGHT_SQUARE_BRACKET);
        add(TokenType.ASSIGN);
        anyUntil(TokenType.SEMICOLON);
    }

    public static VariableAssignmentPattern get() {
        return new VariableAssignmentPattern();
    }
}
