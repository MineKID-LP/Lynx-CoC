package de.stylabs.lynx.parser;

public class MathToken {
    private TokenStream tokens;
    private Operator operator;

    public MathToken(Operator operator, TokenStream tokens) {
        this.operator = operator;
        this.tokens = tokens;
    }

    public TokenStream getTokens() {
        return tokens;
    }

    public Operator getOperator() {
        return operator;
    }
}
