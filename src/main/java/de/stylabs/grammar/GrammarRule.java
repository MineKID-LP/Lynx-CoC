package de.stylabs.grammar;

import de.stylabs.parser.AST;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenType;

import java.util.List;

public interface GrammarRule {
    List<Object> getTokenSequence();

    AST createNode(List<Token> tokens);

    default boolean canHaveChildren() {
        return false;
    }
}
