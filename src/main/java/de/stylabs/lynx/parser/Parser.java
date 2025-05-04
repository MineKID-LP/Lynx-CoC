package de.stylabs.lynx.parser;

import de.stylabs.lynx.errors.UnexpectedEOF;
import de.stylabs.lynx.grammar.*;
import de.stylabs.lynx.pattern.*;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;


public class Parser {

    public static AST generateAST(TokenStream tokenStream, AST parent) {
        AST root = (parent == null) ? new AST(ASTType.PROGRAM) : parent;

        while(tokenStream.hasNext()) {
            Token token = tokenStream.next();

            AST child = switch (token.type()) {
                case CLASS -> ClassDeclarationRule.createNode(tokenStream);
                case FUNCTION -> FunctionDeclarationRule.createNode(tokenStream);
                case IDENTIFIER -> decideOnWhatTheFuckThisIs(tokenStream);
                case FOR -> ForLoopRule.createNode(tokenStream);
                case RETURN -> ReturnRule.createNode(tokenStream);
                case TRY -> TryRule.createNode(tokenStream);
                case THROW -> ThrowRule.createNode(tokenStream);
                case IF -> IfRule.createNode(tokenStream);
                case LEFT_PARENTHESIS -> testTernary(tokenStream);
                case COMMENT -> null;  //FUCK YOU JAVA AND YOUR SHITTY REGEX IMPLEMENTATION 🖕
                default -> throw new RuntimeException(String.format("Unexpected %s at: %s:%s", token.type(), token.line(), token.column()));
            };

            if(!isNull(child)) root.addChild(child);
        }

        return root;
    }

    // This is maybe not the best way to do this, but I don't really care rn
    // TODO: FIX
    private static AST testTernary(TokenStream tokenStream) {
        int peekCounter = 1;

        while (peekCounter < tokenStream.size()) {
            if(tokenStream.peek(peekCounter).type().equals(TokenType.RIGHT_PARENTHESIS)) {
                if (tokenStream.peek(peekCounter + 1).type().equals(TokenType.QUESTION)) {
                    return TernaryExpressionRule.createNode(tokenStream);
                }
            }
            peekCounter++;
        }

        throw new RuntimeException(String.format("Unexpected %s at: %s:%s", tokenStream.get().type(), tokenStream.get().line(), tokenStream.get().column()));
    }

    private static AST decideOnWhatTheFuckThisIs(TokenStream tokenStream) {
        tokenStream.back(); // We actually need this token, since we don't know what it is yet

        List<Token> tokens = tokenStream.until(TokenType.SEMICOLON);
        tokenStream.back();
        tokens.add(tokenStream.next());
        TokenStream statementStream = new TokenStream(tokens);

        PatternMatch match = VariableDeclarationPattern.get().match(statementStream);
        if(match.matched()) {
            return VariableDeclarationRule.createNode(new TokenStream(tokens));
        }

        match = VariableAssignmentPattern.get().match(statementStream);
        if(match.matched()) {
            return VariableAssignmentRule.createNode(new TokenStream(tokens));
        }

        match = FunctionCallPattern.get().match(statementStream);
        if(match.matched()) {
            return FunctionCallRule.createNode(new TokenStream(tokens));
        }

        throw new RuntimeException(String.format("Unexpected '%s' at: %s:%s", tokenStream.get().value(), tokenStream.get().line(), tokenStream.get().column()));
    }

    public static AST generateAST(List<Token> tokens, AST parent) {
        return generateAST(new TokenStream(tokens), parent);
    }

    public static AST generateAST(List<Token> tokens) {
        return generateAST(new TokenStream(tokens), null);
    }

    public static AST generateExpression(List<Token> expressionTokens) {
        return generateExpression(new TokenStream(expressionTokens));
    }

    public static AST generateExpression(TokenStream expressionTokens) {
        if (expressionTokens.size() == 0) {
            throw new UnexpectedEOF();
        }

        if (expressionTokens.size() == 1) {
            Token token = expressionTokens.get();
            if (token.type() == TokenType.IDENTIFIER) {
                return new AST(ASTType.IDENTIFIER, token.value());
            } else if (token.type() == TokenType.NUMBER_LITERAL) {
                return new AST(ASTType.NUMBER_LITERAL, token.value());
            } else if (token.type() == TokenType.STRING_LITERAL) {
                return new AST(ASTType.STRING_LITERAL, token.value());
            } else if (token.type() == TokenType.TRUE || token.type() == TokenType.FALSE) {
                return new AST(ASTType.BOOLEAN, token.value());
            }
            throw new RuntimeException(String.format("Unexpected '%s' at: %s:%s", token.value(), token.line(), token.column()));
        }

        PatternMatch match = FunctionCallPattern.get().match(expressionTokens);
        if(match.matched()) {
            AST node = FunctionCallRule.createNode(expressionTokens);

            if(!expressionTokens.hasNext()) return node;
        }

        match = ArrayInstantiationPattern.get().match(expressionTokens);
        if(match.matched()) {
            return ArrayInstantiationRule.createNode(expressionTokens);
        }

        match = ClassInstantiationPattern.get().match(expressionTokens);
        if (match.matched()) {
            return ClassInstantiationRule.createNode(expressionTokens);
        }

        match = ArrayIndexPattern.get().match(expressionTokens);
        if (match.matched()) {
            //['characters', '[', 'characters', '.', 'length', '(', ')', '-', '1', '-', 'i', ']']
            return ArrayIndexRule.createNode(expressionTokens);
        }

        AST expression = new AST(ASTType.EXPRESSION);

        if(expressionTokens.get().type() == TokenType.LEFT_PARENTHESIS) {
            AST left = generateExpression(expressionTokens.getBlockParenthesis());
            AST right = generateExpression(expressionTokens.untilEnd());
            if(left.getType() == ASTType.EXPRESSION) left.setType(ASTType.LEFT_EXPRESSION);
            if(right.getType() == ASTType.EXPRESSION) right.setType(ASTType.RIGHT_EXPRESSION);
            expression.addChild(left);
            expression.addChild(new AST(ASTType.OPERATOR, "parenthesis"));
            expression.addChild(right);
            return expression;
        }

        if(expressionTokens.get().type().equals(TokenType.IDENTIFIER)) {
            Token next = expressionTokens.peek();
            if(next.type().equals(TokenType.INCREMENT)) {
                expression.addChild(new AST(ASTType.IDENTIFIER, expressionTokens.next().value()));
                expression.addChild(new AST(ASTType.TYPE_SPECIAL, "increment"));
                return expression;
            } else if(next.type().equals(TokenType.DECREMENT)) {
                expression.addChild(new AST(ASTType.IDENTIFIER, expressionTokens.next().value()));
                expression.addChild(new AST(ASTType.TYPE_SPECIAL, "decrement"));
                return expression;
            }
        }


        Token highestPrecedenceToken = expressionTokens.get();
        expressionTokens.reset();
        while (expressionTokens.hasNext()) {
            Token token = expressionTokens.next();

            if (getPrecedence(token.type()) > getPrecedence(highestPrecedenceToken.type())) {
                highestPrecedenceToken = token;
            }
        }
        expressionTokens.reset();

        TokenStream leftTokens = new TokenStream(expressionTokens.until(highestPrecedenceToken.type()));
        TokenStream rightTokens = new TokenStream(expressionTokens.untilEnd());
        if(leftTokens.hasNext()) {
            AST leftExp = generateExpression(leftTokens);
            if(leftExp.getType() == ASTType.EXPRESSION) leftExp.setType(ASTType.LEFT_EXPRESSION);
            else leftExp.setType(ASTType.EXPRESSION);
            expression.addChild(leftExp);
        }
        expression.addChild(new AST(ASTType.OPERATOR, highestPrecedenceToken.value()));
        if(rightTokens.hasNext()) {
            AST rightExp = generateExpression(rightTokens);
            if(rightExp.getType() == ASTType.EXPRESSION) rightExp.setType(ASTType.RIGHT_EXPRESSION);
            else rightExp.setType(ASTType.EXPRESSION);
            expression.addChild(rightExp);
        }
        return expression;
    }


    private static int getPrecedence(TokenType type) {
        switch (type) {
            case MULTIPLY:
            case DIVIDE:
            case MODULO:
                return 3; // High precedence
            case PLUS:
            case MINUS:
                return 2; // Medium precedence
            case LESS_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN:
            case GREATER_THAN_EQUALS:
            case EQUALS:
            case NOT_EQUALS:
                return 1; // Low precedence
            case LOGICAL_AND:
            case LOGICAL_OR:
                return 0; // Lowest precedence
            default:
                return -1; // Invalid or unknown token
        }
    }
}
