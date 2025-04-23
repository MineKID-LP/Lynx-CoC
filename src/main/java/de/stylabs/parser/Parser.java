package de.stylabs.parser;

import de.stylabs.grammar.*;
import de.stylabs.tokenizer.Token;
import de.stylabs.tokenizer.TokenAcceptor;
import de.stylabs.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;


public class Parser {
    private static List<GrammarRule> grammarRules = List.of(
            new ClassDeclarationRule(),
            new FunctionDeclarationRule(),
            new VariableDeclarationRule(),
            new VariableAssignmentRule()
    );

    public static AST generateAST(List<Token> tokens) throws Throwable {
        AST root = new AST(ASTType.PROGRAM);

        int index = 0;
        while (index < tokens.size()) {
            boolean matched = false;

            for (GrammarRule rule : grammarRules) {
                List<Token> subList = tokens.subList(index, tokens.size());
                if (matchesInitialSequence(rule, subList)) {
                    // Create the node for the matched rule
                    AST node = rule.createNode(subList);
                    index += rule.getTokenSequence().size(); // Skip matched tokens

                    // If the rule allows children, process the block
                    if (rule.canHaveChildren()) {
                        List<Token> blockTokens = extractBlockTokens(tokens.subList(index, tokens.size()));
                        AST childAST = generateAST(blockTokens); // Recursively parse the block
                        node.addChild(childAST);
                        index += blockTokens.size(); // Skip block tokens
                    }

                    root.addChild(node);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                index++; // Skip unmatched tokens
            }
        }

        return root;
    }

    private static List<Token> extractBlockTokens(List<Token> tokens) {
        List<Token> blockTokens = new ArrayList<>();
        int openBraces = 0;

        for (Token token : tokens) {
            blockTokens.add(token);

            if (token.value().equals("{")) {
                openBraces++;
            } else if (token.value().equals("}")) {
                openBraces--;
                if (openBraces == 0) {
                    break; // End of the block
                }
            }
        }

        return blockTokens;
    }

    private static boolean matchesInitialSequence(GrammarRule rule, List<Token> tokens) {
        List<Object> sequence = rule.getTokenSequence();
        if (tokens.size() < sequence.size()) {
            return false;
        }

        for (int i = 0; i < sequence.size(); i++) {
            Object o = sequence.get(i);
            if (o instanceof TokenType) {
                if (!tokens.get(i).type().equals(o)) {
                    return false;
                }
            } else if (o instanceof TokenAcceptor) {
                if(!((TokenAcceptor) o).accepts(tokens.get(i))) {
                    return false;
                }
            } else {
                throw new IllegalArgumentException("Unsupported type in token sequence");
            }
        }

        return true;
    }
}
