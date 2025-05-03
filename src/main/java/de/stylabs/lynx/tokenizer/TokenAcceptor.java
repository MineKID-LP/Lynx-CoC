package de.stylabs.lynx.tokenizer;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

public class TokenAcceptor {
    private final List<TokenType> acceptedTypes;

    private TokenAcceptor(List<TokenType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    public static TokenAcceptor of(TokenType... types) {
        return new TokenAcceptor(Arrays.asList(types));
    }

    public static TokenAcceptor all() {
        return new TokenAcceptor(Arrays.asList(TokenType.values()));
    }

    // Accept tokens until the specified type is reached
    public boolean accepts(Token token) {
        return acceptedTypes.contains(token.type());
    }


    public List<TokenType> getAcceptedTypes() {
        return acceptedTypes;
    }

    @Override
    public String toString() {
        return "TokenAcceptor{" +
                "acceptedTypes=" + acceptedTypes +
                '}';
    }
}
