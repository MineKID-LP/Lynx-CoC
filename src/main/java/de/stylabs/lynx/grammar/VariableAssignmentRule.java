package de.stylabs.lynx.grammar;

import de.stylabs.lynx.parser.AST;
import de.stylabs.lynx.parser.ASTType;
import de.stylabs.lynx.parser.Parser;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenAcceptor;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

public class VariableAssignmentRule implements GrammarRule{
    @Override
    public List<Object> getTokenSequence() {
        return List.of(
                TokenType.IDENTIFIER,
                TokenType.ASSIGN,
                //Trust me you do not want to know what this is
                //⋆｡°✩ Keep it magical ⋆｡°✩
                TokenAcceptor.ofVariableAssignment()
        );
    }

    @Override
    public AST createNode(List<Token> tokens) {
        AST ast = new AST(ASTType.VARIABLE_ASSIGNMENT);
        ast.addChild(new AST(ASTType.IDENTIFIER, tokens.get(0).type(), tokens.get(0).value()));
        List<Token> subList = tokens.subList(2, tokens.size()); //cut out identifier and assign
        ast.addChild(Parser.generateAST(subList, ast));
        return ast;
    }
}
