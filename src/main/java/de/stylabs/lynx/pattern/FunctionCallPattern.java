package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class FunctionCallPattern extends Pattern {
    public FunctionCallPattern() {
        super();
        //  print("Original: " + input);            FunctionCall
        //  test.reverse(input);                    FunctionCall

        // Only accept identifiers and dots until we reach a parenthesis
        // them im pretty sure it should be a function call
        add(TokenType.IDENTIFIER);
        until(TokenType.LEFT_PARENTHESIS, TokenType.IDENTIFIER, TokenType.DOT);
    }

    public static FunctionCallPattern get() {
        return new FunctionCallPattern();
    }
}
