package de.stylabs.parser;

import de.stylabs.tokenizer.Token;

import java.util.List;

public record BlockResult(List<Token> tokens, int endIndex) {
}
