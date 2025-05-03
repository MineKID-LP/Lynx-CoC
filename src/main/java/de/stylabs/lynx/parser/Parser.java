package de.stylabs.lynx.parser;

import de.stylabs.lynx.errors.UnexpectedEOF;
import de.stylabs.lynx.grammar.*;
import de.stylabs.lynx.pattern.PatternMatch;
import de.stylabs.lynx.pattern.VariableDeclarationPattern;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;


public class Parser {

    public static AST generateAST(TokenStream tokenStream, AST parent) {
        AST root = (parent == null) ? new AST(ASTType.PROGRAM) : parent;

        while(tokenStream.hasNext()) {
            Token token = tokenStream.next();

            root.addChild(switch (token.type()) {
                case CLASS -> ClassDeclarationRule.createNode(tokenStream);
                case FUNCTION -> FunctionDeclarationRule.createNode(tokenStream);
                case IDENTIFIER -> decideOnWhatTheFuckThisIs(tokenStream);
                case FOR -> ForLoopRule.createNode(tokenStream);
                case RETURN -> ReturnRule.createNode(tokenStream);
                case TRY -> TryRule.createNode(tokenStream);
                case THROW -> ThrowRule.createNode(tokenStream);
                case IF -> IfRule.createNode(tokenStream);
                default -> throw new RuntimeException(String.format("Unexpected %s at: %s:%s", token.type(), token.line(), token.column()));
            });
        }

        return root;
    }

    private static AST decideOnWhatTheFuckThisIs(TokenStream tokenStream) {
        // This is a bit complex...
        // Examples:
        //  string reversed = test.reverse(input);  New Assginment
        //  reversed = "";                          Re-Assginment
        //  print("Original: " + input);            FunctionCall
        //  test.reverse(input);                    FunctionCall
        //  array[1] = 5;                           Array Assignment
        tokenStream.back(); // We actually need this token, since we don't know what it is yet
        List<Token> tokens = tokenStream.until(TokenType.SEMICOLON);
        tokenStream.back();
        tokens.add(tokenStream.next());
        TokenStream statementStream = new TokenStream(tokens);

        PatternMatch match = VariableDeclarationPattern.get().match(statementStream);
        if(match.matched()) {
            VariableDeclarationRule.createNode(new TokenStream(tokens));
        }


        return null;
    }

    public static AST generateAST(List<Token> tokens, AST parent) {
        return generateAST(new TokenStream(tokens), parent);
    }

    public static AST generateAST(List<Token> tokens) {
        return generateAST(new TokenStream(tokens), null);
    }

    private static BlockResult extractBlockTokens(List<Token> tokens, int startIndex) {
        List<Token> blockTokens = new ArrayList<>();
        int openBraces = 0;
        int endIndex = startIndex;

        for (int i = startIndex; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            blockTokens.add(token);

            if (token.type().equals(TokenType.LEFT_CURLY_BRACKET)) {
                openBraces++;
            } else if (token.type().equals(TokenType.RIGHT_CURLY_BRACKET)) {
                openBraces--;
                if (openBraces == 0) {
                    endIndex = i; // Update endIndex to the position after the closing brace
                    break;
                }
            }
        }

        if (openBraces != 0) {
            throw new UnexpectedEOF();
        }

        return new BlockResult(blockTokens, endIndex);
    }

    public static AST generateExpression(List<Token> expressionTokens) {
        return generateExpression(new TokenStream(expressionTokens));
    }

    public static AST generateExpression(TokenStream expressionTokens) {
        //TODO: Do this! LMAO GL FUTURE ME YOU LOOSER
        return new AST(ASTType.EXPRESSION);
    }
}
