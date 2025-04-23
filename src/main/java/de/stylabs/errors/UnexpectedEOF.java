package de.stylabs.errors;

public class UnexpectedEOF extends RuntimeException {
    public UnexpectedEOF() {
        super("Unexpected end of file");
    }
}
