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
        return List.of(
                TokenType.IDENTIFIER,
                TokenType.ASSIGN,
                //Trust me you do not want to know what this is
                //⋆｡°✩ Keep it magical ⋆｡°✩
                TokenAcceptor.ofVariableAssignment()
        );
    }

    @Override
    public AST createNode(List<Token> tokens) {
        AST ast = new AST(ASTType.VARIABLE_ASSIGNMENT);
        ast.addChild(new AST(ASTType.IDENTIFIER, tokens.get(0).type(), tokens.get(0).value()));
        ast.addChild(new AST(ASTType.VARIABLE_VALUE, tokens.get(2).type(), tokens.get(2).value()));
        return ast;
    }
}
