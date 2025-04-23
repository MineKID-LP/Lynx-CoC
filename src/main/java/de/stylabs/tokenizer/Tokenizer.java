package de.stylabs.tokenizer;

import de.stylabs.errors.UnexpectedEOF;
import de.stylabs.util.Character;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private File file;
    private String content;

    private int currentLineNumber;
    private int currentColumn;
    private int index;

    private List<Token> tokens;

    public Tokenizer(File file) {
        this.file = file;
        this.tokens = new ArrayList<>();
        try {
            this.content = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currentLineNumber = 1;
        currentColumn = 0;
        index = 0;
    }

    public void tokenize() {
        while (hasNext()) {
            char c = peek();
            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            if (Character.isDigit(c)) {
                StringBuilder number = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn;
                while (hasNext() && Character.isDigit(peek())) {
                    number.append(next());
                }
                tokens.add(new Token(TokenType.NUMBER_LITERAL, number.toString(), startLine, startColumn));
                continue;
            }

            if (Character.isLetter(c)) {
                StringBuilder identifier = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn;
                while (hasNext() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
                    identifier.append(next());
                }
                tokens.add(new Token(TokenType.match(identifier.toString()), identifier.toString(), startLine, startColumn));
                continue;
            }

            if(Character.isBracket(c)) {
                tokens.add(new Token(TokenType.matchBracket(c), String.valueOf(c), currentLineNumber, currentColumn));
                advance();
                continue;
            }

            if (Character.isOperator(c)) {
                StringBuilder operator = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn;
                while (hasNext() && Character.isOperator(peek())) {
                    operator.append(next());
                }
                tokens.add(new Token(TokenType.match(operator.toString()), operator.toString(), startLine, startColumn));
                continue;
            }

            if (Character.isPunctuation(c)) {
                tokens.add(new Token(TokenType.matchPunctuation(c), String.valueOf(c), currentLineNumber, currentColumn));
                advance();
                continue;
            }

            if (Character.isStringDelimiter(c)) {
                StringBuilder stringLiteral = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn;
                char delimiter = next();
                while (hasNext() && peek() != delimiter) {
                    stringLiteral.append(next());
                }
                if (hasNext()) {
                    next(); // consume the closing delimiter
                } else {
                    throw new UnexpectedEOF();
                }
                tokens.add(new Token(TokenType.STRING_LITERAL, stringLiteral.toString(), startLine, startColumn));
                continue;
            }

            throw new RuntimeException("Unexpected character: " + c);
        }
    }

    private boolean hasNext() {
        return index < content.length();
    }

    private void assertNext() {
        if (index >= content.length()) {
            throw new UnexpectedEOF();
        }
    }

    private char peek() {
        assertNext();
        return content.charAt(index);
    }

    private char next() {
        assertNext();
        char c = content.charAt(index);
        advance();
        return c;
    }

    private void advance() {
        assertNext();
        char c = peek();
        if (c == '\n') {
            currentLineNumber++;
            currentColumn = 0;
        } else {
            currentColumn++;
        }
        index++;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
