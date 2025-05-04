package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class FunctionCallRule {
    public static AST createNode(TokenStream tokens) {
        AST functionCall = new AST(ASTType.FUNCTION_CALL);

        tokens.expect(TokenType.IDENTIFIER);
        AST functionName = new AST(ASTType.FUNCTION_NAME, tokens.next().value());

        while (tokens.hasNext()) {
            if(!tokens.get().type().equals(TokenType.DOT)) break;
            tokens.skip(); // Skip the dot
            tokens.expect(TokenType.IDENTIFIER);
            functionName.addChild(new AST(ASTType.FUNCTION_NAME, tokens.next().value()));
        }

        functionCall.addChild(functionName);

        tokens.expect(TokenType.LEFT_PARENTHESIS);
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

        functionCall.addChild(parameters);

        return functionCall;
    }
}
