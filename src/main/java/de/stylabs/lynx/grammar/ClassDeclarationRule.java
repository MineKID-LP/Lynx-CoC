package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.TokenType;

public class ClassDeclarationRule {
    public static AST createNode(TokenStream tokens) {
        AST classDeclaration = new AST(ASTType.CLASS_DECLARATION);
        tokens.expect(TokenType.IDENTIFIER);
        classDeclaration.addChild(new AST(ASTType.CLASS_NAME, tokens.next().value()));
        tokens.expect(TokenType.LEFT_CURLY_BRACKET);

        TokenStream blockTokens = tokens.getBlock();
        AST block = new AST(ASTType.BLOCK_STATEMENT);
        Parser.generateAST(blockTokens, block);

        classDeclaration.addChild(block);

        return classDeclaration;
    }
}
