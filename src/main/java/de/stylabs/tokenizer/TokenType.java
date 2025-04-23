package de.stylabs.tokenizer;

public enum TokenType {
    STRING_LITERAL(""),
    NUMBER_LITERAL(""),

    IDENTIFIER(""),
    OPERATOR(""),

    TYPE(""),

    LEFT_BRACKET("("),
    RIGHT_BRACKET(")"),
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
    CLASS("class"),

    INT("int"),
    FLOAT("float"),
    STRING("string"),
    BOOL("bool"),
    VOID("void"),
    CHAR("char"),

    THIS("this"),
    NEW("new"),
    PUBLIC("public"),
    PRIVATE("private"),

    TRUE("true"),
    FALSE("false"),

    THROW("throw"),

    ;

    private final String matchingString;

    TokenType(String s) {
        this.matchingString = s;
    }

    public static TokenType matchOperator(String string) {
        return switch(string) {
            case "+", "-", "*", "/", "%", "==", "!=", "<", ">", "<=", ">=" -> OPERATOR;
            default -> IDENTIFIER;
        };
    }

    public static TokenType match(String string) {
        for (TokenType type : TokenType.values()) {
            if (type.matchingString.equalsIgnoreCase(string)) {
                return type;
            }
        }
        return matchOperator(string);
    }

    public static TokenType matchBracket(char c) {
        return switch(c) {
            case '(', ')' -> LEFT_BRACKET;
            case '[', ']' -> LEFT_SQUARE_BRACKET;
            case '{', '}' -> LEFT_CURLY_BRACKET;
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
}
