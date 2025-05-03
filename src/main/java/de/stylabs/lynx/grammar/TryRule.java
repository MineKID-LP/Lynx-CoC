package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class TryRule {
    public static AST createNode(TokenStream tokens) {
        AST tryCatchStatement = new AST(ASTType.TRY_CATCH_STATEMENT);

        tokens.expect(TokenType.LEFT_CURLY_BRACKET);
        TokenStream tryBlockTokens = tokens.getBlock();


        AST tryBlock = new AST(ASTType.TRY_BLOCK_STATEMENT);
        Parser.generateAST(tryBlockTokens, tryBlock);
        tryCatchStatement.addChild(tryBlock);

        Token next = tokens.get();
        boolean finallyBlock = false;

        if(next.type().equals(TokenType.CATCH)) {
            tokens.skip(); // Skip the catch keyword

            tokens.expect(TokenType.LEFT_PARENTHESIS);
            tokens.skip();
            tokens.expect(TokenType.IDENTIFIER);
            Token catchType = tokens.next();
            tokens.expect(TokenType.IDENTIFIER);
            AST catchParameter = new AST(ASTType.PARAMETER, tokens.next().value());
            catchParameter.addChild(new AST(ASTType.TYPE, catchType.value()));
            tokens.expect(TokenType.RIGHT_PARENTHESIS);
            tokens.skip();

            AST catchBlock = new AST(ASTType.CATCH_BLOCK_STATEMENT);
            catchBlock.addChild(catchParameter);
            tokens.expect(TokenType.LEFT_CURLY_BRACKET);
            TokenStream catchBlockTokens = tokens.getBlock();

            AST blockStatement = Parser.generateAST(catchBlockTokens, new AST(ASTType.BLOCK_STATEMENT));
            catchParameter.addChild(blockStatement);

            tryCatchStatement.addChild(catchBlock);
        } else if(next.type().equals(TokenType.FINALLY)) {
            // Either Try-Finally
            finallyBlock = true;
            tokens.skip(); // Skip the finally keyword
            AST catchBlock = new AST(ASTType.FINALLY_BLOCK_STATEMENT);
            tokens.expect(TokenType.LEFT_CURLY_BRACKET);
            TokenStream catchBlockTokens = tokens.getBlock();
            Parser.generateAST(catchBlockTokens, catchBlock);
            tryCatchStatement.addChild(catchBlock);
        }

        if(!tokens.hasNext()) return tryCatchStatement;
        next = tokens.peek();

        if(next.type().equals(TokenType.FINALLY)) {
            if(finallyBlock) {
                throw new RuntimeException("Can not have 2 finally blocks at %s:%s".formatted(tokens.get().line(), tokens.get().column()));
            }
            // Or Try-Catch or Try-Catch-Finally
            tokens.skip(); // Skip the finally keyword
            AST catchBlock = new AST(ASTType.FINALLY_BLOCK_STATEMENT);
            tokens.expect(TokenType.LEFT_CURLY_BRACKET);
            TokenStream catchBlockTokens = tokens.getBlock();
            Parser.generateAST(catchBlockTokens, catchBlock);
            tryCatchStatement.addChild(catchBlock);
        }

        return tryCatchStatement;
    }
}
