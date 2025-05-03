package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class ArrayIndexPattern extends Pattern {
    public ArrayIndexPattern() {
        super();

        add(TokenType.IDENTIFIER);
        add(TokenType.LEFT_SQUARE_BRACKET);
    }

    public static ArrayIndexPattern get() {
        return new ArrayIndexPattern();
    }
}
