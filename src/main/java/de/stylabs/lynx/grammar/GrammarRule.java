package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;

import java.util.List;

abstract class GrammarRule {
    public static AST createNode(TokenStream tokens) {
        throw new RuntimeException("Unimplemented createNode method");
    };
}
