package de.stylabs.lynx.tokenizer;

public record Token(TokenType type, String value, int line, int column) {}
