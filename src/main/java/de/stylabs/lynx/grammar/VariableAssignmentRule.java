package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.parser.TokenStream;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class VariableAssignmentRule {
    public static AST createNode(TokenStream tokens) {
        //  string reversed = test.reverse(input);  New Assginment
        AST variableAssignment = new AST(ASTType.VARIABLE_ASSIGNMENT);

        tokens.expect(TokenType.IDENTIFIER);
        variableAssignment.addChild(new AST(ASTType.IDENTIFIER, tokens.next().value()));
        if(tokens.get().type().equals(TokenType.LEFT_SQUARE_BRACKET)) {
            tokens.skip();

            AST arrayAccess = new AST(ASTType.TYPE_SPECIAL, "arrayAccess");

            List<Token> expressionTokens = tokens.until(TokenType.RIGHT_SQUARE_BRACKET);
            arrayAccess.addChild(Parser.generateExpression(expressionTokens));

            variableAssignment.addChild(arrayAccess);
        }

        tokens.expect(TokenType.ASSIGN);
        tokens.skip(); // Skip the assignment operator

        List<Token> expressionTokens = tokens.until(TokenType.SEMICOLON);
        variableAssignment.addChild(Parser.generateExpression(expressionTokens));

        return variableAssignment;
    }
}
