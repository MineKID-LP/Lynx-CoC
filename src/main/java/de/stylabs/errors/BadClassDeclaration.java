package de.stylabs.errors;

import de.stylabs.tokenizer.Token;

public class BadClassDeclaration extends Throwable {
    private Token token;
    public BadClassDeclaration(Token token) {
        super("");
        this.token = token;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Expected identifier after 'class' at line ")
                .append(token.line())
                .append(", column ")
                .append(token.column())
                .append(": '")
                .append(token.value())
                .append("'");
        return message.toString();
    }
}
