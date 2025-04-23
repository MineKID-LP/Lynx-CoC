package de.stylabs.grammar;

import de.stylabs.parser.AST;
import de.stylabs.parser.ASTType;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenAcceptor;
import de.stylabs.tokenizer.TokenType;

import java.util.List;

public class VariableAssignmentRule implements GrammarRule{
    @Override
    public List<Object> getTokenSequence() {
        return List.of(TokenType.IDENTIFIER, TokenType.ASSIGN, TokenAcceptor.of(TokenType.NUMBER_LITERAL, TokenType.STRING_LITERAL), TokenType.SEMICOLON);
    }

    @Override
    public AST createNode(List<Token> tokens) {
        AST ast = new AST(ASTType.VARIABLE_ASSIGNMENT);
        ast.addChild(new AST(ASTType.IDENTIFIER, tokens.get(0).type(), tokens.get(0).value()));
        ast.addChild(new AST(ASTType.VARIABLE_VALUE, tokens.get(2).type(), tokens.get(2).value()));
        return ast;
    }
}
