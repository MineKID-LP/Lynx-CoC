package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class ForLoopRule{
    public static AST createNode(TokenStream tokens) {
        AST forLoop = new AST(ASTType.FOR_STATEMENT);

        tokens.expect(TokenType.LEFT_PARENTHESIS);
        tokens.skip(); // Skip the opening parenthesis

        List<Token> initializationTokens = tokens.until(TokenType.LEFT_CURLY_BRACKET);
        // pop last right parenthesis
        if (initializationTokens.getLast().type() == TokenType.RIGHT_PARENTHESIS) {
            initializationTokens.removeLast();
        } else {
            throwMalformed(tokens.get().line());
        }
        checkSemicolons(initializationTokens);

        TokenStream initStream = new TokenStream(initializationTokens);

        initStream.expect(TokenType.IDENTIFIER);
        initStream.expectValue("int", "float", "double");

        List<Token> variableDeclarationTokens = initStream.until(TokenType.SEMICOLON);
        AST variableDeclaration = VariableDeclarationRule.createNode(new TokenStream(variableDeclarationTokens));
        forLoop.addChild(variableDeclaration);

        initStream.skip(); // Skip the semicolon

        List<Token> conditionTokens = initStream.until(TokenType.SEMICOLON);
        AST condition = Parser.generateExpression(new TokenStream(conditionTokens));
        condition.setType(ASTType.FOR_CONDITION);
        forLoop.addChild(condition);

        initStream.skip(); // Skip the semicolon

        List<Token> incrementTokens = initStream.untilEnd();
        AST increment = Parser.generateExpression(new TokenStream(incrementTokens));
        increment.setType(ASTType.FOR_INCREMENT);
        forLoop.addChild(increment);

        tokens.back(); // Go back to the opening parenthesis
        TokenStream blockTokens = tokens.getBlock();
        AST block = new AST(ASTType.BLOCK_STATEMENT);
        Parser.generateAST(blockTokens, block);
        forLoop.addChild(block);
        
        return forLoop;
    }

    private static void checkSemicolons(List<Token> initializationTokens) {
        int semicolonCount = 0;
        for (Token token : initializationTokens) {
            if (token.type() == TokenType.SEMICOLON) {
                semicolonCount++;
            }
        }

        if (semicolonCount != 2) {
            throwMalformed(initializationTokens.getFirst().line());
        }
    }

    private static void throwMalformed(int line) {
        throw new RuntimeException("Malformed for loop at: " + line);
    }
}
