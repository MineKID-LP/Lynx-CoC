package de.stylabs.grammar;

import de.stylabs.parser.AST;
import de.stylabs.parser.ASTType;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenType;

import java.util.List;

public class FunctionDeclarationRule implements GrammarRule{
    @Override
    public List<Object> getTokenSequence() {
        return List.of(TokenType.FUNCTION, TokenType.IDENTIFIER);
    }

    @Override
    public AST createNode(List<Token> tokens) {
        return new AST(ASTType.FUNCTION_DECLARATION, tokens.get(1).value());
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }
}
