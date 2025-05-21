package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class TernaryExpressionPattern extends Pattern{
    public TernaryExpressionPattern() {
        super();
        //  print("Original: " + input);            FunctionCall
        //  test.reverse(input);                    FunctionCall

        // Only accept identifiers and dots until we reach a parenthesis
        // them im pretty sure it should be a function call
        add(TokenType.LEFT_PARENTHESIS);
        anyUntil(TokenType.QUESTION);
    }

    public static TernaryExpressionPattern get() {
        return new TernaryExpressionPattern();
    }
}
