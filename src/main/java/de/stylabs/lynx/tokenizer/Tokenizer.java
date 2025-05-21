package de.stylabs.lynx.tokenizer;

import de.stylabs.lynx.errors.UnexpectedEOF;
import de.stylabs.lynx.util.Character;

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

            if (Character.isSlash(c)) {
                if (c != '/' || peek() != '/') return;
                StringBuilder comment = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn + 1;
                while (hasNext() && peek() != '\n') {
                    comment.append(next());
                }
                tokens.add(new Token(TokenType.COMMENT, comment.toString(), startLine, startColumn));
                continue;
            }

            if (Character.isDigit(c)) {
                StringBuilder number = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn + 1;
                int dotCount = 0;
                while (hasNext() && Character.isDigit(peek()) || peek() == '.') {
                    if (peek() == '.') {
                        dotCount++;
                        if (dotCount > 1) {
                            throw new RuntimeException(String.format("Unexpected second decimal point at %s:%s", currentLineNumber, currentColumn + 1));
                        }
                    }
                    number.append(next());
                }
                tokens.add(new Token(TokenType.NUMBER_LITERAL, number.toString(), startLine, startColumn));
                continue;
            }

            if (Character.isLetter(c)) {
                StringBuilder identifier = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn + 1;
                while (hasNext() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
                    identifier.append(next());
                }
                tokens.add(new Token(TokenType.match(identifier.toString()), identifier.toString(), startLine, startColumn));
                continue;
            }

            if(Character.isBracket(c)) {
                tokens.add(new Token(TokenType.matchBracket(c), String.valueOf(c), currentLineNumber, currentColumn + 1));
                advance();
                continue;
            }

            if (Character.isOperator(c)) {
                StringBuilder operator = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn + 1;
                while (hasNext() && Character.isOperator(peek())) {
                    operator.append(next());
                }
                tokens.add(new Token(TokenType.match(operator.toString()), operator.toString(), startLine, startColumn));
                continue;
            }

            if (Character.isPunctuation(c)) {
                tokens.add(new Token(TokenType.matchPunctuation(c), String.valueOf(c), currentLineNumber, currentColumn + 1));
                advance();
                continue;
            }

            if (Character.isStringDelimiter(c)) {
                StringBuilder stringLiteral = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn + 1;
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


            if (Character.isSymbol(c)) {
                StringBuilder operator = new StringBuilder();
                int startLine = currentLineNumber;
                int startColumn = currentColumn + 1;
                while (hasNext() && Character.isSymbol(peek())) {
                    operator.append(next());
                }
                tokens.add(new Token(TokenType.match(operator.toString()), operator.toString(), startLine, startColumn));
                continue;
            }

            throw new RuntimeException(String.format("Unexpected character '%s' at %s:%s", c, currentLineNumber, currentColumn + 1));
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
