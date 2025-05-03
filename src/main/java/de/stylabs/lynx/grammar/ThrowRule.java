package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class ThrowRule {
    public static AST createNode(TokenStream tokens) {
        AST throwStatement = new AST(ASTType.THROW_STATEMENT);

        List<Token> expressionTokens = tokens.until(TokenType.SEMICOLON);
        AST returnExpression = Parser.generateExpression(expressionTokens);
        throwStatement.addChild(returnExpression);

        return throwStatement;
    }
}
