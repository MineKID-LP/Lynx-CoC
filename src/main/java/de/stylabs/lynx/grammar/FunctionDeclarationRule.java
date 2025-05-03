package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

public class FunctionDeclarationRule {
    public static AST createNode(TokenStream tokens) {
        AST functionDeclaration = new AST(ASTType.FUNCTION_DECLARATION);

        // Set Function Name
        tokens.expect(TokenType.IDENTIFIER);
        functionDeclaration.addChild(new AST(ASTType.FUNCTION_NAME, tokens.next().value()));
        tokens.expect(TokenType.LEFT_PARENTHESIS);
        tokens.skip(); // Skip the opening parenthesis

        // Get all the parameters
        AST parameters = new AST(ASTType.PARAMETER_LIST);

        while (tokens.peek().type() != TokenType.RIGHT_PARENTHESIS) {
            if (tokens.get().type() == TokenType.RIGHT_PARENTHESIS) {
                tokens.skip(); // Skip the closing parenthesis
                break;
            }
            if (tokens.get().type() == TokenType.COMMA) {
                tokens.skip(); // Skip the comma
            }

            tokens.expect(TokenType.IDENTIFIER); //Type
            AST parameter = new AST(ASTType.PARAMETER);
            AST type = new AST(ASTType.TYPE, tokens.next().value());
            parameter.addChild(type);

            if(tokens.get().type() == TokenType.LEFT_SQUARE_BRACKET) {
                tokens.skip();
                tokens.expect(TokenType.RIGHT_SQUARE_BRACKET);
                tokens.skip();
                type.addChild(new AST(ASTType.TYPE_SPECIAL, "array"));
            }

            tokens.expect(TokenType.IDENTIFIER); //Type
            parameter.addChild(new AST(ASTType.IDENTIFIER, tokens.next().value()));

            tokens.expect(TokenType.RIGHT_PARENTHESIS, TokenType.COMMA);

            parameters.addChild(parameter);
        }

        functionDeclaration.addChild(parameters);

        // Get Function Return Type
        tokens.expect(TokenType.ARROW, TokenType.LEFT_CURLY_BRACKET); //Accept -> or {
        if(tokens.get().type() == TokenType.ARROW) {
            tokens.skip(); // Skip the arrow
            tokens.expect(TokenType.IDENTIFIER);
            functionDeclaration.addChild(new AST(ASTType.FUNCTION_RETURN_TYPE, tokens.next().value()));
        } else {
            functionDeclaration.addChild(new AST(ASTType.FUNCTION_RETURN_TYPE, "void")); // Imply void return
        }

        // Get Function Body
        TokenStream blockTokens = tokens.getBlock();
        AST block = new AST(ASTType.BLOCK_STATEMENT);
        Parser.generateAST(blockTokens, block);

        functionDeclaration.addChild(block);

        return functionDeclaration;
    }
}
