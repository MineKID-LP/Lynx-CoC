package de.stylabs.parser;

import java.util.ArrayList;
import java.util.List;

public class AST {
    private ASTType type;
    private String value;
    private List<AST> children;

    public AST(ASTType type) {
        this.type = type;
        this.children = new ArrayList<>();
    }

    public AST(ASTType type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(AST child) {
        this.children.add(child);
    }

    public List<AST> getChildren() {
        return children;
    }

    public ASTType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
