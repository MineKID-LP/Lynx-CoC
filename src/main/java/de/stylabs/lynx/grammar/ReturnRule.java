package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class ReturnRule {
    public static AST createNode(TokenStream tokens) {
           AST returnStatement = new AST(ASTType.RETURN_STATEMENT);

           List<Token> expressionTokens = tokens.until(TokenType.SEMICOLON);
           AST returnExpression = Parser.generateExpression(expressionTokens);
           returnStatement.addChild(returnExpression);

           return returnStatement;
    }
}
