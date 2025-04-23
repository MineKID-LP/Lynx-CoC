package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class ClassDeclarationRule implements GrammarRule {
    @Override
    public List<Object> getTokenSequence() {
        return List.of(TokenType.CLASS, TokenType.IDENTIFIER);
    }

    @Override
    public AST createNode(List<Token> tokens) {
        return new AST(ASTType.CLASS_DECLARATION, tokens.get(1).type(), tokens.get(1).value());
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }
}
