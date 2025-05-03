package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.TokenType;

public class VariableDeclarationRule implements GrammarRule {
    public static AST createNode(TokenStream tokens) {
        //  string reversed = test.reverse(input);  New Assginment
        AST variableDeclaration = new AST(ASTType.VARIABLE_DECLARATION);

        AST type = new AST(ASTType.TYPE, tokens.next().value());
        if(tokens.peek().type().equals(TokenType.LEFT_SQUARE_BRACKET)) {
            tokens.skip();
            tokens.expect(TokenType.RIGHT_SQUARE_BRACKET);
            tokens.skip();
            type.addChild(new AST(ASTType.TYPE_SPECIAL, "array"));
        }
        variableDeclaration.addChild(type);

        tokens.expect(TokenType.IDENTIFIER);
        variableDeclaration.addChild(new AST(ASTType.IDENTIFIER, tokens.next().value()));

        tokens.expect(TokenType.ASSIGN);
        tokens.skip(); // Skip the assignment operator

        //HOW THE FUCK DO I??
        

        return variableDeclaration;
    }
}
