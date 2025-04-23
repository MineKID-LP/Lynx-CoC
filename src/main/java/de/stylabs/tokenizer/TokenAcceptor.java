package de.stylabs.tokenizer;

import java.util.Arrays;
import java.util.List;

public class TokenAcceptor {
    private final List<TokenType> acceptedTypes;

    private TokenAcceptor(List<TokenType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    public static TokenAcceptor of(TokenType... types) {
        return new TokenAcceptor(Arrays.asList(types));
    }

    public boolean accepts(Token token) {
        return acceptedTypes.contains(token.type());
    }

    public List<TokenType> getAcceptedTypes() {
        return acceptedTypes;
    }
}
