package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.TokenType;

public class ClassInstantiationPattern extends Pattern {
    public ClassInstantiationPattern() {
        super();

        add(TokenType.NEW);
        add(TokenType.IDENTIFIER);
        add(TokenType.LEFT_PARENTHESIS);
    }

    public static ClassInstantiationPattern get() {
        return new ClassInstantiationPattern();
    }
}
