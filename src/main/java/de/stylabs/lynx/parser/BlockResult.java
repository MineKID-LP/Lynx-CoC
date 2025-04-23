package de.stylabs.lynx.parser;

import de.stylabs.lynx.tokenizer.Token;

import java.util.List;

public record BlockResult(List<Token> tokens, int endIndex) {
}
