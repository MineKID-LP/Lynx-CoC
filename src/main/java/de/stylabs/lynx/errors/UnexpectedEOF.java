package de.stylabs.lynx.errors;

public class UnexpectedEOF extends RuntimeException {
    public UnexpectedEOF() {
        super("Unexpected end of file");
    }
}
