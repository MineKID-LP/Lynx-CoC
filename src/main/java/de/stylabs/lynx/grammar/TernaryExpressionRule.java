package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class TernaryExpressionRule {
    public static AST createNode(TokenStream tokens) {
        AST ternaryStatement = new AST(ASTType.TERNARY_EXPRESSION);

        List<Token> conditionTokens = tokens.until(TokenType.RIGHT_PARENTHESIS);
        AST condition = new AST(ASTType.CONDITION);
        condition.addChild(Parser.generateExpression(conditionTokens));
        ternaryStatement.addChild(condition);

        tokens.expect(TokenType.QUESTION);
        tokens.skip(); // Skip the question mark

        List<Token> trueExpressionTokens = tokens.until(TokenType.COLON);
        AST trueExpression = new AST(ASTType.TRUE_EXPRESSION);
        trueExpression.addChild(Parser.generateExpression(trueExpressionTokens));
        ternaryStatement.addChild(trueExpression);

        List<Token> falseExpressionTokens = tokens.until(TokenType.SEMICOLON);
        AST falseExpression = new AST(ASTType.FALSE_EXPRESSION);
        falseExpression.addChild(Parser.generateExpression(falseExpressionTokens));
        ternaryStatement.addChild(falseExpression);

        return ternaryStatement;
    }
}
