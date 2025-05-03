package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.TokenType;

public class ClassInstantiationRule {
    public static AST createNode(TokenStream tokens) {
        AST classInstantiation = new AST(ASTType.CLASS_INSTANTIATION);

        tokens.expect(TokenType.NEW);
        tokens.skip();

        tokens.expect(TokenType.IDENTIFIER);
        classInstantiation.addChild(new AST(ASTType.CLASS_NAME, tokens.next().value()));

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

        classInstantiation.addChild(parameters);

        return classInstantiation;
    }
}
