package de.stylabs.lynx.pattern;

import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenAcceptor;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public abstract class Pattern {
    private List<PatternElement> pattern;

    public Pattern() {
        this.pattern = new ArrayList<>();
    }

    void add(TokenType tokenType) {
        pattern.add(new PatternElement(tokenType));
    }

    void add(TokenAcceptor tokenAcceptor) {
        pattern.add(new PatternElement(tokenAcceptor));
    }

    void addOptional(TokenType tokenType) {
        pattern.add(new PatternElement(tokenType, true));
    }

    void addOptional(TokenAcceptor tokenAcceptor) {
        pattern.add(new PatternElement(tokenAcceptor, true));
    }

    void anyUntil(TokenType tokenType) {
        PatternElement patternElement = new PatternElement();
        patternElement.anyUntil(tokenType);
    }

    void until(TokenType until, TokenType ...accepted) {
        PatternElement patternElement = new PatternElement();
        patternElement.until(until, accepted);
    }

    public PatternMatch match(TokenStream tokens) {
        int maxTokens = tokens.size();
        int peekCounter = 0; // Start at 0 (not 1)
        int patternCounter = 0; // Index for the pattern elements
        List<Token> matchedTokens = new ArrayList<>();

        while (peekCounter < maxTokens && patternCounter < pattern.size()) {
            Token token = tokens.peek(peekCounter);
            PatternElement element = pattern.get(patternCounter);

            if (element.matches(token)) {
                // If token matches the current element, add it to matched tokens
                matchedTokens.add(token);
                peekCounter++; // Advance token stream

                // Advance pattern if allowed by the element
                if (element.allowedToAdvance()) {
                    patternCounter++;
                }
            } else if (element.isOptional()) {
                // If element is optional and doesn't match, skip it
                patternCounter++;
            } else {
                // If a required element doesn't match, the pattern fails
                return new PatternMatch(false, matchedTokens);
            }
        }

        // Check if all required elements in the pattern were matched
        boolean isCompleteMatch = (patternCounter >= pattern.size());
        return new PatternMatch(isCompleteMatch, matchedTokens);
    }
}
