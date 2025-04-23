package de.stylabs.lynx.errors;

import de.stylabs.lynx.tokenizer.Token;

public class UnexpectedToken extends RuntimeException{
    private final Token token;

    public UnexpectedToken(Token token) {
        super("");
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "Unexpected '" + token.value() + "' at " + token.line() + ":" + token.column();
    }
}
