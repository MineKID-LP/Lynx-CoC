package de.stylabs.tokenizer;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

public class TokenAcceptor {
    private final List<TokenType> acceptedTypes;
    private boolean conditionReached = false;
    private TokenType untilType = null;

    private TokenAcceptor(List<TokenType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    public static TokenAcceptor of(TokenType... types) {
        return new TokenAcceptor(Arrays.asList(types));
    }

    //Look to FunctionDeclarationRule for example usage
    public TokenAcceptor until(TokenType type) {
        this.untilType = type;
        return this;
    }

    // Accept tokens until the specified type is reached
    public boolean accepts(Token token) {
        if (!isNull(untilType)) {
            if (token.type() == untilType) {
                conditionReached = true;
            }
            if (conditionReached) {
                return false;
            }
        }
        return acceptedTypes.contains(token.type());
    }


    public List<TokenType> getAcceptedTypes() {
        return acceptedTypes;
    }
}
