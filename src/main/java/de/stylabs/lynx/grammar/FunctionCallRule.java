package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.TokenType;

public class FunctionCallRule {
    public static AST parsePrimary(TokenStream tokens) {
        // Parse an identifier or a parenthesized expression as the base
        if (tokens.get().type() == TokenType.IDENTIFIER) {
            AST identifier = new AST(ASTType.IDENTIFIER, tokens.next().value());
            return identifier;
        } else if (tokens.get().type() == TokenType.LEFT_PARENTHESIS) {
            tokens.skip();
            AST expr = Parser.generateExpression(tokens.getBlockParenthesis());
            return expr;
        } else {
            throw new RuntimeException("Expected identifier or '('");
        }
    }

    public static AST createNode(TokenStream tokens) {
        AST expr = parsePrimary(tokens);

        while (tokens.hasNext()) {
            if (tokens.get().type() == TokenType.DOT) {
                tokens.skip();
                tokens.expect(TokenType.IDENTIFIER);
                String methodName = tokens.next().value();

                if (tokens.hasNext() && tokens.get().type() == TokenType.LEFT_PARENTHESIS) {
                    // Function call
                    AST callNode = new AST(ASTType.FUNCTION_CALL, methodName);

                    // Add the callee (object or previous call) as the first child
                    callNode.addChild(expr);

                    // Parse arguments
                    AST parameters = new AST(ASTType.PARAMETER_LIST);
                    TokenStream parameterListTokens = tokens.getBlockParenthesis();
                    int parameterCount = 0;
                    while (parameterListTokens.hasNext()) {
                        TokenStream parameterTokens = new TokenStream(parameterListTokens.until(TokenType.COMMA));
                        AST parameter = new AST(ASTType.PARAMETER, String.valueOf(parameterCount));
                        parameter.addChild(Parser.generateExpression(parameterTokens));
                        parameters.addChild(parameter);
                        parameterCount++;
                    }
                    callNode.addChild(parameters);

                    expr = callNode;
                } else {
                    // Property access (e.g., obj.prop)
                    AST propNode = new AST(ASTType.PROPERTY_ACCESS, methodName);
                    propNode.addChild(expr);
                    expr = propNode;
                }
            } else if (tokens.get().type() == TokenType.LEFT_PARENTHESIS) {
                // Direct function call (e.g., foo())
                AST callNode = new AST(ASTType.FUNCTION_CALL, null);
                callNode.addChild(expr);

                AST parameters = new AST(ASTType.PARAMETER_LIST);
                TokenStream parameterListTokens = tokens.getBlockParenthesis();
                int parameterCount = 0;
                while (parameterListTokens.hasNext()) {
                    TokenStream parameterTokens = new TokenStream(parameterListTokens.until(TokenType.COMMA));
                    AST parameter = new AST(ASTType.PARAMETER, String.valueOf(parameterCount));
                    parameter.addChild(Parser.generateExpression(parameterTokens));
                    parameters.addChild(parameter);
                    parameterCount++;
                }
                callNode.addChild(parameters);

                expr = callNode;
            } else {
                break;
            }
        }

        return expr;
    }
}
