package de.stylabs.lynx.pattern;

import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenAcceptor;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternElement {
    private TokenType type = null;
    private TokenAcceptor acceptor = null;
    private boolean optional = false;
    private TokenType until = null;
    private TokenAcceptor untilAcceptor;
    private boolean untilEnabled = false;
    private boolean untilReached = false;

    public PatternElement(TokenType tokenType) {
        type = tokenType;
    }

    public PatternElement(TokenAcceptor tokenAcceptor) {
        acceptor = tokenAcceptor;
    }

    public PatternElement(TokenType tokenType, boolean optional) {
        type = tokenType;
        this.optional = optional;
    }

    public PatternElement(TokenAcceptor tokenAcceptor, boolean optional) {
        acceptor = tokenAcceptor;
        this.optional = optional;
    }

    public PatternElement() {

    }

    public void anyUntil(TokenType tokenType) {
        until = tokenType;
        untilEnabled = true;
        acceptor = TokenAcceptor.all();
    }

    public void until(TokenType until, TokenType[] accepted) {
        this.until = until;
        List<TokenType> acceptedTypes = new ArrayList<>();
        acceptedTypes.addAll(List.of(accepted));
        acceptedTypes.add(until);
        this.acceptor = TokenAcceptor.of(acceptedTypes.toArray(TokenType[]::new));
        untilEnabled = true;
    }

    public boolean matches(Token token) {
        if (untilReached) return false;
        checkUntil(token);
        if (acceptor != null) {
            return acceptor.accepts(token);
        }
        if (type != null) return token.type() == type;

        throw new RuntimeException("No type or acceptor defined for this pattern element");
    }

    private void checkUntil(Token token) {
        if (!untilEnabled) return;
        if (token.type() == until) {
            untilReached = true;
        } else if (untilAcceptor != null && untilAcceptor.accepts(token)) {
            untilReached = true;
        }
    }

    public boolean allowedToAdvance() {
        if (untilEnabled) {
            return untilReached;
        } else {
            return true;
        }
    }

    public boolean isOptional() {
        return optional;
    }
}
