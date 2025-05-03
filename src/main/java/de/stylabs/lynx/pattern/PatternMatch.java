package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.Token;

import java.util.List;

public record PatternMatch(boolean matched, List<Token> tokens) {
}
