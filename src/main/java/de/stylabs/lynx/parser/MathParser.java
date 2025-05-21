package de.stylabs.lynx.parser;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * This is basically only for mathematical expressions.
 * Although it isn't limited to numbers.
 */
public class MathParser {

    public static AST parse(TokenStream expressionTokens) {
        List<MathToken> mathTokens = new ArrayList<>();
        List<Token> subTokens = new ArrayList<>();

        while (expressionTokens.hasNext()) {
            Token token = expressionTokens.next();

            if(Operator.isOperator(token.value())) {
                if(!subTokens.isEmpty() && (token.type().equals(TokenType.LEFT_PARENTHESIS) || token.type().equals(TokenType.RIGHT_PARENTHESIS))) {
                    subTokens.add(token);
                    continue;
                }

                if(!subTokens.isEmpty()) {
                    mathTokens.add(new MathToken(Operator.NONE, new TokenStream(new ArrayList<>(){{addAll(subTokens);}})));
                    subTokens.clear();
                }

                Operator operator = Operator.fromToken(token);
                TokenStream subStream = new TokenStream(new ArrayList<>() {{add(token);}});
                mathTokens.add(new MathToken(operator, subStream));
            } else {
                switch(token.type()) {
                    case NUMBER_LITERAL -> mathTokens.add(new MathToken(Operator.NONE, new TokenStream(new ArrayList<>(){{add(token);}})));
                    case STRING_LITERAL -> mathTokens.add(new MathToken(Operator.NONE, new TokenStream(new ArrayList<>(){{add(token);}})));
                    default -> subTokens.add(token);
                }
            }
        }

        mathTokens = infixToPostfix(mathTokens);

        AST ast = new AST(ASTType.EXPRESSION);
        List<AST> stack = new ArrayList<>();

        for (MathToken mathToken : mathTokens) {
            if(mathToken.getOperator().equals(Operator.NONE)) {
                stack.add(Parser.generateExpression(mathToken.getTokens()));
            } else {
                AST operatorNode = new AST(ASTType.OPERATOR, mathToken.getOperator().toString());
                AST leftNode = new AST(ASTType.LEFT_EXPRESSION);
                AST rightNode = new AST(ASTType.RIGHT_EXPRESSION);

                if(!stack.isEmpty()) {
                    rightNode.addChild(stack.removeLast());
                } else {
                    Token token = mathToken.getTokens().get();
                    throw new RuntimeException("Right expression is empty at: " + token.line() + ":" + token.column());
                }

                if(!stack.isEmpty()) {
                    leftNode.addChild(stack.removeLast());
                } else {
                    Token token = mathToken.getTokens().get();
                    throw new RuntimeException("Left expression is empty at: " + token.line() + ":" + token.column());
                }

                operatorNode.addChild(leftNode);
                operatorNode.addChild(rightNode);

                stack.add(operatorNode);
            }
        }

        if(stack.size() != 1) {
            throw new RuntimeException("Stack size is not 1 after parsing: " + stack.size());
        }

        ast.addChild(stack.removeLast());

        return ast;
    }

    private static List<MathToken> infixToPostfix(List<MathToken> mathTokens) {
        List<MathToken> output = new ArrayList<>();
        List<MathToken> stack = new ArrayList<>();

        for (MathToken token : mathTokens) {
            if (token.getOperator() == Operator.NONE) {
                output.add(token);
            } else if (token.getOperator() == Operator.LEFT_PARENTHESIS) {
                stack.add(token);
            } else if (token.getOperator() == Operator.RIGHT_PARENTHESIS) {
                while (!stack.isEmpty() && stack.getLast().getOperator() != Operator.LEFT_PARENTHESIS) {
                    output.add(stack.removeLast());
                }
                stack.removeLast();
            } else {
                while (!stack.isEmpty() && stack.getLast().getOperator().getPrecedence() >= token.getOperator().getPrecedence()) {
                    output.add(stack.removeLast());
                }
                stack.add(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.removeLast());
        }

        return output;
    }
}
