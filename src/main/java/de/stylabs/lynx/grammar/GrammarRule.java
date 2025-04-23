package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.tokenizer.Token;

import java.util.List;

public interface GrammarRule {
    List<Object> getTokenSequence();

    AST createNode(List<Token> tokens);

    default boolean canHaveChildren() {
        return false;
    }
}
