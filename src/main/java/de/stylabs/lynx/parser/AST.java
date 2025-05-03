package de.stylabs.lynx.parser;

import de.stylabs.lynx.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class AST {
    private ASTType type;
    private String value;
    private List<AST> children;
    private TokenType tokenType;

    public AST(ASTType type) {
        this.type = type;
        this.children = new ArrayList<>();
    }

    public AST(ASTType type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public AST(ASTType type, TokenType tokenType, String value) {
        this.type = type;
        this.value = value;
        this.tokenType = tokenType;
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

    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\": \"").append(type).append("\", ");
        if (value != null) {
            json.append("\"value\": \"").append(value).append("\", ");
        }
        if (tokenType != null) {
            json.append("\"tokenType\": \"").append(tokenType).append("\", ");
        }
        json.append("\"children\": [");
        for (int i = 0; i < children.size(); i++) {
            json.append(children.get(i).toJSON());
            if (i < children.size() - 1) {
                json.append(", ");
            }
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }

    public void setType(ASTType type) {
        this.type = type;
    }
}
