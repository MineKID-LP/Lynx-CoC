package de.stylabs.tokenizer;

public record Token(TokenType type, String value, int line, int column) {}
