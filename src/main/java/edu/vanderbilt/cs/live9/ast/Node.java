package edu.vanderbilt.cs.live9.ast;

public interface Node {

    public void accept(AstVisitor visitor);
}
