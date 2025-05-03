package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;

import java.util.List;

public interface GrammarRule {
    static AST createNode(TokenStream tokens) {
        return null;
    }
}
