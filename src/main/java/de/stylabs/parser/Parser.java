package de.stylabs.parser;

import de.stylabs.errors.UnexpectedEOF;
import de.stylabs.errors.UnexpectedToken;
import de.stylabs.grammar.*;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenAcceptor;
import de.stylabs.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static de.stylabs.LynxCompiler.print;


public class Parser {
    private static final List<GrammarRule> grammarRules = List.of(
            new ClassDeclarationRule(),
            new FunctionDeclarationRule(),
            new VariableDeclarationRule(),
            new VariableAssignmentRule(),
            new VoidFunctionDeclarationRule()
    );

    public static AST generateAST(List<Token> tokens) {
        AST root = new AST(ASTType.PROGRAM);

        int index = 0;
        while (index < tokens.size()) {
            boolean matched = false;

            for (GrammarRule rule : grammarRules) {
                List<Token> subList = tokens.subList(index, tokens.size());
                if (matchesInitialSequence(rule, subList)) {
                    AST node = rule.createNode(subList);
                    index += rule.getTokenSequence().size();

                    // Make sure to not skip the left curly bracket in order to not fk up extractBlockTokens
                    if(rule.getTokenSequence().getLast().equals(TokenType.LEFT_CURLY_BRACKET)) {
                        index--;
                    } else if (rule.getTokenSequence().getLast() instanceof TokenAcceptor acceptor) {
                        if (acceptor.getAcceptedTypes().contains(TokenType.LEFT_CURLY_BRACKET)) {
                            index--;
                        }
                    }

                    // If the rule allows children, process the block
                    if (rule.canHaveChildren()) {
                        BlockResult blockResult = extractBlockTokens(tokens, index);
                        AST childAST = generateAST(blockResult.tokens());
                        node.addChild(childAST);
                        index = blockResult.endIndex();
                    }

                    root.addChild(node);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                index++;
                //throw new UnexpectedToken(tokens.get(index));
            }
        }

        return root;
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
                    endIndex = i + 1; // Update endIndex to the position after the closing brace
                    break;
                }
            }
        }

        if (openBraces != 0) {
            throw new UnexpectedEOF();
        }

        return new BlockResult(blockTokens, endIndex);
    }

    private static boolean matchesInitialSequence(GrammarRule rule, List<Token> tokens) {
        List<Object> sequence = rule.getTokenSequence();
        if (tokens.size() < sequence.size()) {
            return false;
        }

        int matchedCount = 0;

        for (int i = 0; i < sequence.size(); i++) {
            Object expected = sequence.get(i);
            Token actual = tokens.get(matchedCount);

            if (expected instanceof TokenType) {
                if (!actual.type().equals(expected)) {
                    reportMismatch(rule, tokens, i, matchedCount, "TYPE");
                    return false;
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

        return true;
    }

    private static void reportMismatch(GrammarRule rule, List<Token> tokens, int failedIndex, int matchedCount, String cause) {
        if (matchedCount > 0) {
            List<String> expectedTypes = new ArrayList<>();
            for (int i = 0; i < rule.getTokenSequence().size(); i++) {
                if(rule.getTokenSequence().get(i) instanceof TokenType expectedType) {
                    expectedTypes.add(expectedType.toString());
                } else if (rule.getTokenSequence().get(i) instanceof TokenAcceptor acceptor) {
                    acceptor.getAcceptedTypes().forEach(type -> expectedTypes.add(type.toString()));
                }
            }

            List<String> actualTypes = new ArrayList<>();
            for (int i = 0; i < rule.getTokenSequence().size(); i++) {
                actualTypes.add(tokens.get(i).type().toString());
            }

            StringBuilder error = new StringBuilder();
            error.append("Token sequence mismatch in rule: ").append(rule.getClass().getSimpleName()).append("\n");
            error.append("Expected sequence: ").append(expectedTypes).append("\n");
            error.append("Actual tokens: ").append(actualTypes).append("\n");
            error.append("Failed at index ").append(failedIndex).append(": ");
            error.append("Expected ").append(rule.getTokenSequence().get(failedIndex)).append(", ");
            error.append("but got ").append(tokens.get(matchedCount).type()).append("\n");
            error.append("Matched ").append(matchedCount).append(" token(s) before failing.")
            .append("\nCause: ").append(cause).append("\n");

            print(error.toString());
        }
    }
}
