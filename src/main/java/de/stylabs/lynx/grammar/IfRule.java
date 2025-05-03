package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class IfRule {
    public static AST createNode(TokenStream tokens) {
        AST ifStatement = new AST(ASTType.IF_STATEMENT);

        tokens.expect(TokenType.LEFT_PARENTHESIS);
        tokens.skip();
        List<Token> conditionTokens = tokens.until(TokenType.RIGHT_PARENTHESIS);

        AST condition = Parser.generateExpression(conditionTokens);
        ifStatement.addChild(condition);

        tokens.expect(TokenType.LEFT_CURLY_BRACKET);
        TokenStream blockTokens = tokens.getBlock();
        AST block = new AST(ASTType.IF_BLOCK_STATEMENT);
        Parser.generateAST(blockTokens, block);
        ifStatement.addChild(block);

        while (tokens.hasNext()) {
            if(!tokens.next().type().equals(TokenType.ELSE)) {
                tokens.back();
                break;
            }

            tokens.skip(); // Skip the else keyword
            if (tokens.hasNext() && tokens.get().type().equals(TokenType.IF)) {
                // If-Else If
                tokens.skip(); // Skip the if keyword
                AST elseIfStatement = createNode(tokens);
                ifStatement.addChild(elseIfStatement);
            } else {
                // Else Block
                tokens.expect(TokenType.LEFT_CURLY_BRACKET);
                TokenStream elseBlockTokens = tokens.getBlock();
                AST elseBlock = new AST(ASTType.ELSE_BLOCK_STATEMENT);
                Parser.generateAST(elseBlockTokens, elseBlock);
                ifStatement.addChild(elseBlock);
            }
        }

        return ifStatement;
    }
}
