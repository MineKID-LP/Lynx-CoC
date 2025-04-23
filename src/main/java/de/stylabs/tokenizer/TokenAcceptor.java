package de.stylabs.tokenizer;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

public class TokenAcceptor {
    private final List<TokenType> acceptedTypes;
    private boolean conditionReached = false;
    private TokenType untilType = null;

    private TokenAcceptor(List<TokenType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    public static TokenAcceptor of(TokenType... types) {
        return new TokenAcceptor(Arrays.asList(types));
    }

    public static Object ofVariableAssignment() {
        return TokenAcceptor.of(
                TokenType.NUMBER_LITERAL,
                TokenType.STRING_LITERAL,

                TokenType.IDENTIFIER,

                TokenType.LEFT_SQUARE_BRACKET,
                TokenType.RIGHT_SQUARE_BRACKET,
                TokenType.LEFT_PARENTHESIS,
                TokenType.RIGHT_PARENTHESIS,

                TokenType.COMMA,

                TokenType.BITWISE_OR,
                TokenType.BITWISE_AND,
                TokenType.BITWISE_XOR,
                TokenType.BITWISE_NOT,
                TokenType.BITWISE_SHIFT_LEFT,
                TokenType.BITWISE_SHIFT_RIGHT,
                TokenType.BITWISE_SHIFT_RIGHT_UNSIGNED,
                TokenType.BITWISE_SHIFT_LEFT_UNSIGNED,

                TokenType.LOGICAL_AND,
                TokenType.LOGICAL_OR,

                TokenType.EXCLAMATION,

                TokenType.PLUS,
                TokenType.MINUS,
                TokenType.MULTIPLY,
                TokenType.DIVIDE,
                TokenType.MODULO,

                TokenType.DECREMENT,
                TokenType.INCREMENT,

                TokenType.EQUALS,
                TokenType.NOT_EQUALS,

                TokenType.GREATER_THAN,
                TokenType.GREATER_THAN_EQUALS,
                TokenType.LESS_THAN,
                TokenType.LESS_THAN_EQUALS,

                TokenType.QUESTION,
                TokenType.THIS,

                TokenType.TRUE,
                TokenType.FALSE,

                TokenType.NULL
        ).until(TokenType.SEMICOLON);
    }

    //Look to FunctionDeclarationRule for example usage
    public TokenAcceptor until(TokenType type) {
        this.untilType = type;
        return this;
    }

    // Accept tokens until the specified type is reached
    public boolean accepts(Token token) {
        if (!isNull(untilType)) {
            if (token.type() == untilType) {
                conditionReached = true;
                return true;
            }
            if (conditionReached) {
                return false;
            }
        }
        return acceptedTypes.contains(token.type());
    }


    public List<TokenType> getAcceptedTypes() {
        return acceptedTypes;
    }

    @Override
    public String toString() {
        return "TokenAcceptor{" +
                "acceptedTypes=" + acceptedTypes +
                ", conditionReached=" + conditionReached +
                ", untilType=" + untilType +
                '}';
    }
}
