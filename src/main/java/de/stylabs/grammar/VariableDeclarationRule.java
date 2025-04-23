package de.stylabs.grammar;

import de.stylabs.parser.AST;
import de.stylabs.parser.ASTType;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenAcceptor;
import de.stylabs.tokenizer.TokenType;

import java.util.List;

public class VariableDeclarationRule implements GrammarRule{
    @Override
    public List<Object> getTokenSequence() {
        return List.of(
                TokenType.IDENTIFIER,
                TokenAcceptor.of(
                        TokenType.LEFT_SQUARE_BRACKET,
                        TokenType.RIGHT_SQUARE_BRACKET
                ).until(TokenType.IDENTIFIER),
                TokenType.ASSIGN,
                //Trust me you do not want to know what this is
                //⋆｡°✩ Keep it magical ⋆｡°✩
                TokenAcceptor.ofVariableAssignment()
        );
    }

    @Override
    public AST createNode(List<Token> tokens) {
        AST ast = new AST(ASTType.VARIABLE_DECLARATION);
        ast.addChild(new AST(ASTType.IDENTIFIER, tokens.get(1).type(), tokens.get(1).value()));
        ast.addChild(new AST(ASTType.VARIABLE_VALUE, tokens.get(3).type(), tokens.get(3).value()));
        return ast;
    }
}
