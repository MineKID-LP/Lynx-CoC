package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class ArrayInstantiationPattern extends Pattern {
    public ArrayInstantiationPattern() {
        super();
        // this is only the part after the =
        // int[] a = new int[5];
        // so only the new int[5]

        add(TokenType.NEW);
        add(TokenType.IDENTIFIER);
        add(TokenType.LEFT_SQUARE_BRACKET);
    }

    public static ArrayInstantiationPattern get() {
        return new ArrayInstantiationPattern();
    }
}
