package edu.vanderbilt.cs.live9.ast;

public class LParenNode implements Node{
    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
