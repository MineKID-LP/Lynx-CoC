package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class ArrayInstantiationRule {
    public static AST createNode(TokenStream tokens) {
        AST arrayInstantiation = new AST(ASTType.ARRAY_INSTANTIATION);

        tokens.expect(TokenType.NEW);
        tokens.skip();
        tokens.expect(TokenType.IDENTIFIER);

        Token arrayType = tokens.next();
        arrayInstantiation.addChild(new AST(ASTType.TYPE, arrayType.value()));

        tokens.expect(TokenType.LEFT_SQUARE_BRACKET);
        tokens.skip();

        List<Token> arraySizeTokens = tokens.until(TokenType.RIGHT_SQUARE_BRACKET);
        arrayInstantiation.addChild(Parser.generateExpression(arraySizeTokens));


        return arrayInstantiation;
    }
}
