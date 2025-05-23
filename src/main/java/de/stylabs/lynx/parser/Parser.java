package de.stylabs.lynx.parser;

import de.stylabs.lynx.errors.UnexpectedEOF;
import de.stylabs.lynx.grammar.*;
import de.stylabs.lynx.pattern.*;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.List;

import static de.stylabs.lynx.LynxCompiler.print;
import static java.util.Objects.isNull;

public class Parser {

    public static AST generateAST(TokenStream tokenStream, AST parent) {
        AST root = (parent == null) ? new AST(ASTType.PROGRAM) : parent;

        while (tokenStream.hasNext()) {
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
                case COMMENT -> null; // FUCK YOU JAVA AND YOUR SHITTY REGEX IMPLEMENTATION 🖕
                default -> throw new RuntimeException(
                        String.format("Unexpected %s at: %s:%s", token.type(), token.line(), token.column()));
            };

            if (!isNull(child))
                root.addChild(child);
        }

        return root;
    }

    // This is maybe not the best way to do this, but I don't really care rn
    // TODO: FIX
    private static AST testTernary(TokenStream tokenStream) {
        int peekCounter = 1;

        while (peekCounter < tokenStream.size()) {
            if (tokenStream.peek(peekCounter).type().equals(TokenType.RIGHT_PARENTHESIS)) {
                if (tokenStream.peek(peekCounter + 1).type().equals(TokenType.QUESTION)) {
                    tokenStream.back();
                    return TernaryExpressionRule.createNode(tokenStream);
                }
            }
            peekCounter++;
        }

        throw new RuntimeException(String.format("Unexpected %s at: %s:%s", tokenStream.get().type(),
                tokenStream.get().line(), tokenStream.get().column()));
    }

    private static AST decideOnWhatTheFuckThisIs(TokenStream tokenStream) {
        tokenStream.back(); // We actually need this token, since we don't know what it is yet

        List<Token> tokens = tokenStream.until(TokenType.SEMICOLON);
        tokenStream.back();
        tokens.add(tokenStream.next());
        TokenStream statementStream = new TokenStream(tokens);

        PatternMatch match = VariableDeclarationPattern.get().match(statementStream);
        if (match.matched()) {
            return VariableDeclarationRule.createNode(new TokenStream(tokens));
        }

        match = VariableAssignmentPattern.get().match(statementStream);
        if (match.matched()) {
            return VariableAssignmentRule.createNode(new TokenStream(tokens));
        }

        match = FunctionCallPattern.get().match(statementStream);
        if (match.matched()) {
            return FunctionCallRule.createNode(new TokenStream(tokens));
        }

        throw new RuntimeException(String.format("Unexpected '%s' at: %s:%s", tokenStream.get().value(),
                tokenStream.get().line(), tokenStream.get().column()));
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
                return new AST(ASTType.VARIABLE_GET, token.value());
            } else if (token.type() == TokenType.NUMBER_LITERAL) {
                return new AST(ASTType.NUMBER_LITERAL, token.value());
            } else if (token.type() == TokenType.STRING_LITERAL) {
                return new AST(ASTType.STRING_LITERAL, token.value());
            } else if (token.type() == TokenType.TRUE || token.type() == TokenType.FALSE) {
                return new AST(ASTType.BOOLEAN, token.value());
            }
            throw new RuntimeException(
                    String.format("Unexpected '%s' at: %s:%s", token.value(), token.line(), token.column()));
        }

        if (expressionTokens.size() == 2) {
            Token token = expressionTokens.get();
            Token next = expressionTokens.peek();

            // This is the most readable way to do this, at least what I can think of rn.
            if (token.type().equals(TokenType.IDENTIFIER)) {
                switch (next.type()) {
                    case INCREMENT, DECREMENT -> {
                        AST node = new AST(ASTType.UNARY_EXPRESSION);
                        node.addChild(new AST(ASTType.VARIABLE_GET, token.value()));
                        node.addChild(new AST(ASTType.UNARY_OPERATOR, next.type().toString()));
                        return node;
                    }
                }
            }

        }

        PatternMatch match = FunctionCallPattern.get().match(expressionTokens);
        if (match.matched()) {
            // Get rid of standalone function calls
            AST node = FunctionCallRule.createNode(expressionTokens);
            if (!expressionTokens.hasNext())
                return node;
        }
        match = ArrayInstantiationPattern.get().match(expressionTokens);
        if (match.matched()) {
            AST node = ArrayInstantiationRule.createNode(expressionTokens);
            // Only if its consumed whole
            if (!expressionTokens.hasNext())
                return node;
        }
        match = ArrayIndexPattern.get().match(expressionTokens);
        if (match.matched()) {
            AST node = ArrayIndexRule.createNode(expressionTokens);
            if (!expressionTokens.hasNext())
                return node;
        }
        match = TernaryExpressionPattern.get().match(expressionTokens);
        if (match.matched()) {
            AST node = TernaryExpressionRule.createNode(expressionTokens);
            if (!expressionTokens.hasNext())
                return node;
        }
        match = ClassInstantiationPattern.get().match(expressionTokens);
        if (match.matched()) {
            AST node = ClassInstantiationRule.createNode(expressionTokens);
            if (!expressionTokens.hasNext())
                return node;
        }


        expressionTokens.reset();
        return MathParser.parse(expressionTokens);
    }
}
