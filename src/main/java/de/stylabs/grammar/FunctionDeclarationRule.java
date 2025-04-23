package de.stylabs.grammar;

import de.stylabs.parser.AST;
import de.stylabs.parser.ASTType;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenAcceptor;
import de.stylabs.tokenizer.TokenType;

import java.util.List;

public class FunctionDeclarationRule implements GrammarRule{
    @Override
    public List<Object> getTokenSequence() {
        return List.of(
                TokenType.FUNCTION,
                TokenType.IDENTIFIER,
                TokenType.LEFT_PARENTHESIS,
                TokenAcceptor.of(TokenType.IDENTIFIER, TokenType.COMMA).until(TokenType.RIGHT_PARENTHESIS), // Accepts all identifiers and commas until we get a closing parenthesis
                TokenType.LEFT_CURLY_BRACKET
        );
    }

    @Override
    public AST createNode(List<Token> tokens) {
        AST ast = new AST(ASTType.FUNCTION_DECLARATION);
        ast.addChild(new AST(ASTType.IDENTIFIER, tokens.get(1).type(), tokens.get(1).value()));

        // Add parameters
        for (int i = 3; i < tokens.size() - 1; i++) {
            if (tokens.get(i).type() == TokenType.IDENTIFIER) {
                ast.addChild(new AST(ASTType.PARAMETER, tokens.get(i).type(), tokens.get(i).value()));
            }
        }

        return ast;
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }
}
