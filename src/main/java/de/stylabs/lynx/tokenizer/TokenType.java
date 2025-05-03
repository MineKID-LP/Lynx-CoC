package de.stylabs.lynx.tokenizer;

public enum TokenType {
    STRING_LITERAL(""),
    NUMBER_LITERAL(""),

    IDENTIFIER(""),
    OPERATOR(""),

    TYPE(""),

    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_SQUARE_BRACKET("["),
    RIGHT_SQUARE_BRACKET("]"),
    LEFT_CURLY_BRACKET("{"),
    RIGHT_CURLY_BRACKET("}"),

    SEMICOLON(";"),
    DOT("."),
    COMMA(","),
    COLON(":"),
    EXCLAMATION("!"),
    QUESTION("?"),

    IF("if"),
    ELSE("else"),

    WHILE("while"),
    FOR("for"),

    RETURN("return"),

    FUNCTION("function"),
    ARROW("->"),
    CLASS("class"),

    THIS("this"),
    NEW("new"),
    PUBLIC("public"),
    PRIVATE("private"),

    TRUE("true"),
    FALSE("false"),

    THROW("throw"),

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

    ASSIGN("="),

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

    NULL("null"),

    TRY("try"),
    CATCH("catch"),
    FINALLY("finally"),
    COMMENT("");

    private final String matchingString;

    TokenType(String s) {
        this.matchingString = s;
    }

    public static TokenType match(String string) {
        for (TokenType type : TokenType.values()) {
            if (type.matchingString.equalsIgnoreCase(string)) {
                return type;
            }
        }
        return TokenType.IDENTIFIER;
    }

    public static TokenType matchBracket(char c) {
        return switch(c) {
            case '(' -> LEFT_PARENTHESIS;
            case ')' -> RIGHT_PARENTHESIS;
            case '[' -> LEFT_SQUARE_BRACKET;
            case ']' -> RIGHT_SQUARE_BRACKET;
            case '{' -> LEFT_CURLY_BRACKET;
            case '}' -> RIGHT_CURLY_BRACKET;
            default -> IDENTIFIER;
        };
    }

    public static TokenType matchPunctuation(char c) {
        return switch(c) {
            case ';' -> SEMICOLON;
            case '.' -> DOT;
            case ',' -> COMMA;
            case ':' -> COLON;
            case '!' -> EXCLAMATION;
            case '?' -> QUESTION;
            default -> IDENTIFIER;
        };
    }

    public static String aliasof(TokenType tokenType) {
        return (tokenType.matchingString.isEmpty()) ? tokenType.name() : tokenType.matchingString;
    }
}
