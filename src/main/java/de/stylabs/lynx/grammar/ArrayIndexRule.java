package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class ArrayIndexRule {
    public static AST createNode(TokenStream tokens) {
        AST arrayIndex = new AST(ASTType.RETURN_STATEMENT);

        tokens.expect(TokenType.IDENTIFIER);
        arrayIndex.addChild(new AST(ASTType.IDENTIFIER, tokens.next().value()));

        tokens.expect(TokenType.LEFT_SQUARE_BRACKET);
        tokens.skip();

        List<Token> expressionTokens = tokens.until(TokenType.RIGHT_SQUARE_BRACKET);
        arrayIndex.addChild(Parser.generateExpression(expressionTokens));

        return arrayIndex;
    }
}
