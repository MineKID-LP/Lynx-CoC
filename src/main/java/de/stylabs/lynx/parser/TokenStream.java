package de.stylabs.lynx.parser;

import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenStream {
    private List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public boolean hasNext() {
        return index < tokens.size();
    }

    public Token next() {
        if (!hasNext()) {
            throw new RuntimeException("No more tokens");
        }
        return tokens.get(index++);
    }

    public Token peek() {
        if (!hasNext()) {
            throw new RuntimeException("No more tokens");
        }
        return tokens.get(index + 1);
    }

    public Token peek(int offset) {
        if (!hasNext()) {
            throw new RuntimeException("No more tokens");
        }
        return tokens.get(index + offset);
    }

    public Token get() {
        if (!hasNext()) {
            throw new RuntimeException("No more tokens");
        }
        return tokens.get(index);
    }

    public void skip() {
        index++;
    }

    public void skip(int count) {
        index += count;
    }

    public void reset() {
        index = 0;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void expect(TokenType tokenType) {
        Token token = get();
        if (token.type() != tokenType) {
            throw new RuntimeException(String.format("Expected %s at: %s:%s", TokenType.aliasof(tokenType), token.line(), token.column()));
        }
    }

    public void expect(TokenType ...tokenTypes) {
        Token token = get();
        for (TokenType tokenType : tokenTypes) {
            if (token.type() == tokenType) {
                return;
            }
        }

        throw new RuntimeException(String.format("Expected one of %s at: %s:%s", Arrays.toString(tokenTypes), token.line(), token.column()));
    }

    public TokenStream getBlock() {
        //Extract block tokens from the current index
        expect(TokenType.LEFT_CURLY_BRACKET);
        int openBraces = 0;
        int startIndex = index + 1;

        next(); // Consume the left curly bracket

        List<Token> blockTokens = new ArrayList<>();
        while (hasNext()) {
            Token token = next();
            if (token.type() == TokenType.LEFT_CURLY_BRACKET) {
                openBraces++;
            } else if (token.type() == TokenType.RIGHT_CURLY_BRACKET) {
                openBraces--;
                if (openBraces == 0) {
                    next(); // Consume the closing right curly bracket
                    break;
                }
            }
            blockTokens.add(token);
        }

        return new TokenStream(blockTokens);
    }

    @Override
    public String toString() {
        return "TokenStream{" +
                "tokens=" + tokens +
                ", index=" + index +
                '}';
    }

    public void back() {
        if (index > 0) {
            index--;
        } else {
            throw new RuntimeException("No more tokens to go back");
        }
    }

    public List<Token> until(TokenType tokenType) {
        List<Token> result = new ArrayList<>();
        while (hasNext()) {
            Token token = next();
            if (token.type() == tokenType) {
                break;
            }
            result.add(token);
        }
        return result;
    }

    public void expectValue(String value) {
        Token token = get();
        if (!token.value().equals(value)) {
            throw new RuntimeException(String.format("Expected %s at: %s:%s", value, token.line(), token.column()));
        }
    }

    public void expectValue(String ...values) {
        Token token = get();
        for (String value : values) {
            if (token.value().equals(value)) {
                return;
            }
        }
        throw new RuntimeException(String.format("Expected one of %s at: %s:%s", Arrays.toString(values), token.line(), token.column()));
    }

    public List<Token> untilEnd() {
        List<Token> result = new ArrayList<>();
        while (hasNext()) {
            Token token = next();
            result.add(token);
        }
        return result;
    }
}
