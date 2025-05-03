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
        int peekCounter = 1;
        List<Token> matchedTokens = new ArrayList<>();

        while (peekCounter < maxTokens) {
            Token token = tokens.peek(peekCounter);
            if (peekCounter >= pattern.size()) {
                return new PatternMatch(true, matchedTokens);
            }
            PatternElement element = pattern.get(peekCounter);

            if (element.matches(token)) {
                matchedTokens.add(token);
                if(element.allowedToAdvance()) peekCounter++; //Skip if element allows us to. This is for "until"
            } else if(element.isOptional()) {
                peekCounter++; //Skip if optional and doesnt match
            } else {
                return new PatternMatch(false, matchedTokens);
            }
        }


        return new PatternMatch(false, new ArrayList<>());
    }
}
