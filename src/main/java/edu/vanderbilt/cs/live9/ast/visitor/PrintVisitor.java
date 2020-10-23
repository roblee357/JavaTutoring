package edu.vanderbilt.cs.live9.ast.visitor;

import edu.vanderbilt.cs.live9.ast.*;

public class PrintVisitor implements AstVisitor {

    private String indent = "";

    private void increaseIndent() {
        indent += "  ";
    }

    private void decreaseIndent() {
        indent = indent.substring(0, indent.length() - 2);
    }

    @Override
    public void visit(ExpressionNode n) {
        System.out.println(indent + "ExpressionNode");
    }

    @Override
    public void visit(LiteralNode l) {
        System.out.println(indent + "LiteralNode " + l.getValue());
    }

    @Override
    public void visit(LParenNode l) {
        increaseIndent();
        System.out.println(indent + "LParenNode (");
    }

    @Override
    public void visit(RParenNode r) {
        System.out.println(indent + "RParenNode )");
        decreaseIndent();
    }
}
