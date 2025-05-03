package de.stylabs.lynx.util;

public class Character {
    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    public static boolean isUnderscore(char c) {
        return c == '_';
    }

    public static boolean isBracket(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']';
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '!' || c == '<' || c == '>';
    }

    public static boolean isStringDelimiter(char c) {
        return c == '"' || c == '\'';
    }

    public static boolean isPunctuation(char c) {
        return c == ',' || c == ';' || c == ':' || c == '.' || c == '?' || c == '!';
    }

    public static boolean isSymbol(char c) {
        return c == '&' || c == '|' || c == '!' || c == '=' || c == '^' || c == '~' || c == '<' || c == '>' ||
                c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' ||
                c == '.' || c == ',' || c == ':' || c == '?';
    }

    public static boolean isNewline(char c) {
        return c == '\n' || c == '\r';
    }

    public static boolean isSlash(char c) {
        return c == '/' || c == '\\';
    }
}
