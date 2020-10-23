package edu.vanderbilt.cs.live9.ast;


public interface AstVisitor {

    public void visit(ExpressionNode n);

    public void visit(LiteralNode l);

    public void visit(LParenNode l);

    public void visit(RParenNode r);

}
