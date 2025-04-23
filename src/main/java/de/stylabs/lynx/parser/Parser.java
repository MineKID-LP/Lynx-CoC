package de.stylabs.lynx.parser;

import de.stylabs.lynx.errors.UnexpectedEOF;
import de.stylabs.lynx.errors.UnexpectedToken;
import de.stylabs.lynx.grammar.*;
import de.stylabs.lynx.tokenizer.Token;
import de.stylabs.lynx.tokenizer.TokenAcceptor;
import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static de.stylabs.lynx.LynxCompiler.print;

public class Parser {
    private static final List<GrammarRule> grammarRules = List.of(
            new ClassDeclarationRule(),
            new FunctionDeclarationRule(),
            new VariableDeclarationRule(),
            new VariableAssignmentRule(),
            new VoidFunctionDeclarationRule());

    public static AST generateAST(List<Token> tokens, AST parent) {
        AST root = (parent == null) ? new AST(ASTType.PROGRAM) : parent;

        int index = 0;
        while (index < tokens.size()) {
            boolean matched = false;
            for (GrammarRule rule : grammarRules) {
                List<Token> subList = tokens.subList(index, tokens.size());
                SequenceMatch match = matchesInitialSequence(rule, subList);
                if (match.success()) {
                    matched = true;
                    AST node = rule.createNode(subList);
                    index += match.matchedAmount();

                    // If the rule allows children, process the block
                    if (rule.canHaveChildren()) {
                        BlockResult blockResult = extractBlockTokens(subList, index);
                        List<Token> subTokens = blockResult.tokens();
                        subTokens = subTokens.subList(1, subTokens.size() - 1); // Remove surrounding braces
                        generateAST(subTokens, node); // Pass the current node as parent
                        index = blockResult.endIndex() + 1; // Move index past the block
                    }

                    root.addChild(node);
                    break;
                }
            }

            if (!matched) {
                throw new UnexpectedToken(tokens.get(index + 1));
            }
        }

        return root;
    }

    public static AST generateAST(List<Token> tokens) {
        return generateAST(tokens, null);
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

    private static SequenceMatch matchesInitialSequence(GrammarRule rule, List<Token> tokens) {
        List<Object> sequence = rule.getTokenSequence();
        if (tokens.size() < sequence.size()) {
            print("Too few tokens");
            return new SequenceMatch(false, 0);
        }

        int matchedCount = 0;

        for (int i = 0; i < sequence.size(); i++) {
            if (matchedCount >= tokens.size()) {
                break;
            }
            Object expected = sequence.get(i);
            Token actual = tokens.get(matchedCount);

            if (expected instanceof TokenType) {
                if (!actual.type().equals(expected)) {
                    reportMismatch(rule, tokens, i, matchedCount, "TYPE");
                    return new SequenceMatch(false, 0);
                }
            } else if (expected instanceof TokenAcceptor acceptor) {
                if (acceptor.accepts(actual)) {
                    i--;
                } else {
                    i++;
                }
            } else {
                throw new IllegalArgumentException("Unsupported type in token sequence at index " + i);
            }

            matchedCount++;
        }
        return new SequenceMatch(true, matchedCount);
    }

    private static void reportMismatch(GrammarRule rule, List<Token> tokens, int failedIndex, int matchedCount,
            String cause) {
        List<String> expectedTypes = new ArrayList<>();
        for (int i = 0; i < rule.getTokenSequence().size(); i++) {
            if (rule.getTokenSequence().get(i) instanceof TokenType expectedType) {
                expectedTypes.add(expectedType.toString());
            } else if (rule.getTokenSequence().get(i) instanceof TokenAcceptor acceptor) {
                acceptor.getAcceptedTypes().forEach(type -> expectedTypes.add(type.toString()));
            }
        }

        List<String> actualTypes = new ArrayList<>();
        for (int i = 0; i < rule.getTokenSequence().size(); i++) {
            actualTypes.add(tokens.get(i).type().toString() + "(" + tokens.get(i).value().toString() + ")");
        }

        StringBuilder error = new StringBuilder();
        error.append("Token sequence mismatch in rule: ").append(rule.getClass().getSimpleName()).append("\n");
        error.append("Expected sequence: ").append(expectedTypes).append("\n");
        error.append("Actual tokens: ").append(actualTypes).append("\n");
        error.append("Failed at index ").append(failedIndex).append(": ");
        error.append("Expected ").append(rule.getTokenSequence().get(failedIndex)).append(", ");
        error.append("but got ").append(tokens.get(matchedCount).type()).append("\n");
        error.append("Matched ").append(matchedCount).append(" token(s) before failing.\n");
        error.append("Cause: ").append(cause).append("\n");

        print(error.toString());
    }
}
