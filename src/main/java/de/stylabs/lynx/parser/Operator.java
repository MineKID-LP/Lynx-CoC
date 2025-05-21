package de.stylabs.lynx.parser;

import de.stylabs.lynx.tokenizer.Token;

public enum Operator {
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),

    EQUALS("=="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_EQUALS("<="),
    GREATER_THAN_EQUALS(">="),
    ADD_ASSIGN("+="),
    SUBTRACT_ASSIGN("-="),
    MULTIPLY_ASSIGN("*="),
    DIVIDE_ASSIGN("/="),
    MODULO_ASSIGN("%="),

    INCREMENT("++"),
    DECREMENT("--"),

    BITWISE_AND("&"),
    BITWISE_OR("|"),
    BITWISE_XOR("^"),
    BITWISE_NOT("~"),
    BITWISE_SHIFT_LEFT("<<"),
    BITWISE_SHIFT_RIGHT(">>"),
    BITWISE_SHIFT_RIGHT_UNSIGNED(">>>"),
    BITWISE_SHIFT_LEFT_UNSIGNED("<<<"),

    LOGICAL_AND("&&"),
    LOGICAL_OR("||"),

    NONE("");

    private final String matchingString;

    Operator(String s) {
        this.matchingString = s;
    }

    public static boolean isOperator(String token) {
        for (Operator operator : Operator.values()) {
            if (operator.matchingString.equalsIgnoreCase(token)) {
                return true;
            }
        }
        return false;
    }

    public static Operator fromToken(Token token) {
        for (Operator operator : Operator.values()) {
            if (operator.matchingString.equalsIgnoreCase(token.value())) {
                return operator;
            }
        }
        return NONE;
    }

    public int getPrecedence() {
        return switch (this) {
            case MULTIPLY, DIVIDE, MODULO -> 3;
            case PLUS, MINUS -> 2;
            case LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, EQUALS, NOT_EQUALS -> 1;
            case LOGICAL_AND, LOGICAL_OR -> 0;
            default -> -1;
        };
    }
}
