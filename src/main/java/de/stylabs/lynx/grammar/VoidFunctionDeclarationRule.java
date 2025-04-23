package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenAcceptor;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class VoidFunctionDeclarationRule implements GrammarRule{
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
        boolean foundRightParenthesis = false;
        while (!foundRightParenthesis) {
            Token token = tokens.get(3);
            if (token.type() == TokenType.RIGHT_PARENTHESIS) {
                foundRightParenthesis = true;
            } else if (token.type() == TokenType.IDENTIFIER) {
                AST param = new AST(ASTType.PARAMETER, token.type(), token.value());
                param.addChild(new AST(ASTType.IDENTIFIER, tokens.get(4).type(), tokens.get(4).value()));
                ast.addChild(param);
            }
            tokens.remove(3); // Remove the token after processing it
            tokens.remove(4);
        }

        ast.addChild(new AST(ASTType.FUNCTION_RETURN_TYPE, TokenType.IDENTIFIER, "void"));

        return ast;
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }
}
